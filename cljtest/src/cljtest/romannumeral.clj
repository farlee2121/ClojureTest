(ns cljtest.romannumeral
  (:require [clojure.core.reducers :as r]))

;; need to handle the case of a lower numeral directly in front of a bigger numeral
(def numeral-value-map
{"I" 1
"V" 5
"X" 10
"L" 50
"C" 100
"D" 500
"M" 1000})

(def max-numeral-val (reduce max (vals numeral-value-map)))

(defn numeral-lt [left right]
  (< (get numeral-value-map left) (get numeral-value-map right))
)

(defn roman-to-arabic [numeral-str]
  (-> 
   (reduce (fn [{:keys [sum prev-val]} numeral]
            ;;(println [sum prev-val numeral])
            (let [arabic-val (get numeral-value-map (str numeral))
                  new-sum (if (< prev-val arabic-val) (+ sum (- arabic-val (* 2 prev-val))) (+ sum arabic-val))]
              {:sum new-sum :prev-val arabic-val}
            ))
          {:sum 0 :prev-val max-numeral-val}
          numeral-str)
      (get :sum)
))
;; (r/fold (fn [{:keys [lsum lprev]} {:keys [rsum rprev]}] (if (< lprev rprev) (- lsum rprev) (+ lsum rprev)))
;;           #((let [arabic-val (get numeral-value-map %)] {:sum arabic-val :prev arabic-val}))
;;           numeral-str)

;; two solutions much prettier than mine, both make use of the fact that if you reverse the list then it creates an equivalence between comparing the sum and comparing adjacent numeral values
;; in other words, decrement markers are the only case where numerals to the right can sum to more than the current numeral

(defn symbol-to-value [sym]
  ;; I like the use of case here
  (case sym 
    \I 1
    \V 5
    \X 10
    \L 50
    \C 100
    \D 500
    \M 1000
  )
)

(defn roman-plus [carry item]
  (if (< carry item) (+ carry item) (- carry item))
)

(defn compact [numbers]
  ;; collapse adjacent numbers of the same value
  (map (fn [x] (reduce + x)) (partition-by identity numbers))
)

(defn translate-roman-numerals [roman]
  (reduce roman-plus (compact (map symbol-to-value (reverse (seq roman)))))
)

; options
; - reduce the list twice, first accumulating everything, then looking for any sequences to subtract like 'IV'
; - reduce with an accumulator that collects symbols until the following symbol is smaller in value, then reduce back, subtracting symbols from the root
;   - requires me to have an terminal element like nil 

(defn translate-roman-numerals-alt [roman]
  (->> (partition-by identity roman)
       (map (partial map {\I 1 \V 5 \X 10 \L 50 \C 100 \D 500 \M 1000}))
       (map (partial reduce +))
       (reverse)
       (reduce #((if (< %1 %2) + -) %1 %2))
  )
)