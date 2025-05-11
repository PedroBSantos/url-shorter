(ns url-shorter-api.core
  (:gen-class)
  (:require [url-shorter-api.web.server :as s]))

(defn -main
  [& _]
  (s/run-server 8080 false))
