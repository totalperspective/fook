(ns fook.query-test
  (:require [fook.query :as sut]
            [clojure.test :as t]))

(t/deftest normalisation-tests
  (t/testing "Vector to map"
    (let [query '[:find [?name ...]
                  :in $ ?artist
                  :where
                  [?release :release/name ?name]
                  [?release :release/artists ?artist]]
          query' (sut/normalise-query query)]
      (t/is (= query'
               '{:find ([?name ...]),
                 :in ($ ?artist),
                 :where ([?release :release/name ?name]
                         [?release :release/artists ?artist])}))
      (t/is (= (meta query')
               {:fook.query/type :seq, :fook.query/order [:find :in :where]}))))
  (t/testing "Map to map"
    (let [query '{:find [[?name ...]]
                  :in [$ ?artist]
                  :where [[?release :release/name ?name]
                          [?release :release/artists ?artist]]}
          query' (sut/normalise-query query)]
      (t/is (= query'
               query))
      (t/is (= (meta query')
               {:fook.query/type :map}))))
  (t/testing "denormalisation"
    (let [query '[:find [?name ...]
                  :in $ ?artist
                  :where
                  [?release :release/name ?name]
                  [?release :release/artists ?artist]]
          query' (sut/normalise-query query)]
      (t/is (= (sut/denormalise-query query')
               query)))))


(t/deftest query-interface
  (let [query '[:find [?name ...]
                :in $ ?artist
                :where
                [?release :release/name ?name]
                [?release :release/artists ?artist]]]
    (t/testing "Get query"
      (t/is (= (sut/get-query query :find)
               '([?name ...])))
      (t/is (= (sut/get-query query :in)
               '($ ?artist))))
    (t/testing "Assoc query"
      (t/is (= (sut/assoc-query query :in '[$])
               '[:find [?name ...]
                 :in $
                 :where
                 [?release :release/name ?name]
                 [?release :release/artists ?artist]]))
      (t/is (= (sut/assoc-query query :with '[?artist])
               '[:find [?name ...]
                 :in $ ?artist
                 :where
                 [?release :release/name ?name]
                 [?release :release/artists ?artist]
                 :with ?artist])))
    (t/testing "Dissoc query"
      (t/is (= (fook.query/dissoc-query query :in)
               '[:find [?name ...]
                 :where
                 [?release :release/name ?name]
                 [?release :release/artists ?artist]])))
    (t/testing "Update query"
      (t/is (= (fook.query/update-query query :find (comp vector ffirst))
               '[:find ?name
                 :in $ ?artist
                 :where
                 [?release :release/name ?name]
                 [?release :release/artists ?artist]])))))
