(ns hdfs-cleaner.config
  (:require [dscribe-logger.combo :as log]
            [cheshire.core :as json]))

(def ^:const config-file "config.json")

(defn- load-config
  [cfg-file]
  (try
    (-> cfg-file
        slurp
        (json/decode true))
    (catch Exception e
      (log/error e "[config] Failed to load config file" cfg-file))))

(def config (load-config config-file))
(def dscribe-server (:dscribe-server config))
(def dscribe-app (:dscribe-app config))
(def log-level (:log-level config))
(def hdfs-server (:hdfs-server config))