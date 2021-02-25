(ns kata.core
  (:require

   [clojure.string :refer [split]]
   [clojure.math.numeric-tower :as math]

   [clojure.edn :as edn]))

(defn to-digits [n]
  (->> n str (#(split % #"")) (map #(edn/read-string %)))
  ;;(->> n str (map (comp #(- % 48) int)))
  )

(defn dig-pow [n p]

  (->> (to-digits n)

       (map-indexed #(math/expt %2 (+ p %1)))
       (reduce +)
       (#(/ % n))

       (#(if (= 0 (mod % 1)) % -1))))


(defn c-flatten [toCollapse]
  (println toCollapse)
  (if (some coll? toCollapse)
    (recur (reduce (fn [%1 %2]
                     (println %1 %2)
                     (vec ((if (coll? %2) concat conj) %1 %2))) [] toCollapse))
    toCollapse))

(defn fib [n]
  (println "n:" n)
  (loop [n0 0 n1 1 accum [1]]
    (println "n0:" n0 "n1:" n1 "accum" accum)
    (if (< (count accum) n)
      (let [n-next (+ n0 n1)]
        (recur n1 n-next (conj accum n-next)))
      accum)))


(defn dropn [lst drp]
  (->> lst
       (map-indexed #(if (and (not= 0 %) (= 0 (mod % drp))) nil %2))
       (remove nil?)))

(defn print-cont [arg]
  (println arg)
  arg)

(defn rev-interweave [list parts]
  (->> list
       (group-by #(mod %1 parts))
       (print-cont)
       (map second)))

(defn rotate [shift lst]
  (let [lst-size (count lst)
        mod-shift (mod shift lst-size)]
    (->> lst
         (split-at mod-shift)
         reverse
         (reduce concat))))
  ;; (let [rotf (fn [n l] (->> l (split-at n (reduce concat [()])
  ;;       rotr ]
;;       

(defn largest-sub [lst]
  (let [size (count lst)
        subsets (for [i (range 0 size) j (range (+ i 2) (inc size))] (subvec lst i j))
        dedup (fn [lst] (map first (partition-by identity lst)))
        increasing? #(and (= (sort %) %) (= (dedup %) %))]
    (->> subsets
         (print-cont)
         (filter increasing?)
         (print-cont)
         (sort-by #(* -1 (count %)))
         (print-cont)
         (#(nth % 0 [])))))

(defn c-partition [size lst]
  (for [i (range 0 (int (/ (count lst) size)))]
    (take size (drop (* i size) lst))))

(defn c-distinct [lst]
  (:ordered (reduce (fn [{:keys [unique ordered] :as agg} nxt]
                      (if (contains? unique nxt)
                        agg
                        {:ordered (conj ordered nxt) :unique (conj unique nxt)}))
                    {:ordered [] :unique #{}}
                    lst)))

(defn c-comp [& funcs]
  (let [cf (reduce
            (fn [aggf f] #(->> % (print-cont) aggf f))
            (reverse (drop-last funcs)))]
    (fn [& args] (print-cont args) (cf (apply (last funcs) args)))))

(defn lazy-reduce
  ;; reimplementation of reductions 
  ([f col]
   (lazy-reduce f (first col) (rest col)))
  ([f seed col]
   (lazy-seq (cons
              seed
              (when-not (empty? col)
                (lazy-reduce f (f seed (first col)) (rest col)))))))

(defn test-multi
  ([] "default")
  ([arg] arg))


(defn c-iterate [f seed]
  ;; this works because cons doesn't try to evaluate the lazy seq, it 
  ;; returns a new seq that is the value, then the lazy seq
  (cons seed (lazy-seq (c-iterate f (f seed)))))


(defn gcd [n1 n2]
  (let [smaller (min n1 n2)
        potential-divisors (cons smaller (reverse (range 1 (inc (/ smaller 2)))))]
    (first (filter #(= 0 (rem n1 %) (rem n2 %)) potential-divisors))))

(fn [s1 s2] (doall (filter #(contains? s2 %) s1)))

(defn col-test [col]
  (let [test-vals [[1 2] [1 2] [2 3]]
        moded (apply (partial conj col) test-vals)]
    (cond
      (= (take 3 moded) (reverse test-vals)) :list
      (= (take-last 3 moded) test-vals) :vector
      (= (moded 1) 2) :map
      (= ((frequencies moded) [1 2]) 1) :set
      :else :map))
  ;; (try
  ;;   (let [moded (conj col 1 2 2)]
  ;;     (cond
  ;;       (= (take 3 moded) [2 2 1]) :list
  ;;       (= (take-last 3 moded) [1 2 2]) :vector
  ;;       (= ((frequencies moded) 2) 1) :set
  ;;       )
  ;;    )
  ;;   (catch Exception _ :map)
  ;;   )
  )

(defn pascal-triangle [n]
  (nth (cons [1]
             (lazy-seq (iterate
                        (fn [prevRow] (flatten [1 (map #(reduce + %) (partition 2 1 prevRow)) 1]))
                        [1 1])))
       (dec n))
  ;; (if (= n 1)
  ;;   [1]
  ;;   (loop [i 2 row [1 1]]
  ;;     (if (= i n)
  ;;       row
  ;;       (flatten [1 (map #(reduce +) (partition 2 1 row)) 1])
  ;;       )
  ;;     )
  ;;   )
  )

((defn lcm [& xs]
   (first (filter
           (fn [mult] (every? #(= 0 (rem mult %)) xs))
           (rest (range))))))
(defn lcm-ratios [& xs]
  (let [lcm-int (fn [ys] (first (filter
                                 (fn [mult] (every? #(= 0 (rem mult %)) ys))
                                 (rest (range)))))
        get-greatest-denominator (fn [ys] (reduce max (map #(if (ratio? %)
                                                              (denominator %)
                                                              1)
                                                           ys)))
        greatest-denom (get-greatest-denominator xs)]

    (->> xs
         (map #(* % greatest-denom))
         (lcm-int)
         (#(/ % greatest-denom)))))

(defn pascal-trapezoid [start-vec]
  (lazy-seq (iterate
             (fn [prevRow] (flatten [(first start-vec) (map #(reduce + %) (partition 2 1 prevRow)) (last start-vec)]))
             start-vec)))


(defn mirror-tree [tree]
  (if (coll? tree)
    (let [[k l r] tree] [k (mirror-tree r) (mirror-tree l)])
    tree))

(defn tree-sym? [tree]
  (let [mirror-tree
        (fn [tree]
          (if (coll? tree)
            (let [[k l r] tree] [k (mirror-tree r) (mirror-tree l)])
            tree))
        [_ right left] tree]
    (= left (mirror-tree right))))

(defn pairwise-disjoint [sets]
  (let [pairwise (for [s sets s2 sets :when (not= s s2)] [s s2])
        disjoint? (fn [[s1 s2]] (= nil (some s1 s2)))]
    (and
     (not-any? empty? sets)
     (every? disjoint? pairwise))))

(println (pairwise-disjoint #{#{(#(-> *)) + (quote mapcat) #_nil}
                              #{'+ '* mapcat (comment mapcat)}
                              #{(do) set contains? nil?}
                              #{#_empty?}}))


(defn sort-words [sentance]
  (->> sentance
       (remove #{\! \.})
       (apply str)
       (#(clojure.string/split % #" "))
       (sort-by clojure.string/lower-case)))

(defn nprimes [n]
  (let [prime? (fn [n] (every? #(not= 0 (rem n %)) (range 2 n)))
        next-prime (fn next-prime [prev-prime]
                     (first (filter prime? (iterate inc (inc prev-prime)))))]
    (take n (iterate next-prime 2))))

(defn perfect-squares [num-string]
  ;; the important thing here is the integer parse
  (->> num-string
       (#(clojure.string/split % #","))
       (map #(Integer/parseInt %))
       (filter #(some (fn [n] (= (* n n) %)) (range %)))
       (clojure.string/join ",")))

;; !!! letfn is super helpful for these kata, it lets me declare multiple
;;  functions with one layer, instead of fn then let
  
 (defn anagram-finder [words]
      (->> words
           (map #(identity {:word % :char-map (frequencies %)}))
           (group-by :char-map)
           (vals)
           (map #(map :word %))
           (filter #(< 1 (count %)))
           (map set)
           (set)))


(defn reduce-maps [op & maps]
  (reduce
   (fn [acc [k v]] (if (contains? acc k) (assoc acc k (op (acc k) v)) (assoc acc k v)))
   {}
   (print-cont (reduce conj (map vec maps)))))
 
(defn kebab-to-camel [word]
  (->> word
       (#(clojure.string/split % #"-"))
       (map-indexed #(if (not= 0 %)
                         (apply str (cons (clojure.string/upper-case (first %2)) (rest %2)))
                          %2
                                  ))
      ;;  (#(cons (clojure.string/lower-case (first %)) (rest %)))
       (clojure.string/join "")
  )
       )

(def totientt (letfn [(gcd [n1 n2]
  		(let [smaller (min n1 n2)
        		potential-divisors (cons smaller (reverse (range 1 (inc (/ smaller 2)))))]
    		(first (filter #(= 0 (rem n1 %) (rem n2 %)) potential-divisors))))
                      (coprime? [n1 n2] (= 1 (gcd n1 n2)))
        (totient [n] (case n 
                       1 1
                       (count (filter coprime? (range 1 n)))
                       )
                 )
        ]
                 totient
                 ))

(defn happy-numbers [n]
  (let [to-digits (fn [_n] (->> _n str (map (comp #(- % 48) int))))]
    ; should try transduce
    )
  )