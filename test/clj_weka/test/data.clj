(ns clj-weka.test.data
  (:use clojure.test
        clj-weka.data
        clj-data.core
        midje.sweet))

(set! *warn-on-reflection* false)

(deftest create-attribute-nominal-test
  (let [matrix [[1 2 3] [4 5 6] [7 8 9]]
        data-set (make-data-set "Testing" ["a" "b" "c"] matrix)
        attribute (create-attribute data-set 0)]
    (is (. attribute isNumeric))))

(deftest create-attribute-numeric-test
  (let [matrix [[1.4 2 3] [4.3 5 6] [7 8 9] [10 11 12]]
        data-set (make-data-set "Testing" ["a" "b" "c"] matrix :classifications ["g" "h" "h" "g"])
        instances (convert-data-set data-set)]
    (println instances)))
