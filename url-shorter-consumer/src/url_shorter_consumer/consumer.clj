(ns url-shorter-consumer.consumer
  (:require [langohr.basic :as b]
            [cheshire.core :refer [parse-string]]
            [url-shorter-consumer.usecases.url-registration :as ur]
            [url-shorter-consumer.infra.redis :as rd]
            [clojure.tools.logging :as l]
            [url-shorter-consumer.domain.model :as m]
            [url-shorter-consumer.infra.rabbitmq :as r]
            [url-shorter-consumer.infra.prometheus-metrics :as pm]))

#_{:clj-kondo/ignore [:unused-binding]}
(defn consumer-callback
  [channel {:keys [content-type delivery-tag type]} ^bytes payload]
  (l/info "Realizando o processamento de mensagem...")
  (l/info "Abrindo conexão com o redis")
  (let [redis-url (get rd/redis-db :uri "")
        redis-output (rd/open-connection redis-url)
        redis-connector (get redis-output :connector)
        redis-commands (redis-output :commands)
        redis-data-save (partial rd/insert-value-for-a-key redis-commands)
        redis-key-expire (partial rd/expire-key-in redis-commands)
        publisher (fn [x] (r/publish r/publisher-queue-config x) (pm/inc-urls-rejeitas 1))]
    (try
      (pm/inc-urls-recuperada-fila-processamento 1)
      (-> payload
          String.
          (parse-string true)
          m/new-url-shorted
          (ur/register-url {:save-url redis-data-save :key-expiration redis-key-expire :discart-url publisher}))
      (l/info "Realizando ACK da mensagem")
      (b/ack channel delivery-tag false)
      (pm/inc-urls-processadas 1)
      (catch Exception e
        (l/error "Exception capturada")
        (.printStackTrace e)
        (l/info "Realizando o NACK da mensagem")
        (b/nack channel delivery-tag false true)
        (pm/inc-urls-reenviadas-fila-processamento 1))
      (finally
        (l/info "Fechando conexão com o redis")
        (rd/close-connection redis-connector)))))