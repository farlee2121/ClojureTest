(ns dsltutorial.core)

(defmulti emit-bash
  (fn [form]
    (class form)))

(defmethod emit-bash
  clojure.lang.PersistentList
  [form]
  (case (name (first form))
    "println" (str "echo " (second form))))

(defmethod emit-bash
  java.lang.String
  [form]
  form)

(defmethod emit-bash
  java.lang.Long
  [form]
  (str form))

(defmethod emit-bash
  java.lang.Double
  [form]
  (str form))
