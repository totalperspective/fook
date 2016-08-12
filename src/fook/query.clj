(ns fook.query
  (:require [clojure.set :as set]))

(defn normalise-query
  "Returns the map form of a datalog query. Annotates the metadata with
  the original form. And the order (if from a seq)."
  [query]
  (if (map? query)
    (with-meta query {::type :map})
    (let [pairs (partition-by keyword? query)]
      (assert (even? (count pairs)))
      (let [items (partition 2 pairs)
            query-map (into {} (map (juxt ffirst second)) items)
            order (into [] (map ffirst items))]
        (with-meta query-map
          {::type :seq
           ::order order})))))

(defn denormalise-query
  "Given the map form return the original form (depending on the metadata)"
  [query]
  (let [{:keys [::type ::order]} (meta query)]
    (condp = type
      :map query
      :seq (let [ks (keys query)
                 missing (set/difference (set ks) (set order))
                 order (into (or order []) missing)]
             (into []
                   (comp (keep (fn [k]
                                 (when-let [v (get query k)]
                                   (into [k] v))))
                         (mapcat identity))
                   order))
      query)))

(defn apply-query
  [query f & args]
  (let [q (normalise-query query)
        m (meta q)]
    (prn q f args)
    (-> f
        (apply q args)
        (with-meta m)
        denormalise-query)))

(defn get-query
  "Get part of a query"
  [query k]
  (-> query
      normalise-query
      (get k)))

(defn assoc-query
  "Assoc into a query"
  [query k v]
  (apply-query query assoc k v))

(defn dissoc-query
  "Dissoc from a query"
  [query k]
  (apply-query query dissoc k))

(defn update-query
  "Dissoc from a query"
  [query k f]
  (apply-query query update k f))
