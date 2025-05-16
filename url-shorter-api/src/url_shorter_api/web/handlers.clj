(ns url-shorter-api.web.handlers
  (:require [url-shorter-api.usecases.url-shorting :as u]))

(defn short-url-handler [request]
  (let [url (get-in request [:body :url] "")
        url-shorting-params (get request :url-shorting-params {})
        hours-to-expire (get url-shorting-params :hours-to-expire)
        final-base-url (get url-shorting-params :final-base-url)
        urls-repository-write (get request :rabbitmq-publisher)
        urls-repository-read (get request :redis-db)
        url-shorted (u/generate-shorted-url {:input-url url
                                             :hours-to-expire hours-to-expire
                                             :final-base-url final-base-url}
                                            {:save-url urls-repository-write
                                             :contains-url? urls-repository-read})]
    (if (get url-shorted :already-shorted)
      {:status 409 
       :body url-shorted}
      {:status (if (get url-shorted :valid-shorting-context) 
                 200 
                 400) 
       :body url-shorted})))

(defn health-check-handler [_]
  {:status 200
   :body {:healthy true}})