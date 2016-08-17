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

(defn expand-clause
  ([clause]
   (expand-clause clause true))
  ([clause op]
   (match
    [clause]
    [[?e]] [[?e '_ '_ '_ op]]
    [[?e ?a]] [[?e ?a '_ '_ op]]
    [[?e ?a ?v]] [[?e ?a ?v '_ op]]
    [[?e ?a ?v ?tx]] [[?e ?a ?v ?tx op]]
    [(['or & ?clauses] :seq)] (mapcat expand-clause ?clauses)
    [(['or-join _ & ?clauses] :seq)] (mapcat expand-clause ?clauses)
    [(['not & ?clauses] :seq)] (mapcat #(expand-clause % false) ?clauses)
    [(['not-join ?keep & ?clauses] :seq)] (let [keep? (set ?keep)]
                                            (->> ?clauses
                                                 (mapcat #(expand-clause % false))
                                                 (filter (fn [clause]
                                                           (seq (filter keep? clause))))))
    :else clause)))

(defn expand-clauses
  ([clauses]
   (->> clauses
        (mapcat expand-clause)
        (into #{}))))

(defn unify-clauses
  [clauses datoms subst]
  (let [clause (first clauses)
        datom (first datoms)]
    (if (and clause datom)
      (let [subst' (u/unify datom clause subst)]
        (if subst'
          (let [subst' (unify-clauses clauses (rest datoms) subst')]
            (recur (rest clauses) datoms subst'))
          (let [subst' (unify-clauses (rest clauses) datoms subst)]
            (recur clauses (rest datoms) subst'))))
      subst)))

(defn unify-query
  "Given a query and some datoms, return a set of possible bindings that
  would match the query."
  ([query datoms]
   (let [{:keys [where]} (q/normalise-query query)
         clauses (expand-clauses where)]
     (->> datoms
          (group-by first)
          (map second)
          (map #(unify-clauses clauses % {}))
          (into #{})))))
