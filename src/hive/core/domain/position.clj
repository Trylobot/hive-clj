(ns hive.core.domain.position)
(require '[clojure.string :as str])
(require '[hive.core.util :refer :all])

; position
;   represents a position (at table-level) of a hive piece or stack of pieces (height of stack is ignored)

; so named because it represents an ordered list of unit vectors
;   pointing in each of six "cardinal" directions.
; i.e., the directions from grid (0,0) to each of its six neighbor hexes (refer to "doc/grid.png").
(def direction-vectors "the six cardinal directions of a single unit of discrete piece movement" [
  0   ; 12 o'clock, north
  60  ;  2 o'clock, north-east
  120 ;  4 o'clock, south-east
  180 ;  6 o'clock, south
  240 ;  8 o'clock, south-west
  300 ; 10 o'clock, north-west
])

(defn create "create a valid position on the grid — see doc/grid.png"
  [row col]
    {:pre [(or (and (even? row) (even? col)) 
               (and (odd?  row) (odd?  col)) )]}
    {:row row, :col col})

(def origin (create 0 0))

(defn translation "return result of applying direction vector to position"
  [position direction]
    {:pre [(map? position)
           (contains? position :row) (contains? position :col)
           (contains-value? direction-vectors direction)]}
    (let [{row :row, col :col} position]
      (case direction
        0   (create (+ row -2) (+ col +0))
        60  (create (+ row -1) (+ col +1))
        120 (create (+ row +1) (+ col +1))
        180 (create (+ row +2) (+ col -0))
        240 (create (+ row +1) (+ col -1))
        300 (create (+ row -1) (+ col -1)) )))

(defn rotation-clockwise "return result of applying a single unit of clockwise rotation to direction vector"
  [direction]
    (case direction
      0   60
      60  120
      120 180
      180 240
      240 300
      300 0   ))

(defn rotation-counter-clockwise "return result of applying a single unit of counter-clockwise rotation to direction vector"
  [direction]
    (case direction
      0   300
      60  0
      120 60
      180 120
      240 180
      300 240 ))

(defn rotation "rotate direction vector either :cw clockwise or :ccw counter-clockwise"
  [direction rotation-direction]
    (case rotation-direction
      :cw (rotation-clockwise direction)
      :ccw (rotation-counter-clockwise direction)) )

(defn adjacencies "return list of positions representing the immediate adjacencies of position"
  [position]
    (map #(translation position %) direction-vectors))

