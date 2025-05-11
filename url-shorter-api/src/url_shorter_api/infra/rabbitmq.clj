(ns url-shorter-api.infra.rabbitmq
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [langohr.basic :as b]
            [langohr.channel :as ch]
            [langohr.core :as rmq]
            [langohr.queue :as q]
            [langohr.exchange :as e]
            [cheshire.generate :as gen]
            [cheshire.core :refer [generate-string]])
  (:import [java.time LocalDateTime]))

(extend-protocol gen/JSONable
  LocalDateTime
  (to-json [local-date gen]
    (gen/write-string gen (str local-date))))

(defonce ^:private application-conf (System/getProperty "conf"))

(defonce rabbitmq-config (-> application-conf
                             io/resource
                             slurp
                             edn/read-string
                             :rabbitmq
                             :config))

(defn- build-queue-config
  [queue]
  (let [{durable :durable
         auto-delete :auto-delete
         exclusive :exclusive
         queue-name :queue-name
         exchange-name :exchange-name
         routing-key :routing-key} queue]
    {:queue-config {:durable durable
                    :auto-delete auto-delete
                    :exclusive exclusive}
     :queue-name queue-name
     :exchange-name exchange-name
     :routing-key routing-key}))

(defonce queue-config (-> application-conf
                          io/resource
                          slurp
                          edn/read-string
                          :rabbitmq
                          :queue
                          build-queue-config))

(defn publish
  [publisher-config message]
  (let [connection (rmq/connect rabbitmq-config)
        channel (ch/open connection)
        queue-config (get publisher-config :queue-config)
        queue-name (get publisher-config :queue-name)
        exchange-name (get publisher-config :exchange-name)
        routing-key (get publisher-config :routing-key)
        json-message (generate-string message)] 
    (e/declare channel exchange-name "direct")
    (q/declare channel queue-name queue-config)
    (q/bind channel queue-name exchange-name {:routing-key routing-key})
    (b/publish channel exchange-name routing-key json-message {:content-type "application/json" :persistent true})
    (ch/close channel)
    (rmq/close connection)))