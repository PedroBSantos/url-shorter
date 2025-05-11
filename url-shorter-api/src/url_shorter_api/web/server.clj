(ns url-shorter-api.web.server
  (:require
   [url-shorter-api.web.routes :refer [app-routes]]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.logger :refer [wrap-log-request-params
                        wrap-log-request-start wrap-log-response
                        wrap-with-logger]]
   [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.cors :refer [wrap-cors]]
   [url-shorter-api.web.middlewares :as m]))

(def app
  (-> app-routes
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete :patch :options])
      m/wrap-shorting-params
      m/wrap-rabbitmq-publisher
      m/wrap-redis-database
      wrap-log-response
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-json-response {:keywords? true :bigdecimals? true})
      (wrap-log-request-params {:transform-fn #(assoc % :level :info)})
      (wrap-params {:encoding "UTF-8"})
      (wrap-defaults api-defaults)
      wrap-log-request-start
      wrap-reload))

(defn run-server [port join?]
  (run-jetty (wrap-with-logger app) {:port port :join? join?}))