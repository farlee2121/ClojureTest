(ns dsltutorial.core)

(derive ::bash ::common)
(derive ::batch ::common)

(def ^{:dynamic true}
  ;; The current script language implementation to generate
  *current-implementation*)

(defmulti emit
  (fn [form]
    [*current-implementation* (class form)]))

(defmethod emit [::bash clojure.lang.PersistentList]
  [form]
  (case (name (first form))
    "println" (str "echo " (second form))))

(defmethod emit [::common java.lang.String]
  [form]
  form)

(defmethod emit [::common java.lang.Long]
  [form]
  (str form))

(defmethod emit [::common java.lang.Double]
  [form]
  (str form))



(defmacro emit-lang [lang form]
  `(binding [*current-implementation* ~lang]
    (emit '~form))
  )

;; (defn emit-lang [lang form]
;;   (binding [*current-implementation* lang]
;;     (emit form)))
