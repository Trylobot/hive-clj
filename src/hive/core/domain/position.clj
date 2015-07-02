(ns hive.core.domain.position)

(def directions_enum [
  "12 o'clock"
  "2 o'clock"
  "4 o'clock"
  "6 o'clock"
  "8 o'clock"
  "10 o'clock"
])

(defn create [row col]
  {:row row, :col col})

(defn translation [position direction]
  (case direction
    "12 o'clock" (create (+ (position :row) -2) (+ (position :col) +0) )
    "2 o'clock"  (create (+ (position :row) -1) (+ (position :col) +1) )
    "4 o'clock"  (create (+ (position :row) +1) (+ (position :col) +1) )
    "6 o'clock"  (create (+ (position :row) +2) (+ (position :col) -0) )
    "8 o'clock"  (create (+ (position :row) +1) (+ (position :col) -1) )
    "10 o'clock" (create (+ (position :row) -1) (+ (position :col) -1) )))

