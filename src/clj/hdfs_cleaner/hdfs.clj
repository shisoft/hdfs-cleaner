(ns hdfs-cleaner.hdfs
  (:require [cluster-connector.utils.for-debug :refer [$ spy]]
            [cluster-connector.native-cache.core :refer [defcache]]
            [taoensso.timbre :as log])
  (:import (com.factual.hdfs_cleaner HDFS)
           (org.apache.hadoop.fs Path FileStatus)))

(def shallow-pattern [])

(defn get-dir-size [^Path path]
  (-> HDFS/dfs
      (.getContentSummary path)
      (.getLength)))

(defn path-pattern-matching [^Path path-pattern]
  (-> HDFS/dfs
      (.globStatus path-pattern)))

(defn scan** [^Path path depth max-depth]
  (->> (map
         (fn [^FileStatus file]
           (let [path (.getPath file)
                 name (-> path (.getName))]
             (when-not
               (.startsWith name ".")
               (merge {:name (-> path (.getName))
                       :is_dir (.isDirectory file)
                       :last_modified (.getModificationTime file)
                       :replication (.getReplication file)}
                      (try
                        (if (and (.isDirectory file)
                                 (or (and depth max-depth (>= depth max-depth))))
                          {:size (get-dir-size path)}
                          (let [sub-files (when (.isDirectory file)
                                            (scan** path (inc depth) max-depth))]
                            {:children sub-files
                             :size (if sub-files
                                     (reduce + (map :size sub-files))
                                     (.getLen file))}))
                        (catch Exception e
                          (log/error e "Scan failed for dir:" (-> path (.toString)))
                          {:has_error true}))))))
         (.listStatus HDFS/dfs path))
       (filter identity)))

(defn scan* [^String path max-depth]
  (let [start-time (System/currentTimeMillis)]
    {:result (scan** (Path. path) 0 max-depth)
     :start_time start-time
     :end_time (System/currentTimeMillis)}))

(defcache
  scan {:expire-after-write-secs (* 5 1000)}
  (fn [[path max-depth]] (future (scan* path max-depth))))