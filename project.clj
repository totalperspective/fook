(defproject totalperspective/fook "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/truss "1.2.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]]
  :profiles
  {:dev {:dependencies [[com.datomic/datomic-free "0.9.5390"]]}})
