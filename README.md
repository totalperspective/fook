# Fook

> "O Deep Thought computer," he said, "the task we have designed you to perform is this. We want you to tell us...." he paused, "The Answer."

> "The Answer?" said Deep Thought. "The Answer to what?"

> "Life!" urged Fook.

> "The Universe!" said Lunkwill.

> "Everything!" they said in chorus.

Fook is a collection of functions that enable you to work with Datomic more easily.

## Usage

### Queries

This `fook.query` namespace enables you to easily manipulate queries.

```clojure

((juxt identity meta)
 (fook.query/normalise-query
  '[:find [?name ...]
    :in $ ?artist
    :where
    [?release :release/name ?name]
    [?release :release/artists ?artist]]))
=>
[{:find ([?name ...]),
  :in ($ ?artist),
  :where ([?release :release/name ?name]
          [?release :release/artists ?artist])}
 
 {:fook.query/type :seq, :fook.query/order [:find :in :where]}]

((juxt identity meta)
 (fook.query/normalise-query
  '{:find [[?name ...]]
    :in [$ ?artist]
    :where [[?release :release/name ?name]
            [?release :release/artists ?artist]]}))
=>
[{:find [[?name ...]],
  :in [$ ?artist],
  :where [[?release :release/name ?name]
          [?release :release/artists ?artist]]}
 
 {:fook.query/type :map}]

(fook.query/denormalise-query
 (fook.query/normalise-query
  '[:find [?name ...]
    :in $ ?artist
    :where
    [?release :release/name ?name]
    [?release :release/artists ?artist]]))
=>
[:find [?name ...]
 :in $ ?artist
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]]

(fook.query/get-query
 '[:find [?name ...]
   :in $ ?artist
   :where
   [?release :release/name ?name]
   [?release :release/artists ?artist]] :in)
=>
($ ?artist)

(fook.query/assoc-query
 '[:find [?name ...]
   :in $
   :where 
   [?release :release/name ?name]
   [?release :release/artists ?artist]]
 :in
 '[$])
=>
[:find [?name ...]
 :in $ ?artist
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]]

(fook.query/assoc-query
 '[:find (count ?name)
   :in $ ?artist
   :where
   [?release :release/name ?name]
   [?release :release/artists ?artist]]
 :with
 '[?artist])
=>
[:find (count ?name)
 :in $ ?artist
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]
 :with ?artist]

(fook.query/dissoc-query
 '[:find [?name ...]
   :in $ ?artist
   :where 
   [?release :release/name ?name]
   [?release :release/artists ?artist]] :in)
=>
[:find [?name ...]
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]]

(fook.query/update-query
 '[:find [?name ...]
   :in $ ?artist
   :where
   [?release :release/name ?name]
   [?release :release/artists ?artist]]
 :find
 (comp vector ffirst))
=>
[:find ?name
 :in $ ?artist
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]]

```

### Transactions

### Schema

Provides a shorthand way of defining schemas.

```clojure

(fook.tx/with-tempid
  datomic.api/tempid
  (fook.schema/schema
   [[:foo :string :index]
    [:foo/bar :ref :many]
    [:foo/baz :uuid :identity]]
   (fook.schema/attr-ns :ns
                        [:foo :string "This is a string" :index]
                        [:foo/bar :ref :component]
                        [:foo/baz :uuid :unique])))
=>
({:db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db,
  :db/ident :ns.foo/baz,
  :db/unique :db.unique/value,
  :db/valueType :db.type/uuid,
  :db/id {:part :db.part/db, :idx -1000037}}
 {:db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db,
  :db/ident :ns.foo/bar,
  :db/isComponent true,
  :db/valueType :db.type/ref,
  :db/id {:part :db.part/db, :idx -1000036}}
 {:db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db,
  :db/ident :ns/foo,
  :db/doc "This is a string",
  :db/index true,
  :db/valueType :db.type/string,
  :db/id {:part :db.part/db, :idx -1000032}}
 {:db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db,
  :db/ident :foo,
  :db/index true,
  :db/valueType :db.type/string,
  :db/id {:part :db.part/db, :idx -1000033}}
 {:db/cardinality :db.cardinality/many,
  :db.install/_attribute :db.part/db,
  :db/ident :foo/bar,
  :db/valueType :db.type/ref,
  :db/id {:part :db.part/db, :idx -1000034}}
 {:db/cardinality :db.cardinality/one,
  :db.install/_attribute :db.part/db,
  :db/ident :foo/baz,
  :db/unique :db.unique/identity,
  :db/valueType :db.type/uuid,
  :db/id {:part :db.part/db, :idx -1000035}})

```

### Unification


## License

Copyright © 2016 Bahul Neel Upadhyaya

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
