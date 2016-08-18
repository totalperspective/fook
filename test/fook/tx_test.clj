(ns fook.tx-test
  (:require [fook.tx :as sut]
            [clojure.test :as t]
            [datomic.api :as d]))

(t/deftest using-tempids
  (sut/with-tempid
    d/tempid
    (t/testing "Auto generation"
      (let [e (sut/add-id {:foo "bar"} :db.part/user)]
        (t/is (= (get e :foo) "bar"))
        (t/is (= (get-in e [:db/id :part]) :db.part/user))
        (t/is (neg? (get-in e [:db/id :idx])))))
    (t/testing "Provided id"
      (let [id (datomic.api/tempid :db.part/user -1)
            e (sut/add-id {:foo "bar"
                           :db/id id}
                          nil)]
        (t/is (= (get e :foo) "bar"))
        (t/is (= (get e :db/id) id))))
    (t/testing "Vector form with n"
      (let [e (sut/add-id {:foo "bar"
                           :db/id [:db.part/user]}
                          nil)]
        (t/is (= (get e :foo) "bar"))
        (t/is (= (get-in e [:db/id :part]) :db.part/user))
        (t/is (neg? (get-in e [:db/id :idx])))))
    (t/testing "Vector form with n"
      (let [e (sut/add-id {:foo "bar"
                           :db/id [:db.part/user -2]}
                          nil)]
        (t/is (= (get e :foo) "bar"))
        (t/is (= (get-in e [:db/id :part]) :db.part/user))
        (t/is (= (get-in e [:db/id :idx]) -2))))))


(t/deftest get-attrs
  (sut/with-entity
    d/entity
    (let [conn (let [uri (str "datomic:mem://" (d/squuid))]
                 (d/create-database uri)
                 (d/connect uri))
          db (d/db conn)
          datoms (take 10 (d/datoms db :eavt))]
      (t/testing "Convert datoms"
        (t/is (= (sut/get-attrs db datoms)
                 '([0 :db/ident :db.part/db 13194139533312 true]
                   [0 :db.install/partition 0 13194139533366 true]
                   [0 :db.install/partition 3 13194139533366 true]
                   [0 :db.install/partition 4 13194139533366 true]
                   [0 :db.install/valueType 20 13194139533366 true]
                   [0 :db.install/valueType 21 13194139533366 true]
                   [0 :db.install/valueType 22 13194139533366 true]
                   [0 :db.install/valueType 23 13194139533366 true]
                   [0 :db.install/valueType 24 13194139533366 true]
                   [0 :db.install/valueType 25 13194139533366 true])))))))
