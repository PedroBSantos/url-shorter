(ns url-shorter-api.usecases.url-shorting
  (:require [clojure.spec.alpha :as s]
            [url-shorter-api.domain.logic :as l]
            [url-shorter-api.domain.model :as m]
            [clojure.tools.logging :as lg])
  (:import [java.time LocalDateTime]))

(s/def ::input-url (s/and string? l/valid-url?))
(s/def ::hours-to-expire (s/and integer? (fn [hours-to-expire] (> hours-to-expire 0))))
(s/def ::final-base-url (s/and string? l/valid-url?))
(s/def ::shorting-context (s/keys :req-un [::input-url ::hours-to-expire ::final-base-url]))

(defn- short-url
  [shorting-context]
  (let [input-url (get shorting-context :input-url)
        hours-to-expire (get shorting-context :hours-to-expire)
        final-base-url (get shorting-context :final-base-url)
        url-hash (l/url-hash input-url)
        shorted-at (LocalDateTime/now)
        expire-at (.plusHours shorted-at hours-to-expire)
        final-url (format "%s/%s" final-base-url url-hash)]
    (m/new-url-shorted url-hash input-url final-url shorted-at expire-at)))

(defn- when-url-was-not-shorted
  [shorting-context urls-fns]
  (lg/info "A URL" (get shorting-context :input-url) "ainda não foi encurtada")
  (let [shorted-url (short-url shorting-context)
        save-url! (get urls-fns :save-url)
        url-hash (get shorted-url :url-hash)
        final-url (get shorted-url :after-shorting)]
    (save-url! shorted-url)
    (lg/info "Encurtamento de url finalizado url-hash" url-hash "final-url" final-url)
    {:valid-shorting-context true
     :url-hash url-hash
     :final-url final-url
     :already-shorted false}))

(defn- when-url-already-shorted
  [url]
  (lg/warn "A URL" url "já foi encurtada")
  {:valid-shorting-context true
   :url-hash ""
   :final-url ""
   :already-shorted true})

(defn- do-generate-shorted-url
  [shorting-context urls-fns]
  (lg/info "Verificando se a URL" (get shorting-context :input-url) "já foi encurtada")
  (let [input-url (get shorting-context :input-url)
        contains-url? (get urls-fns :contains-url?)
        already-shorted (contains-url? input-url)]
    (if already-shorted
      (when-url-already-shorted input-url)
      (when-url-was-not-shorted shorting-context urls-fns))))

(defn- when-shorting-context-is-valid
  [shorting-context urls-fns]
  (lg/info "Os parâmetros de encurtamento são válidos")
  (do-generate-shorted-url shorting-context urls-fns))

(defn- when-shorting-context-is-not-valid
  []
  (lg/info "Os parâmetros de encurtamento não são válidos")
  {:valid-shorting-context false
   :url-hash ""
   :final-url ""
   :already-shorted false})

(defn valid-shorting-context?
  [context]
  (s/valid? ::shorting-context context))

(defn generate-shorted-url
  [shorting-context urls-fns]
  (lg/info "Validando parâmetros de encurtamento" shorting-context)
  (if (not (valid-shorting-context? shorting-context))
    (when-shorting-context-is-not-valid)
    (when-shorting-context-is-valid shorting-context urls-fns)))
