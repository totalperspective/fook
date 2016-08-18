(ns fook.schema-test
  (:require [fook.schema :as sut]
            [clojure.test :as t]
            [fook.tx :as tx]
            [datomic.api :as d]))

(t/deftest schema
  (tx/with-tempid
    identity
    (t/testing "Big test (make smaller"
      (t/is (= (sut/schema
                [[:foo :string :index]
                 [:foo/bar :ref :many]
                 [:foo/baz :uuid :identity]]
                (sut/attr-ns :ns
                             [:foo :string "This is a string" :index]
                             [:foo/bar :ref :component]
                             [:foo/baz :uuid :unique]))

               '({:db/cardinality :db.cardinality/one,
                  :db.install/_attribute :db.part/db,
                  :db/ident :ns.foo/baz,
                  :db/unique :db.unique/value,
                  :db/valueType :db.type/uuid,
                  :db/id :db.part/db}
                 {:db/cardinality :db.cardinality/one,
                  :db.install/_attribute :db.part/db,
                  :db/ident :ns.foo/bar,
                  :db/isComponent true,
                  :db/valueType :db.type/ref,
                  :db/id :db.part/db}
                 {:db/cardinality :db.cardinality/one,
                  :db.install/_attribute :db.part/db,
                  :db/ident :ns/foo,
                  :db/doc "This is a string",
                  :db/index true,
                  :db/valueType :db.type/string,
                  :db/id :db.part/db}
                 {:db/cardinality :db.cardinality/one,
                  :db.install/_attribute :db.part/db,
                  :db/ident :foo,
                  :db/index true,
                  :db/valueType :db.type/string,
                  :db/id :db.part/db}
                 {:db/cardinality :db.cardinality/many,
                  :db.install/_attribute :db.part/db,
                  :db/ident :foo/bar,
                  :db/valueType :db.type/ref,
                  :db/id :db.part/db}
                 {:db/cardinality :db.cardinality/one,
                  :db.install/_attribute :db.part/db,
                  :db/ident :foo/baz,
                  :db/unique :db.unique/identity,
                  :db/valueType :db.type/uuid,
                  :db/id :db.part/db}))))))
