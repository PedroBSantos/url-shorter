(ns url-shorter-api.domain.model)

(defrecord UrlShorted [url-hash before-shorting after-shorting shorted-at expire-at])

(defn new-url-shorted
  [url-hash before-shorting after-shorting shorted-at expire-at]
  (->UrlShorted url-hash before-shorting after-shorting shorted-at expire-at))
