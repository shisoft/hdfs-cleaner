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
                       :path (-> path (.toUri) (.getRawPath))
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

(def one-TB (* 1024 1024 1024 1024))
(def user-dir "/user")

(defn bytes->human-readable [bytes & [si?]]
  (let [unit (if si? 1000 1024)]
    (if (< bytes unit) (str bytes " B")
                       (let [exp (int  (/ (Math/log bytes)
                                          (Math/log unit)))
                             pre (str (nth (if si? "kMGTPE" "KMGTPE") (dec exp)) (if-not si? "i" ))]
                         (format "%.1f %sB" (/ bytes (Math/pow unit exp)) pre)))))

(defn scan-size [^Path path]
  (->> (try
         (.listStatus HDFS/dfs path)
         (catch Exception e 0))
       (map
         (fn [file]
           (try
             (let [path (.getPath file)]
               (if (.isDirectory file)
                 (scan-size path)
                 (.getLen file)))
             (catch Exception e 0))))
       (reduce +)))

(defn scan-alert-users []
  (let [path (Path. ^String user-dir)]
    (log/info "Start scan")
    (doseq [user-dir (.listStatus HDFS/dfs path)]
      (let [user-path (.getPath user-dir)
            user-name (.getName user-path)
            _ (log/info "scan" user-name)
            size (scan-size user-path)
            size-readable (bytes->human-readable size)]
        (when (>= size one-TB)
          (log/info "User:" user-name "use" size-readable))))))