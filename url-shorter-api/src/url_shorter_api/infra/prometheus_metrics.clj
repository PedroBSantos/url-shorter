(ns url-shorter-api.infra.prometheus-metrics
  (:require
   [steffan-westcott.clj-otel.api.metrics.instrument :as i]))

(defonce url-encurtada-enviada-fila (i/instrument {:name "url-shorter-api.urls-encurtadas-enviadas-fila-processamento"
                                                   :measurement-type :long
                                                   :instrument-type :counter
                                                   :unit "{urls}"
                                                   :description "Total de url's que foram enviadas para a fila de processamento após encurtamento"}))

(defn inc-url-encurtada-enviada-fila [value]
  (i/add! url-encurtada-enviada-fila {:value value}))
