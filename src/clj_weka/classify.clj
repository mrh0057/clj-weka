(ns clj-weka.classify
  (:use clj-weka.data)
  (:import [weka.classifiers Evaluation Classifier]
           [weka.core Instances Attribute]
           [java.util Random Date]
           [java.io ObjectOutputStream FileOutputStream ObjectInputStream FileInputStream]))


(defprotocol ClassifierProtocol
  (cross-validation [classifier data-set folds])
  (build-classifer [classifier data-set])
  (save-classifier [classifier file]))

(defrecord EvaluationRecord [evaluation
                             classes])

(defprotocol EvaluationProtocol
  (avg-cost [this])
  (confusion-matrix [this])
  (correct [this])
  (correlation-coefficient [this])
  (error-rate [this])
  (false-negative-rate [this classification])
  (false-positive-rate [this classification])
  (f-measure [this classification])
  (incorrect [this])
  (kappa [this])
  (kb-information [this])
  (kb-mean-information [this])
  (kb-relative-information [this])
  (mean-absolute-error [this])
  (mean-prior-absolute-error [this])
  (num-false-negative [this classification])
  (num-false-positives [this classification])
  (num-true-negativies [this classification])
  (num-true-positives [this classification])
  (percent-correct [this])
  (percent-incorrect [this])
  (percent-unclassified [this])
  (precision [this classification])
  (prior-entropy [this])
  (recall [this classification])
  (relative-absolute-error [this])
  (root-mean-squared-error [this])
  (root-mean-prior-squared-error [this])
  (root-relative-squared-error [this])
  (sf-entropy-gain [this])
  (sf-mean-entropy-gain [this])
  (sf-mean-prior-entropy [this])
  (sf-mean-scheme-entropy [this])
  (sf-prior-entropy [this])
  (sf-scheme-entropy [this])
  (to-class-details-string [this])
  (to-cumulative-margin-distribution-string [this])
  (to-matrix-string [this])
  (to-summary-string [this])
  (total-cost [this])
  (true-negative-rate [this classification])
  (true-positive-rate [this classification])
  (unclassified [this]))

(defn- get-classification-position [evaluation classification]
  (. #^Attribute (:classes evaluation) indexOfValue classification))

(extend-type EvaluationRecord
  EvaluationProtocol
  (avg-cost [this] (. #^Evaluation (:evaluation this) avgCost))
  (confusion-matrix [this] (. #^Evaluation (:evaluation this) confusionMatrix))
  (correct [this] (. #^Evaluation (:evaluation this) correct))
  (correlation-coefficient [this] (. #^Evaluation (:evaluation this) correlationCoefficient))
  (error-rate [this] (. #^Evaluation (:evaluation this) errorRate))
  (false-negative-rate [this classification] (. #^Evaluation (:evaluation this) falseNegativeRate (get-classification-position this classification)))
  (false-positive-rate [this classification] (. #^Evaluation (:evaluation this) falsePositiveRate (get-classification-position this classification)))
  (f-measure [this classification] (. #^Evaluation (:evaluation this) fMeasure (get-classification-position this classification)))
  (incorrect [this] (. #^Evaluation (:evaluation this) incorrect))
  (kappa [this] (. #^Evaluation (:evaluation this) kappa))
  (kb-information [this] (. #^Evaluation (:evaluation this) KBInformation))
  (kb-mean-information [this] (. #^Evaluation (:evaluation this) KBMeanInformation))
  (mean-absolute-error [this] (. #^Evaluation (:evaluation this) meanAbsoluteError))
  (mean-prior-absolute-error [this] (. #^Evaluation (:evaluation this) meanPriorAbsoluteError))
  (num-false-negative [this classification] (. #^Evaluation (:evaluation this) numFalseNegatives (get-classification-position this classification)))
  (num-false-positives [this classification] (. #^Evaluation (:evaluation this) numFalsePositives (get-classification-position this classification)))
  (num-true-negativies [this classification] (. #^Evaluation (:evaluation this) numTrueNegatives (get-classification-position this classification)))
  (num-true-positives [this classification] (. #^Evaluation (:evaluation this) numTruePositives (get-classification-position this classification)))
  (percent-correct [this] (. #^Evaluation (:evaluation this) pctCorrect))
  (percent-incorrect [this] (. #^Evaluation (:evaluation this) pctIncorrect))
  (percent-unclassified [this] (. #^Evaluation (:evaluation this) pctUnclassified))
  (precision [this classification] (. #^Evaluation (:evaluation this) precision (get-classification-position this classification)))
  (prior-entropy [this] (. #^Evaluation (:evaluation this) priorEntropy))
  (recall [this classification] (. #^Evaluation (:evaluation this) recall (get-classification-position this classification)))
  (relative-absolute-error [this] (. #^Evaluation (:evaluation this) relativeAbsoluteError))
  (root-mean-squared-error [this] (. #^Evaluation (:evaluation this) rootMeanSquaredError))
  (root-mean-prior-squared-error [this] (. #^Evaluation (:evaluation this) rootMeanPriorSquaredError))
  (root-relative-squared-error [this] (. #^Evaluation (:evaluation this) rootRelativeSquaredError))
  (sf-entropy-gain [this] (. #^Evaluation (:evaluation this) SFEntropyGain))
  (sf-mean-entropy-gain [this] (. #^Evaluation (:evaluation this) SFMeanEntropyGain))
  (sf-mean-prior-entropy [this] (. #^Evaluation (:evaluation this) SFMeanPriorEntropy))
  (sf-mean-scheme-entropy [this] (. #^Evaluation (:evaluation this) SFMeanSchemeEntropy))
  (to-class-details-string [this] (. #^Evaluation (:evaluation this) toClassDetailsString))
  (to-matrix-string [this] (. #^Evaluation (:evaluation this) toMatrixString))
  (to-summary-string [this] (. #^Evaluation (:evaluation this) toSummaryString))
  (total-cost [this] (. #^Evaluation (:evaluation this) totalCost))
  (true-negative-rate [this classification] (. #^Evaluation (:evaluation this) truePositiveRate (get-classification-position this classification)))
  (true-positive-rate [this classification] (. #^Evaluation (:evaluation this) trueNegativeRate (get-classification-position this classification)))
  (unclassified [this] (. #^Evaluation (:evaluation this) unclassified))
)

(extend-type Classifier
  ClassifierProtocol
  (cross-validation [classifier data-set folds]
    (let [instances (convert-data-set data-set)
          evaluation (new Evaluation instances)]
      (new EvaluationRecord (doto evaluation
                              (. crossValidateModel #^Classifier classifier #^Instances instances #^Integer folds #^Random (new Random (. (new Date) getTime ))
                                 (into-array [])))
           (. instances classAttribute))))
  (save-classifier [classifier file]
    (doto (new ObjectOutputStream (new FileOutputStream file))
      (. writeObject classifier)
      (. flush)
      (. close)))
  (build-classifer [classifier data-set]
    (let [instances (convert-data-set data-set)]
      (. classifier buildClassifier instances)
      classifier)))

(defn load-classifier [path]
  (let [object-stream (new ObjectInputStream (new FileInputStream path))
        classifier (. object-stream readObject)]
    (. object-stream close)
    classifier))
