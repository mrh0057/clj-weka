(ns clj-weka.classify
  (:import [weka.classifiers Evaluation Classifier]
           [weka.core Instances]
           [java.util Random Date]))


(defprotocol ClassifierProtocol
  (cross-validation [classifier instances folds]))

(extend-type Classifier
  ClassifierProtocol
  (cross-validation [classifier instances folds]
    (let [evaluation (new Evaluation instances)]
      (doto evaluation
        (. crossValidateModel #^Classifier classifier #^Instances instances #^Integer folds #^Random (new Random (. (new Date) getTime ))
           (into-array []))))))
