(ns fook.schema
  (:require [datomic.api :as d]
            [taoensso.truss :refer [have]]
            [clojure.core.match :refer [match]]
            [fook.tx :as tx]))

(def default-attrs
  {:db/cardinality :db.cardinality/one
   :db.install/_attribute :db.part/db})

(defn full-type [kw]
  (when-not (= :enum kw)
    (if (namespace (have keyword? kw))
      kw
      (keyword "db.type" (name kw)))))

(defn full-arg [a]
  (match
   [a]
   [(:or :one :many)] [:db/cardinality (keyword "db.cardinality" (name a))]
   [:unique] [:db/unique :db.unique/value]
   [:identity] [:db/unique :db.unique/identity]
   [:index] [:db/index true]
   [:component] [:db/isComponent true]
   [(a :guard string?)] [:db/doc a]))

(defn make-attr [ident type & args]
  (let [ident (have keyword? ident)
        value-type (full-type type)
        attr (into {:db/ident ident}
                   (map full-arg args))]
    (if value-type
      (assoc attr :db/valueType value-type)
      attr)))

(defn attr [x]
  (have [:or map? sequential?] x)
  (with-meta
    (cond
      (:db/id x) x
      (map? x) (tx/add-id (merge default-attrs x) :db.part/db)
      (sequential? x) (attr (apply make-attr x)))
    (meta x)))

(defn attr-ns [ns & attrs]
  (map (fn [s]
         (let [[ident & args] s
               ident-ns (namespace ident)
               ns-name (name ns)
               ns (if ident-ns
                    (str ns-name "." ident-ns)
                    ns-name)
               ident (keyword ns (name ident))]
           (with-meta (apply vector ident args) (meta s))))
       attrs))

(defn ident
  [n]
  [(tx/add-id {:db/ident n} :db.part/db)])

(defn part
  [n]
  (let [is (ident n)]
    (map #(assoc % :db.install/_partition :db.part/db)
         is)))

(defn schema
  ([s]
   (map attr s))
  ([s & schemas]
   (into (schema s)
         (apply schema schemas))))
