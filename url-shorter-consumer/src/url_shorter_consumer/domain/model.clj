(ns url-shorter-consumer.domain.model
  (:import [java.time LocalDateTime]))

(defrecord UrlShorted [url-hash before-shorting after-shorting shorted-at expire-at])

(defn new-url-shorted
  ([url-hash before-shorting after-shorting shorted-at expire-at]
  (let [shorted-at-as-datetime (if (not (instance? LocalDateTime shorted-at)) (LocalDateTime/parse shorted-at) shorted-at)
        expire-at-as-datetime (if (not (instance? LocalDateTime expire-at)) (LocalDateTime/parse expire-at) expire-at)]
    (->UrlShorted url-hash before-shorting after-shorting shorted-at-as-datetime expire-at-as-datetime)))
  ([url]
   (let [url-hash (get url :url-hash "")
         before-shorting (get url :before-shorting "")
         after-shorting (get url :after-shorting "")
         shorted-at (get url :shorted-at nil)
         expire-at (get url :expire-at nil)
         shorted-at-as-datetime (if (not (instance? LocalDateTime shorted-at)) (LocalDateTime/parse shorted-at) shorted-at)
         expire-at-as-datetime (if (not (instance? LocalDateTime expire-at)) (LocalDateTime/parse expire-at) expire-at)]
     (->UrlShorted url-hash before-shorting after-shorting shorted-at-as-datetime expire-at-as-datetime))))