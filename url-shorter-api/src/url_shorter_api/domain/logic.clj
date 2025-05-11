(ns url-shorter-api.domain.logic
  (:import
   [java.security MessageDigest]
   [java.time LocalDateTime])
  (:require [clojure.string :as s]))

(defn valid-url?
  [url]
  (let [url-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&\/\/=]*)"]
    (if (nil? url)
      false
      (not= nil (re-matches url-regex url)))))

(defn- sha256
  [string]
  (let [digest (.digest (MessageDigest/getInstance "SHA-256") (.getBytes string "UTF-8"))]
    (apply str (map (partial format "%02x") digest))))

(defn url-hash
  [url]
  (-> url
      sha256
      (.substring 0 6)))

(defn url-protocol
  [url]
  (-> url
      (s/split #":\/\/")
      first))

(defn url-shorted-expired?
  [url-shorted]
  (let [now (LocalDateTime/now)
        expire-at (get url-shorted :expire-at)]
    (.isAfter now expire-at)))