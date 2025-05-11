(ns url-shorter-consumer.usecases.url-registration-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [url-shorter-consumer.usecases.url-registration :as ur])
  (:import [java.time LocalDateTime]))

(deftest valid-input-url?-test
  (testing "Não deveria ser um input válido"
    (let [url-input-1 "https://www.google.com"
          url-input-2 {}
          url-input-3 nil
          url-input-4 {:url-hash "123456"}
          url-input-5 {:url-hash "123456" :before-shorting "https://www.google.com"}
          url-input-6 {:url-hash "123456" :before-shorting "https://www.google.com" :after-shorting "https://url-shorter.com/123456"}
          url-input-7 {:url-hash "123456" :before-shorting "https://www.google.com" :after-shorting "https://url-shorter.com/123456" :shorted-at (LocalDateTime/now)}
          url-input-8 {:url-hash "123456" :before-shorting "https://www.google.com" :after-shorting "https://url-shorter.com/123456" :shorted-at (.plusHours (LocalDateTime/now) 48) :expire-at (.plusHours (LocalDateTime/now) -24)}
          url-input-1-validation (ur/valid-input-url? url-input-1)
          url-input-2-validation (ur/valid-input-url? url-input-2)
          url-input-3-validation (ur/valid-input-url? url-input-3)
          url-input-4-validation (ur/valid-input-url? url-input-4)
          url-input-5-validation (ur/valid-input-url? url-input-5)
          url-input-6-validation (ur/valid-input-url? url-input-6)
          url-input-7-validation (ur/valid-input-url? url-input-7)
          url-input-8-validation (ur/valid-input-url? url-input-8)]
      (is false? (get url-input-1-validation :valid-input))
      (is false? (get url-input-2-validation :valid-input))
      (is false? (get url-input-3-validation :valid-input))
      (is false? (get url-input-4-validation :valid-input))
      (is false? (get url-input-5-validation :valid-input))
      (is false? (get url-input-6-validation :valid-input))
      (is false? (get url-input-7-validation :valid-input))
      (is false? (get url-input-8-validation :valid-input))))
  (testing "Deveria ser um input válido"
    (let [url-input-1 {:url-hash "123456" :before-shorting "https://www.google.com" :after-shorting "https://url-shorter.com/123456" :shorted-at (LocalDateTime/now) :expire-at (.plusHours (LocalDateTime/now) 24)}
          url-input-1-validation (ur/valid-input-url? url-input-1)]
      (is true? (get url-input-1-validation :valid-input))))) 