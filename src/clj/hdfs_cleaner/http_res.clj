(ns hdfs-cleaner.http-res
  (:require [manifold.deferred :as d]))

(defn error [status & msgs]
  {:status status
   :body {(cond
            (and (< status 600) (>= status 400)) :error
            :else :message)
          (clojure.string/join " " (map str msgs))}})

(defn response
  [res]
  (d/chain
    res
    (fn [res]
      (if (not-every? identity ((juxt :status :body) res))
        {:status  200
         :body    res}
        res))))

(defn not-implemented []
  {:status 501
   :body {:error "Service Not Implemented. It shoud be done but not available right now."}})