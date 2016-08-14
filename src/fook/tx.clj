(ns fook.tx
  (:require [taoensso.truss :refer [have]]))

(def ^:dynamic *tempid* nil)

(def ^:dynamic *entity* nil)

(defn set-tempid!
  [f]
  (have fn? f)
  (alter-var-root #'*tempid* (constantly f)))

(defn set-entity!
  [f]
  (have fn? f)
  (alter-var-root #'*entity* (constantly f)))

(defmacro with-tempid
  [tempid & body]
  `(binding [*tempid* ~tempid]
     ~@body))

(defmacro with-entity
  [entity & body]
  `(binding [*entity* ~entity]
     ~@body))

(defn tempid
  ([partition]
   (have fn? *tempid*)
   (*tempid* partition))
  ([partition n]
   (have fn? *tempid*)
   (*tempid* partition n)))

(defn entity
  [db eid]
  (have fn? *entity*)
  (*entity* db eid))

(defn add-id
  ([e part]
   (if (:db/id (have map? e))
     e
     (assoc e :db/id (tempid part)))))

(defn get-attr
  [db datom]
  (let [[e a v t op] datom
        attr (entity db a)]
    [e (:db/ident attr) v t op]))

(defn get-attrs
  [db tx-data]
  (map (partial get-attr db) tx-data))
