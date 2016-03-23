(ns hive.core.domain.range)
(use 'hive.core.util)

(def default-range "special range that represents no real restrictions"
  {:min 0, :max :infinity})

(defn is-range? "is the given value a simple range? if so, return the normalized form"
  [r] (cond
    (nil? r) ; nil --> {:min 0, :max :infinity}
      default-range
    (number? r) ; 3 --> {:min 3, :max 3}
      {:min r, :max r}
    (and (vector? r)
         (= 2 (count r))
         (number? (nth r 0))
         (number? (nth r 1))) ; [0, 3] --> {:min 0, :max 3}
      {:min (apply min r), :max (apply max r)}
    (and (map? r)
         (contains? r :min)
         (contains? r :max)) ; {:min 0, :max 3} --> identity
      {:min (min (:min r) (:max r)), :max (max (:min r) (:max r))} ))

(defn is-range-seq? "is the given value a sequence of simple ranges? if so, return the normalized forms of each; gaps filled by default"
  ([s] (cond
    (or (vector? s) (seq? s))
      (mapv #(is-range? %) s) ))
  ([s, d] (cond
    (is-range? s)
      (mapv (fn [i] (is-range? s)) (range d))
    (or (vector? s) (seq? s))
      (mapv #(is-range? %) (fill s d default-range)) )) )

