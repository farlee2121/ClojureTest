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
(def non-nil-string (gen/such-that some? gen/string-alphanumeric)) ;; http://clojure.github.io/test.check/generator-examples.html



;; (defspec string-returns-self 100 (prop/for-all [str non-nil-string] (= str (dsl/emit-bash str))))
;; (defspec )

(deftest emit-bash
  (testing "emit strings"
    (is (= "hello world" (dsl/emit-bash "hello world")))
    (test-prop "String returns self" 100 (prop/for-all [str non-nil-string] (= str (dsl/emit-bash str)))))
  (testing "emit numbers"
    (is (= "1" (dsl/emit-bash 1)))
    (is (= "12.1" (dsl/emit-bash 12.1)))
    (test-prop "int-returns-self" 100 (prop/for-all [n gen/int] (= (str n) (dsl/emit-bash n)))))
  (testing "commands"
    (testing "print"
      (is (= "echo hello" (dsl/emit-bash '(println "hello"))))
      (prop-is 100 (prop/for-all [_str non-nil-string] (= (str "echo" " " _str) (dsl/emit-bash `(println ~@_str)))))
      )
    )
  )


