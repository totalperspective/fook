(defproject totalperspective/fook "0.2.1"
  :description "Small utilities for datomic"
  :url "http://github.com/totalperspective/fook"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/truss "1.2.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.unify "0.5.7"]]
  :profiles
  {:dev {:dependencies [[com.datomic/datomic-free "0.9.5390"]]}})
