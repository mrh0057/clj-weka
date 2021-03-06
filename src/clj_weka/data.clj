(ns clj-weka.data
  (:use [clj-data.core :only [get-value count-rows]])
  (:import [weka.core Attribute FastVector Instance Instances]))

(set! *warn-on-reflection* true)

(defprotocol ConvertableDataSet
  (convert-data-set [this]
    "Used to convert the data to weka Instance and Instances.")
  (classifications-of-data-set [this]
    "Used to get the classification of the data set.

this - The data set to get the classifications from
returns - A list of classifications from the data-set sorted."))

(defn- whole-number? [val]
  (= (mod val 1) 0))

(defn- create-attribute-values [vals]
  (let [attribute-values (new FastVector)]
    (doseq [val vals]
      (. attribute-values addElement  val))
    attribute-values))

(defn- create-classification-values [data-set]
  (if (:classifications data-set)
    (new Attribute "class" #^FastVector (create-attribute-values (classifications-of-data-set data-set)))))

(defn create-attribute [data-set position]
  (new Attribute (get (:attributes data-set) position)))

(defn create-instance [data-set attributes row class-attribute classifications]
  (let [number-of-attributes (if class-attribute
                               (dec (count attributes))
                               (count attributes))
        instance (new Instance (if class-attribute
                                 (inc number-of-attributes)
                                 number-of-attributes))]
    (loop [i 0]
      (if (<= number-of-attributes i)
        (do
          (if classifications
            (. instance setValue #^Attribute class-attribute #^String (nth classifications row)))
          instance)
        (do
          (. instance setValue #^Attribute (nth attributes i)
             #^Double (get-value data-set i row))
          (recur (inc i)))))))

(defn- seq-to-fast-vec [seq]
  (let [fast-vec (new FastVector (count seq))]
    (doseq [val seq]
      (. fast-vec addElement val))
    fast-vec))

(extend-type clj_data.core.DataSet
  ConvertableDataSet
  (convert-data-set [data-set]
    (let [number-of-attributes (count (:attributes data-set))
          class-attribute (create-classification-values data-set)
          attributes (vec (loop [i 0
                                 attributes '()]
                            (if (<= number-of-attributes i)
                              (reverse (if class-attribute
                                         (cons class-attribute attributes)
                                         attributes))
                              (recur
                               (inc i)
                               (cons (create-attribute data-set i) attributes)))))
          number-of-rows (count-rows data-set)
          instances (doto (new Instances #^String (:name data-set)
                               #^FastVector (seq-to-fast-vec attributes)
                               #^Integer number-of-rows)
                      (. setClass class-attribute))]
      (loop [i 0]
        (if (<= number-of-rows i)
          instances
          (do
            (. instances add (doto (create-instance data-set  attributes i class-attribute (:classifications data-set))))
            (recur
             (inc i)))))))
  (classifications-of-data-set [this]
    (sort (distinct (:classifications this)))))
