(ns hdfs-cleaner.core
  (:require [taoensso.timbre :as timbre]
            [dscribe-logger.combo :as log]
            [dscribe-logger.logger :as lm]
            [hdfs-cleaner.config :as conf]
            [hdfs-cleaner.http-res :as http-res]
            [hdfs-cleaner.hdfs :as hdfs]
            [manifold.deferred :as d]
            [cheshire.core :as json]
            [ring.util.response :refer [content-type]]
            [ring.middleware.params :as params]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [aleph.http :as http]
            [clojure.core.async :as a]
            [cluster-connector.utils.for-debug :refer :all]
            [cemerick.pomegranate :as cp])
  (:import (com.factual.hdfs_cleaner HDFS)
           (org.apache.hadoop.conf Configuration)))

(def log-level (or (keyword (System/getenv "LOG_LEVEL"))
                   (keyword conf/log-level)
                   :info))

(println "LOG LEVEL SET TO:" (name log-level))
(timbre/set-level! log-level)
(when (and (not *compile-files*) conf/dscribe-server conf/dscribe-app) ;; prevent compile time hang up and init without server address
  (lm/init-logger {:server conf/dscribe-server :app conf/dscribe-app :task "hdfs-backend"}))


(def app-routes
  (routes
    (GET ["/scan:path", :path #"\/.+"] {{hdfs-path :path} :route-params
                                        {max-depth :max-depth} :query-params}
      (http-res/response (hdfs/scan [hdfs-path max-depth])))
    (GET "/ping" {}
      (http-res/response "pong"))
    (route/not-found (http-res/error 404 "Not Found"))))


(defn encode-response-to-json [response]
  (d/chain (:body response)
           (fn [body]
             (if (coll? body)
               (let [json-response (assoc response :body (json/generate-string body))]
                 (if (contains? (:headers response) "Content-Type")
                   json-response
                   (content-type json-response "application/json; charset=utf-8")))
               response))
           (fn [response]
             (update response :headers dissoc "transfer-encoding"))))

(def app
  (params/wrap-params
    (fn [req]
      (log/debug {:type "request" :content req})
      (d/chain
        (-> req
            (app-routes)
            (d/catch
              (fn [e]
                (log/error e "Error on tracking")
                (http-res/error 500 (.getMessage e)))))
        encode-response-to-json))))

(def ^:private ^:const aleph-config
  {:port 4050
   :max-header-size 65535
   :max-initial-line-length 65535})

(defn config-classpath []
  (do (cp/add-classpath
        "/etc/hadoop/conf/"
        (.getClassLoader Configuration))
      (Configuration.)))

(defn -main
  [& args]
  (log/info "[startup] initialize HDFS")
  (config-classpath)
  (HDFS/init)
  (log/info "[startup] Starting HTTP server")
  (http/start-server app aleph-config)
  (log/info "[startup] HDFS cleaner is ready")
  (a/<!! (a/chan)))

