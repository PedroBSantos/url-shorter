(ns url-shorter-api.web.routes 
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [compojure.route :as route]
   [url-shorter-api.web.handlers :as h]))

(defroutes app-routes
  (POST "/urls/short-url" [] h/short-url-handler)
  (GET "/health" [] h/health-check-handler)
  (route/not-found "Path Not Found"))