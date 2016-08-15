(ns fook.unify
  (:require [fook.query :as q]
            [clojure.core.unify :as u]
            [clojure.core.match :refer [match]]))

(defn symbolise-fns
  [form]
  (clojure.walk/postwalk (fn [x]
                           (if (list? x)
                             (with-meta
                               (apply symbol (take 2 (map name x)))
                               {:form x})
                             x))
                         form))

(defn unify-result
  "Given a query, result and a form, unify the result with that form."
  [query result form]
  (let [{:keys [find]} (q/normalise-query query)
        find (symbolise-fns find)
        form (symbolise-fns form)]
    (->> result
         (map (partial zipmap find))
         (map (partial u/subst form))
         (clojure.walk/postwalk
          (fn [x]
            (if-let [form (:form (meta x))]
              form
              x))))))

