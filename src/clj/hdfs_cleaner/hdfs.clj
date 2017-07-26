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

(defn scan** [^Path path & [depth max-depth]]
  (map
    (fn [^FileStatus file]
      (let [path (.getPath file)]
        (merge {:name (-> path (.getName))
                :is-dir (.isDirectory file)
                :last-modified (.getModificationTime file)
                :replication (.getReplication file)}
               (try
                 (if (and (.isDirectory file)
                          (or (and depth max-depth (>= depth max-depth))))
                   {:size (get-dir-size path)
                    :sub-files "..."}
                   (let [sub-files (scan** path depth max-depth)]
                     {:sub-files sub-files
                      :size (if sub-files
                              (reduce + (map :size sub-files))
                              (.getLen file))}))
                 (catch Exception e
                   (log/error e "Scan failed for dir:" (-> path (.toString)))
                   {:has-error true})))))
    (.listStatus HDFS/dfs path)))

(defn scan* [^String path]
  (let [start-time (System/currentTimeMillis)]
    {:result (scan** (Path. path))
     :start-time start-time
     :end-time (System/currentTimeMillis)}))

(defcache
  scan {:expire-after-write-secs (* 5 1000)}
  (fn [path] (future (scan* path))))