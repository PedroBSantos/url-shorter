(ns url-shorter-api.infra.redis
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

(defn contains-key? 
  [cmds key]
  (->> key
       (redis/exists cmds)
       (= 1)))
