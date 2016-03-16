(ns hive.core.util)

(defn contains-value? [coll value]
  (boolean (some #(= value %) coll)) )

