(ns url-shorter-consumer.usecases.url-registration
  (:require [clojure.spec.alpha :as s]
            [url-shorter-consumer.domain.logic :as l]
            [clojure.tools.logging :as lg])
  (:import [java.time LocalDateTime]))

(s/def ::expire-at (fn [d] (instance? LocalDateTime d)))
(s/def ::shorted-at (fn [d] (instance? LocalDateTime d)))
(s/def ::url-hash (s/and string? (fn [h] (= 6 (count h)))))
(s/def ::after-shorting (s/and string? l/valid-url?))
(s/def ::before-shorting (s/and string? l/valid-url?))
(s/def ::url-shorted (s/and (s/keys :req-un [::url-hash ::before-shorting ::after-shorting ::shorted-at ::expire-at]) (fn [u] (not (l/url-shorted-expired? u)))))

(defn valid-input-url?
  [input-url]
  (s/valid? ::url-shorted input-url))

(defn- when-invalid-register-url-input [url urls-fns]
  (lg/warn "URL inválida" url)
  (let [discart-url! (get urls-fns :discart-url)]
    (discart-url! url)
    {:valid-input false
     :input url
     :validation-error (s/explain ::url-shorted url)}))

(defn- when-valid-register-url-input [url urls-fns]
  (lg/info "URL válida" url)
  (let [save-url! (get urls-fns :save-url)
        expire-url! (get urls-fns :key-expiration)
        before-shorting (get url :before-shorting)
        seconds-to-expire (l/seconds-until-expire url)]
    (lg/info "Salvando a url...")
    (save-url! before-shorting url)
    (lg/info "Determinando o tempo de expiração para" seconds-to-expire "segundos")
    (expire-url! before-shorting seconds-to-expire)
    {:valid-input true
     :input url}))

(defn register-url
  [url urls-fns]
  (lg/info "Validando a estrutura do dado de entrada" url)
  (if (valid-input-url? url)
    (when-valid-register-url-input url urls-fns)
    (when-invalid-register-url-input url urls-fns)))
