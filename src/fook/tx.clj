(ns fook.tx
  (:require [taoensso.truss :refer [have]]))

(def ^:dynamic *tempid* nil)

(defn set-tempid!
  [f]
  (have fn? f)
  (alter-var-root #'*tempid* (constantly f)))

(defmacro with-tempid
  [tempid & body]
  `(binding [*tempid* ~tempid]
     ~@body))


(defn tempid
  ([partition]
   (have fn? *tempid*)
   (*tempid* partition))
  ([partition n]
   (have fn? *tempid*)
   (*tempid* partition n)))

(defn add-id
  ([e part]
   (if (:db/id (have map? e))
     e
     (assoc e :db/id (tempid part)))))
