(ns fook.unify
  (:require [fook.query :as q]
            [clojure.core.unify :as u]))

(defn unify-result
  "Given a query, result a form structure unify the result with tha form."
  [query result form]
  (let [{:keys [find]} (q/normalise-query query)
        bindings (map (partial zipmap find) result)]
    (map (partial u/subst form) bindings)))
