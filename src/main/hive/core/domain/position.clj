(ns hive.core.domain.position)
(require '[clojure.string :as str])

; position
;   represents a position (at table-level) of a hive piece or stack of pieces (height of stack is ignored)

(def directions_enum #{
  0
  60
  120
  180
  240
  300 
})

(defn create [row col]
  {:pre [(or 
    (and (even? row) (even? col)) 
    (and (odd? row) (odd? col)) )]}
  {:row row, :col col})

(defn translation [position direction]
  (case direction
    0   (create (+ (position :row) -2) (+ (position :col) +0) )
    60  (create (+ (position :row) -1) (+ (position :col) +1) )
    120 (create (+ (position :row) +1) (+ (position :col) +1) )
    180 (create (+ (position :row) +2) (+ (position :col) -0) )
    240 (create (+ (position :row) +1) (+ (position :col) -1) )
    300 (create (+ (position :row) -1) (+ (position :col) -1) )))

(defn adjacencies [position]
  (map #(translation position %) directions_enum))
