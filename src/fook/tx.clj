(ns fook.tx
  (:require [taoensso.truss :refer [have]]
            [datomic.api :as d]))

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
  ([e default-part]
   (if-let [id (:db/id (have map? e))]
     (if (sequential? id)
       (let [[part n] id
             id (if n
                  (tempid part n)
                  (tempid part) )]
         (assoc e :db/id id))
       e)
     (assoc e :db/id (tempid default-part)))))

(defn get-attr
  [db datom]
  (let [[e a v t op] datom
        attr (entity db a)]
    [e (or (:db/ident attr) a) v t op]))

(defn get-attrs
  "Given db and a sequence of datoms, returns a lazy sequence of datoms with
  their attribute names looked up in db"
  [db datoms]
  (map (partial get-attr db) datoms))
