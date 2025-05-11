(ns url-shorter-consumer.core
  (:gen-class)
  (:require [url-shorter-consumer.infra.rabbitmq :as r]
            [url-shorter-consumer.consumer :as c]
            [clojure.tools.logging :as l]))

(defn -main
  [& _]
  (l/info "Inicializando consumer RabbitMQ...")
  (r/consume r/consumer-queue-config c/consumer-callback))
