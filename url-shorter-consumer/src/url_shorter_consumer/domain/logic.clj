(ns url-shorter-consumer.domain.logic
  (:import [java.time LocalDateTime]
           [java.time.temporal ChronoUnit]))

(defn url-shorted-expired?
  [url-shorted]
  (let [now (LocalDateTime/now)
        expire-at (get url-shorted :expire-at)]
    (.isAfter now expire-at)))

(defn valid-url?
  [url]
  (let [url-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&\/\/=]*)"]
    (if (nil? url)
      false
      (not= nil (re-matches url-regex url)))))

(defn seconds-until-expire 
  [url-shorted]
  (let [shorted-at (get url-shorted :shorted-at)
        expire-at (get url-shorted :expire-at)
        seconds-to-expire (.until shorted-at expire-at ChronoUnit/SECONDS)]
    seconds-to-expire))