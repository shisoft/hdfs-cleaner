(ns hdfs-cleaner.hdfs
  (:require [cluster-connector.utils.for-debug :refer [$ spy]])
  (:import (com.factual.hdfs_cleaner HDFS)
           (org.apache.hadoop.fs Path FileStatus)))

(defn scan* [^Path path]
  (map
    (fn [^FileStatus file]
      (merge {:name (-> file (.getPath) (.getName))
              :path (-> file (.getPath) (.toString))
              :is-dir (.isDirectory file)
              :last-modified (.getModificationTime file)
              :replication (.getReplication file)}
             (try
               (let [sub-files (when (.isDirectory file) (scan* (.getPath  file)))]
                 {:sub-files sub-files
                  :size (if sub-files
                          (reduce + (map :size sub-files))
                          (.getLen file))})
               (catch Exception e
                 {:has-error true}))))
    (.listStatus HDFS/dfs path)))

(defn scan [^String path]
  (let [start-time (System/currentTimeMillis)]
    {:result (scan* (Path. path))
     :start-time start-time
     :end-time (System/currentTimeMillis)}))