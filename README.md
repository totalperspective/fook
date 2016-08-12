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

comment
(fook.query/get-query
 '[:find [?name ...]
   :in $ ?artist
   :where [?release :release/name ?name]
   [?release :release/artists ?artist]] :in)

($ ?artist)

(fook.query/assoc-query
 '[:find [?name ...]
   :in $ ?artist
   :where [?release :release/name ?name]
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
   :where [?release :release/name ?name]
   [?release :release/artists ?artist]] :in)
=>
[:find [?name ...]
 :where
 [?release :release/name ?name]
 [?release :release/artists ?artist]]

(fook.query/apply-query
 '[:find [?name ...]
   :in $ ?artist
   :where
   [?release :release/name ?name]
   [?release :release/artists ?artist]]
 update
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

### Unification


## License

Copyright © 2016 Bahul Neel Upadhyaya

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
