(ns url-shorter-api.usecases.url-test
  (:require
   [clojure.string :as s]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [url-shorter-api.usecases.url-shorting :as u]))

(def urls-repository (atom []))

(use-fixtures :each (fn [f]
                      (reset! urls-repository [])
                      (f)))

(deftest generate-shorted-url-test
  (testing "Deveria gerar a url encurtada"
    (let [url-1 "https://www.google.com"
          url-2 "http://www.youtube.com"
          hours-to-expire 24
          url-1-shorted (u/generate-shorted-url {:input-url url-1
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)
                                                 :contains-url? (fn [url] (some (fn [x] (= url (get x :before-shorting))) @urls-repository))})
          url-2-shorted (u/generate-shorted-url {:input-url url-2
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)
                                                 :contains-url? (fn [url] (some (fn [x] (= url (get x :before-shorting))) @urls-repository))})]
      (is (and (get url-1-shorted :valid-shorting-context) (s/starts-with? (get url-1-shorted :final-url) "https://shorter.com")))
      (is (and (get url-2-shorted :valid-shorting-context) (s/starts-with? (get url-2-shorted :final-url) "https://shorter.com")))
      (is (= 2 (count @urls-repository)))))
  (testing "Não deveria gerar a url encurtada quando a url não possuir formato válido"
    (let [hours-to-expire 24
          url-1 "https:/google.com"
          url-2 "http:/google.com"
          url-3 ""
          url-4 nil
          url-5 "https://.com"
          url-1-shorted (u/generate-shorted-url {:input-url url-1
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)})
          url-2-shorted (u/generate-shorted-url {:input-url url-2
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)})
          url-3-shorted (u/generate-shorted-url {:input-url url-3
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)})
          url-4-shorted (u/generate-shorted-url {:input-url url-4
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)})
          url-5-shorted (u/generate-shorted-url {:input-url url-5
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)})]
      (is false? (and (get url-1-shorted :valid-shorting-context) (= 0 (count @urls-repository))))
      (is false? (and (get url-2-shorted :valid-shorting-context) (= 0 (count @urls-repository))))
      (is false? (and (get url-3-shorted :valid-shorting-context) (= 0 (count @urls-repository))))
      (is false? (and (get url-4-shorted :valid-shorting-context) (= 0 (count @urls-repository))))
      (is false? (and (get url-5-shorted :valid-shorting-context) (= 0 (count @urls-repository))))))
  (testing "Não deveria encurtar uma URL que já foi encurtada"
    (let [url-1 "https://www.instagram.com"
          hours-to-expire 24
          url-1-shorted (u/generate-shorted-url {:input-url url-1
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)
                                                 :contains-url? (fn [url] (some (fn [x] (= url (get x :before-shorting))) @urls-repository))})
          url-2-shorted (u/generate-shorted-url {:input-url url-1
                                                 :hours-to-expire hours-to-expire
                                                 :final-base-url "https://shorter.com"}
                                                {:save-url (partial swap! urls-repository conj)
                                                 :contains-url? (fn [url] (some (fn [x] (= url (get x :before-shorting))) @urls-repository))})]
      (is (and (get url-1-shorted :valid-shorting-context) (s/starts-with? (get url-1-shorted :final-url) "https://shorter.com")))
      (is false? (get url-2-shorted :already-shorted)))))

(deftest valid-shorting-context?-test
  (testing "Deveria ser um contexto de encurtamento válido"
    (let [hours-to-expire-1 1
          final-base-url "https://shorter.com"
          input-url "https://www.google.com"
          shorting-context-1 {:input-url input-url :hours-to-expire hours-to-expire-1 :final-base-url final-base-url}]
      (is (u/valid-shorting-context? shorting-context-1))))
  (testing "Não deveria ser um contexto de encurtamento válido"
    (let [hours-to-expire 24
          hours-to-expire-1 0
          final-base-url "https://shorter.com"
          input-url "https://www.google.com"
          shorting-context-1 {:input-url input-url :hours-to-expire hours-to-expire-1 :final-base-url final-base-url}
          hours-to-expire-2 -1
          shorting-context-2 {:input-url input-url :hours-to-expire hours-to-expire-2 :final-base-url final-base-url}
          final-base-url-invalid "https://shorter"
          shorting-context-3 {:input-url input-url :hours-to-expire hours-to-expire :final-base-url final-base-url-invalid}
          input-url-invalid "https://www.google"
          shorting-context-4 {:input-url input-url-invalid :hours-to-expire hours-to-expire :final-base-url final-base-url}
          shorting-context-5 {:input-url input-url-invalid :hours-to-expire hours-to-expire-2 :final-base-url final-base-url-invalid}]
      (is false? (u/valid-shorting-context? shorting-context-1))
      (is false? (u/valid-shorting-context? shorting-context-2))
      (is false? (u/valid-shorting-context? shorting-context-3))
      (is false? (u/valid-shorting-context? shorting-context-4))
      (is false? (u/valid-shorting-context? shorting-context-5)))))
