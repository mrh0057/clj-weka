(defproject clj-weka "0.0.1-SNAPSHOT"
  :description "A wrapper for clojure over the weka library."
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [clj-data "0.0.1-SNAPSHOT"]
                 [weka/weka "3.6.5"]
                 [midje "1.1.1" :exclusions [org.clojure/clojure
                                             org.clojure.contrib/core]]]
  :dev-dependencies [[lein-midje "1.0.3"]
                     [lein-marginalia "0.6.0"]
                     [swank-clojure "1.3.1"]])
