(ns url-shorter-consumer.infra.rabbitmq
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [langohr.channel :as ch]
            [langohr.consumers :as c]
            [langohr.core :as rmq]
            [langohr.basic :as b]
            [langohr.queue :as q]
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

(defonce consumer-queue-config (-> application-conf
                                   io/resource
                                   slurp
                                   edn/read-string
                                   :rabbitmq
                                   :consumer-queue
                                   build-queue-config))

(defonce publisher-queue-config (-> application-conf
                                   io/resource
                                   slurp
                                   edn/read-string
                                   :rabbitmq
                                   :publisher-queue
                                   build-queue-config))

(defn consume
  [consumer-config callback]
  (let [queue-config (get consumer-config :queue-config)
        queue-name (get consumer-config :queue-name)
        connection (rmq/connect rabbitmq-config)
        channel (ch/open connection)]
    (q/declare channel queue-name queue-config)
    (c/subscribe channel queue-name callback {:auto-ack false})))

(defn publish
  [publisher-config message]
  (let [connection (rmq/connect rabbitmq-config)
        channel (ch/open connection)
        queue-config (get publisher-config :queue-config)
        queue-name (get publisher-config :queue-name)
        exchange-name (get publisher-config :exchange-name)
        routing-key (get publisher-config :routing-key)
        json-message (generate-string message)]
    (q/declare channel queue-name queue-config)
    (q/bind channel queue-name exchange-name {:routing-key routing-key})
    (b/publish channel exchange-name routing-key json-message {:content-type "application/json" :persistent true})
    (ch/close channel)
    (rmq/close connection)))