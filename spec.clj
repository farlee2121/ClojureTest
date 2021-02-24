(ns cljtest (:require [clojure.spec.alpha :as s]))
(def lat-regex #"^(\-?\d+(\.\d+)?),\s*(\-?\d+(\.\d+)?)$")
(s/def ::lat (s/or 
  (s/and string? #(re-matches lat-regex %))
  (s/and float? #(<= -90 %) #(<= % 90))
))
(def lon-regex #"^(\-?\d+(\.\d+)?),\s*(\-?\d+(\.\d+)?)$")
(s/def ::lon (s/or 
  (s/and string? #(re-matches lat-regex %))
  (s/and float? #(<= -180 %) #(<= % 180))
))
(s/def ::coordinate (s/keys :req [::lat ::lon]))

(s/def ::street (s/and string? not-empty)) 
(s/def ::city string?)
(s/def ::state string?)
(s/def ::zip (s/or 
  string?
  (s/and int? #(<= 10000 %) #(<= % 99999))
))
(s/def ::address (s/keys :req[::street ::city ::state ::zip]))

(s/def ::location (s/or ::coordinate ::address))
(s/def ::location-list (s/* ::location ))

(s/def ::some-enum #{:opt1 :opt2 :opt3}) 

;;test with (s/verify? spec value)