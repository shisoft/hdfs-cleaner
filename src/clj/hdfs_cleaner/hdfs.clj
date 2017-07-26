(ns hdfs-cleaner.hdfs
  (:require [cluster-connector.utils.for-debug :refer [$ spy]])
  (:import (com.factual.hdfs_cleaner HDFS)
           (org.apache.hadoop.fs Path)))

(defn scan [^String path]
  (let [start-time (System/currentTimeMillis)]
    {:result ($ into [] (HDFS/scan (Path. (str hdfs-cleaner.config/hdfs-server path))))
     :start-time start-time
     :end-time (System/currentTimeMillis)}))