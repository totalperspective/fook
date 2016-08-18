(ns fook.unify-test
  (:require [fook.unify :as sut]
            [clojure.test :as t]))

(t/deftest unify-result
  (t/testing "Unifying a result"
    (t/is (= (sut/unify-result
              '[:find ?artist-name ?release-name
                :where [?release :release/name ?release-name]
                [?release :release/artists ?artist]
                [?artist :artist/name ?artist-name]]
              #{["George Jones" "With Love"]
                ["Shocking Blue" "Hello Darkness / Pickin' Tomatoes"]
                ["Junipher Greene" "Friendship"]}
              '{:name ?artist-name
                :release ?release-name})
             '({:name "George Jones", :release "With Love"}
               {:name "Shocking Blue", :release "Hello Darkness / Pickin' Tomatoes"}
               {:name "Junipher Greene", :release "Friendship"})))))

(t/deftest unify-query
  (t/testing "Unify entities"
    (t/is (= (sut/unify-query
              '[:find ?x ?y
                :where
                [?p :parent/x ?x]
                [?p :parent/y ?z]
                [?a :ancestor/x ?z]
                [?a :ancestor/y ?y]]
              [[1 :parent/x :a 3 true]
               [1 :parent/y :b 3 true]
               [2 :parent/x :c 3 true]
               [2 :parent/y :d 3 true]])
             '#{{?p 2, ?x :c, ?z :d}
                {?p 1, ?x :a, ?z :b}}))))
