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
  {:row row :col col})

