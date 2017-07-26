(ns hdfs-cleaner.hdfs
  (:import (com.factual.hdfs_cleaner HDFS)
           (org.apache.hadoop.fs Path)))

(defn scan [^String path]
  (into [] (HDFS/scan (Path. (str hdfs-cleaner.config/hdfs-server path)))))