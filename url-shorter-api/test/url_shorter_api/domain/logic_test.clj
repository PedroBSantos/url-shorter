(ns url-shorter-api.domain.logic-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [url-shorter-api.domain.logic :as l]
   [url-shorter-api.domain.model :as m])
  (:import [java.time LocalDateTime]
           [java.time Month]))

(deftest valid-url?-test
  (testing "Deveriam ser url's válidas"
    (let [valid-url-1 "https://www.google.com"
          valid-url-2 "https://google.com"
          valid-url-3 "http://www.google.com"
          valid-url-4 "http://google.com"
          valid-url-5 "https://google.com.br"
          valid-url-6 "https://www.google.com.br"
          valid-url-7 "http://www.google.com.br"]
      (is (l/valid-url? valid-url-1))
      (is (l/valid-url? valid-url-2))
      (is (l/valid-url? valid-url-3))
      (is (l/valid-url? valid-url-4))
      (is (l/valid-url? valid-url-5))
      (is (l/valid-url? valid-url-6))
      (is (l/valid-url? valid-url-7))))
  (testing "Não deveriam ser url's válidas"
    (let [invalid-url-1 ""
          invalid-url-2 "http://.com"
          invalid-url-3 "https://.com"
          invalid-url-4 "https://www.google"
          invalid-url-5 "http://www.google"
          invalid-url-6 "https://google"
          invalid-url-7 "//.com"
          invalid-url-8 "https://www.google.com:8"
          invalid-url-9 "http://www.google.com:8"]
      (is false? (l/valid-url? invalid-url-1))
      (is false? (l/valid-url? invalid-url-2))
      (is false? (l/valid-url? invalid-url-3))
      (is false? (l/valid-url? invalid-url-4))
      (is false? (l/valid-url? invalid-url-5))
      (is false? (l/valid-url? invalid-url-6))
      (is false? (l/valid-url? invalid-url-7))
      (is false? (l/valid-url? invalid-url-8))
      (is false? (l/valid-url? invalid-url-9)))))

(deftest url-hash-test
  (testing "Deveria gerar o hash da url"
    (let [url-1 "https://youtube.com"
          url-2 "http://instagram.com"
          url-3 "https://www.google.com.br"
          url-1-hash (l/url-hash url-1)
          url-2-hash (l/url-hash url-2)
          url-3-hash (l/url-hash url-3)]
      (is (and (not= nil url-1-hash) (not-empty url-1-hash) (instance? String url-1-hash) (= 6 (count url-1-hash))))
      (is (and (not= nil url-2-hash) (not-empty url-2-hash) (instance? String url-2-hash) (= 6 (count url-2-hash))))
      (is (and (not= nil url-3-hash) (not-empty url-3-hash) (instance? String url-3-hash) (= 6 (count url-3-hash)))))))

(deftest url-protocol-test
  (testing "Deveria recuperar o protocolo da url"
    (let [url-1 "https://youtube.com"
          url-2 "http://localhost:8080"
          url-1-protocol (l/url-protocol url-1)
          url-2-protocol (l/url-protocol url-2)]
      (is (and (not-empty url-1-protocol) (= "https" url-1-protocol)))
      (is (and (not-empty url-2-protocol) (= "http" url-2-protocol)))))
  (testing "Não deveria recuperar o protocolo da url"
    (let [url-1 "https:/youtube.com"
          url-2 "http:/localhost"
          url-3 "https:test"
          url-4 "http:test"
          url-5 "https:"
          url-6 "http:"]
      (is nil? (l/url-protocol url-1))
      (is nil? (l/url-protocol url-2))
      (is nil? (l/url-protocol url-3))
      (is nil? (l/url-protocol url-4))
      (is nil? (l/url-protocol url-5))
      (is nil? (l/url-protocol url-6)))))

(deftest url-shorted-expired?-test
  (testing "A url deveria estar expirada"
    (let [url-1 "https://www.google.com"
          shorted-at (LocalDateTime/of 2024 Month/MAY 9 11 49)
          expire-at (.plusHours shorted-at 2)
          url-1-hash (l/url-hash url-1)
          final-url-1 (format "https://url-shorter.com/%s" url-1-hash)
          url-1-shorted (m/new-url-shorted url-1-hash url-1 final-url-1 shorted-at expire-at)]
      (is (l/url-shorted-expired? url-1-shorted))))
  (testing "A url não deveria estar expirada"
    (let [url-1 "https://www.google.com"
          shorted-at (LocalDateTime/now)
          expire-at (.plusHours shorted-at 2)
          url-1-hash (l/url-hash url-1)
          final-url-1 (format "https://url-shorter.com/%s" url-1-hash)
          url-1-shorted (m/new-url-shorted url-1-hash url-1 final-url-1 shorted-at expire-at)]
      (is false? (l/url-shorted-expired? url-1-shorted)))))
