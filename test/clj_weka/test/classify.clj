(ns clj-weka.test.classify
  (:use clj-weka.classify
        clj-data.core
        clj-weka.data
        clojure.test)
  (:import [weka.classifiers.bayes NaiveBayes]))

(deftest NaiveBayesTest
  (let [matrix [[1.4 2 3 5 6] [4.3 5 6 5 6] [7 8 9 8 9] [10 11 12 10 11] [10 11 12 10 11] [10 11 12 10 11] [10 11 12 10 11]]
        data-set (make-data-set "Testing" ["a" "b" "c" "d" "e"] matrix :classifications ["g" "h" "h" "g" "g" "g" "g"])
        instances (convert-data-set data-set)
        classifier (new NaiveBayes)
        evaluation (cross-validation classifier instances 2)]
    (println (. evaluation toClassDetailsString))))


