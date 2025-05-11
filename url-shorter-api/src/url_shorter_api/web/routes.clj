(ns url-shorter-api.web.routes 
  (:require
   [compojure.core :refer [defroutes POST]]
   [compojure.route :as route]
   [url-shorter-api.web.handlers :as h]))

(defroutes app-routes
  (POST "/urls/short-url" [] h/short-url-handler)
  (route/not-found "Path Not Found"))