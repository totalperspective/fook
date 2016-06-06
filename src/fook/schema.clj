(ns fook.schema
  (:require [datomic.api :as d]
            [taoensso.truss :refer [have]]
            [clojure.core.match :refer [match]]))

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

(defn add-id [attr part]
  (if (:db/id (have map? attr))
    attr
    (assoc attr :db/id (d/tempid part))))

(defn attr [x]
  (have [:or map? sequential?] x)
  (cond
    (map? x) (add-id (merge default-attrs x) :db.part/db)
    (sequential? x) (attr (apply make-attr x))))

(defn attr-ns [ns & attrs]
  (map (fn [[ident & args]]
         (let [ident-ns (namespace ident)
               ns-name (name ns)
               ns (if ident-ns
                    (str ns-name "." ident-ns)
                    ns-name)
               ident (keyword ns (name ident))]
           (apply vector ident args)))
       attrs))

(defn schema
  ([s]
   (map attr s))
  ([s & schemas]
   (into (schema s)
         (apply schema schemas))))
