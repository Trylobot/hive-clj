(ns hive.core.util)

(defn rotate-string-left "removes the first character from a string and appends it to the end"
  [s]
    (str (apply str (rest s)) (first s)))

(defn contains-value? "returns true if value appears in coll"
  [coll value]
    (boolean (some #(= value %) coll)) )

(defn fill "fill coll with v such that (= s (count coll))"
  [coll s v] 
    (let [len (count coll)] (cond
      (= len s) ; no-op
        coll
      (< len s) ; pad
        (apply conj coll 
          (mapv (fn [i] v) (range (- s len))))
      (> len s) ; truncate
        (subvec (vec coll) 0 s) )) )

