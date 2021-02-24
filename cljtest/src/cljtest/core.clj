(ns cljtest.core
  (:require [clojure.spec.alpha :as s])
  (:gen-class))

(def lat-regex #"^(\-?\d+(\.\d+)?),\s*(\-?\d+(\.\d+)?)$")
(s/def ::lat (s/or
              :lat-string (s/and string? #(re-matches lat-regex %))
              :lat-float (s/and float? #(<= -90 %) #(<= % 90))))
(def lon-regex #"^(\-?\d+(\.\d+)?),\s*(\-?\d+(\.\d+)?)$")
(s/def ::lon (s/or
              :lon-string (s/and string? #(re-matches lat-regex %))
              :lon-float (s/and float? #(<= -180 %) #(<= % 180)))
       )
(s/def ::coordinate (s/keys :req [::lat ::lon]))

(s/def ::street (s/and string? not-empty))
(s/def ::city string?)
(s/def ::state string?)
(s/def ::zip (s/or
              :zip-string string?
              :zip-int (s/and int? #(<= 10000 %) #(<= % 99999)))
       )
(s/def ::address (s/keys :req [::street ::city ::state ::zip]))

(s/def ::location (s/or ::coordinate ::address))
(s/def ::location-list (s/* ::location))

(s/def ::some-enum #{:opt1 :opt2 :opt3})


;;test with (s/verify? spec value)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (s/valid? ::lon 170.0)
  (def coord {::lat 50.21 ::lon 23.22})
  (println (::lon coord))
  (s/explain ::address {::street "151 N 8th" ::city "" ::state "" ::zip 68555})
  (println "I'm done")
)
