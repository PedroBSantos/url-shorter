(ns url-shorter-api.web.middlewares
  (:require [clojure.tools.logging :as l]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [url-shorter-api.infra.rabbitmq :as r]
            [url-shorter-api.infra.redis :as rd]))

(defonce application-conf (System/getProperty "conf"))
(defonce url-shorting-params (-> application-conf
                                 io/resource
                                 slurp
                                 edn/read-string
                                 :url-shorting-params))

(defn wrap-shorting-params [handler]
  (fn [request]
    (l/info "Adicionando parâmetros de encurtamento de url na requisição")
    (let [request-with-shorting-params (assoc request :url-shorting-params url-shorting-params)
          response (handler request-with-shorting-params)]
      response)))

(defn wrap-rabbitmq-publisher [handler]
  (fn [request]
    (l/info "Adicionando publisher do rabbitmq na requisição")
    (let [request-with-rabbitmq-publisher (assoc request :rabbitmq-publisher (partial r/publish r/queue-config))
          response (handler request-with-rabbitmq-publisher)]
      response)))

(defn wrap-redis-database [handler]
  (fn [request]
    (l/info "Adicionando redis database na requisição")
    (let [redis-url (get rd/redis-db :uri "")
          redis-output (rd/open-connection redis-url)
          redis-connector (get redis-output :connector)
          redis-commands (redis-output :commands)
          request-with-redis-database (assoc request :redis-db (partial rd/contains-key? redis-commands))
          response (handler request-with-redis-database)]
      (l/info "Fechando conexão com o redis")
      (rd/close-connection redis-connector)
      response)))