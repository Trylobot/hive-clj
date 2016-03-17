(ns hive.core.util)

(defn rotate-string-left [s] "removes the first character from a string and appends it to the end"
  (str (apply str (rest s)) (first s)))

(defn contains-value? [coll value] "returns true if value appears in coll"
  (boolean (some #(= value %) coll)) )

