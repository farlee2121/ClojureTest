(ns dsltutorial.core-test
  (:require [clojure.test :refer :all]
            [dsltutorial.core :as dsl]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            )
  )
(defn print-cont [arg]
  (println arg)
  arg)
(defn prop-is [n prop] (let [result (tc/quick-check n prop)] (is (:result result) result)))
(defn test-prop [label n prop] (testing label (prop-is n prop)))
(def non-nil-string (gen/such-that some? gen/string-alphanumeric))
;; (defspec string-returns-self 100 (prop/for-all [str non-nil-string] (= str (dsl/emit-bash str))))
;; (defspec int-returns-self 100 (prop/for-all [n gen/int] (= n (dsl/emit-bash n))))

;; http://clojure.github.io/test.check/generator-examples.html

(deftest emit-constants
  (testing "emit strings"
    (is (= "hello world" (dsl/emit-bash "hello world")))
    (test-prop "String returns self" 100 (prop/for-all [str non-nil-string] (= str (dsl/emit-bash str)))))
  (testing "emit numbers"
    (is (= 1 (dsl/emit-bash 1)))
    (is (= 12.1 (dsl/emit-bash 12.1)))

  ))


