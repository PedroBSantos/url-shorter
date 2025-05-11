(ns url-shorter-consumer.infra.redis
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [celtuce.connector :as conn]
            [celtuce.commands :as redis]
            [celtuce.codec :as c]))

(defonce ^:private application-conf (System/getProperty "conf"))
(defonce redis-db (-> application-conf
                      io/resource
                      slurp
                      edn/read-string
                      :redis-db))

(defn open-connection
  [connection-string]
  (let [connector (conn/redis-server connection-string :codec (c/carbonite-codec))
        commands (conn/commands-sync connector)]
    {:connector connector
     :commands commands}))

(defn close-connection
  [connection]
  (conn/shutdown connection))

(defn insert-value-for-a-key
  [cmds key value]
  (redis/set cmds key value))

(defn expire-key-in
  [cmds key seconds-to-expire]
  (redis/expire cmds key seconds-to-expire))
