(ns clj-weka.test.classify
  (:use clj-weka.classify
        clj-data.core
        clj-weka.data
        clojure.test)
  (:import [weka.classifiers.bayes NaiveBayes]
           [weka.classifiers.meta LogitBoost]
           weka.classifiers.trees.DecisionStump
           weka.classifiers.trees.J48))

(deftest parse-options-test
  (let [val (parse-options [:D [:M "MyOpt"]])]
    (is (= (aget val 0) "-D"))
    (is (= (aget val 1) "-M MyOpt")))
  (let [val (parse-options [:D :W])]
    (is (= (aget val 0) "-D"))
    (is (= (aget val 1) "-W")))
  (let [val (parse-options [[:D "Hello"] :W])]
    (is (= (aget val 0) "-D Hello"))
    (is (= (aget val 1) "-W")))
  (let [val (parse-options [[:D 1] :W])]
    (is (= (aget val 0) "-D 1"))))

(deftest find-max-position-test
  (is (= (find-max-position [0 1 2 3 4 5 0]) 5)))

(deftest NaiveBayesTest
  (let [matrix [[1.4 2 3 5 6] [4.3 5 6 5 6] [7 8 9 8 9] [10 11 12 10 11] [10 11 12 10 11] [10 11 12 10 11] [10 11 12 10 11]]
        data-set (make-data-set "Testing" ["a" "b" "c" "d" "e"] matrix :classifications ["g" "h" "h" "g" "g" "g" "h"])
        classifier (new NaiveBayes)
        j48 (new J48)
        params-test (doto (new LogitBoost)
                      (set-options [[:W weka.classifiers.trees.DecisionStump]]))
        evaluation (cross-validation classifier data-set 2)
        params-evaluation (cross-validation params-test data-set 2)]
    (build-classifer classifier data-set)
    (build-classifer j48 data-set)
    (println (to-summary-string params-evaluation))
    (println (classify-by-distribution classifier data-set))
    (is (= (classify classifier data-set) '("g" "h" "h" "g" "g" "g" "g")))
    (is (avg-cost evaluation))
    (is (confusion-matrix evaluation))
    (is (correct evaluation))
    (is (error-rate evaluation))
    (is (false-negative-rate evaluation "g"))
    (is (false-positive-rate evaluation "g"))
    (is (f-measure evaluation "g"))
    (is (incorrect evaluation))
    (is (kappa evaluation))
    (is (kb-information evaluation))
    (is (kb-mean-information evaluation))
    (is (mean-absolute-error evaluation))
    (is (mean-prior-absolute-error evaluation))
    (is (num-false-positives evaluation "g"))
    (is (num-false-negative evaluation "g"))
    (is (num-true-negativies evaluation "g"))
    (is (num-true-negativies evaluation "g"))
    (is (percent-correct evaluation))
    (is (percent-incorrect evaluation))
    (is (percent-unclassified evaluation))
    (is (precision evaluation "g"))
    (is (prior-entropy evaluation))
    (is (recall evaluation "g"))
    (is (relative-absolute-error evaluation))
    (is (root-mean-squared-error evaluation))
    (is (root-mean-prior-squared-error evaluation))
    (is (root-relative-squared-error evaluation))
    (is (sf-entropy-gain evaluation))
    (is (sf-mean-entropy-gain evaluation))
    (is (sf-mean-prior-entropy evaluation))
    (is (sf-mean-scheme-entropy evaluation))
    (is (to-class-details-string evaluation))
    (is (to-matrix-string evaluation))
    (is (total-cost evaluation))
    (is (true-negative-rate evaluation "g"))
    (is (true-positive-rate evaluation "g"))
    (is (unclassified evaluation))
    (save-classifier classifier "my-file.model"))
)
