(ns url-shorter-consumer.infra.prometheus-metrics
  (:require
   [steffan-westcott.clj-otel.api.metrics.instrument :as i]))

(defonce ^:private urls-recuperada-fila-processamento (i/instrument {:name "url-shorter-consumer.urls-recuperada-fila-processamento"
                                                   :measurement-type :long
                                                   :instrument-type :counter
                                                   :unit "{urls}"
                                                   :description "Total de url's que foram recuperadas da fila de processamento"}))

(defn inc-urls-recuperada-fila-processamento [value]
  (i/add! urls-recuperada-fila-processamento {:value value}))


(defonce ^:private urls-reenviadas-fila-processamento (i/instrument {:name "url-shorter-consumer.urls-reenviadas-fila-processamento"
                                                           :measurement-type :long
                                                           :instrument-type :counter
                                                           :unit "{urls}"
                                                           :description "Total de url's que foram reenviadas para a fila de processamento devido erro"}))

(defn inc-urls-reenviadas-fila-processamento [value]
  (i/add! urls-reenviadas-fila-processamento {:value value}))


(defonce ^:private urls-processadas (i/instrument {:name "url-shorter-consumer.urls-processadas"
                                                                     :measurement-type :long
                                                                     :instrument-type :counter
                                                                     :unit "{urls}"
                                                                     :description "Total de url's que foram processadas com sucesso"}))

(defn inc-urls-processadas [value]
  (i/add! urls-processadas {:value value}))


(defonce ^:private urls-rejeitas (i/instrument {:name "url-shorter-consumer.urls-rejeitas"
                                                            :measurement-type :long
                                                            :instrument-type :counter
                                                            :unit "{urls}"
                                                            :description "Total de url's que foram rejeitadas durante o processamento pois, não possuem formato válido"}))

(defn inc-urls-rejeitas [value]
  (i/add! urls-rejeitas {:value value}))