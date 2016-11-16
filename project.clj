(defproject totalperspective/fook "0.3.0"
  :description "Small utilities for datomic"
  :url "http://github.com/totalperspective/fook"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.taoensso/truss "1.3.6"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/core.unify "0.5.7"]]
  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                        [com.datomic/datomic-free "0.9.5390"]]}})
