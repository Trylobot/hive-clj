(ns hive.core.schema.position-schema)
(require '[clojure.string :as str])
(require '[schema.core :as s])
(require '[hive.core.domain.position :as position])

(def v1-position-direction "intermediate type; enum for hive piece relative angles, as strings"
  (s/enum
    "12 o'clock"
    "2 o'clock"
    "4 o'clock"
    "6 o'clock"
    "8 o'clock"
    "10 o'clock" ))

(def v1-serialized-position "intermediate type; enum for serialized positions"
  (s/pred #(re-matches #"\d+,\d+" %)))

; ---------------------

(def v2-position-direction "intermediate type; enum for hive piece relative angles, as integers (degrees clockwise of north)"
  (s/enum
    0
    60
    120
    180
    240
    300 ))

(defn upgrade-v1-to-v2 [v1-position]
  (apply position/create (map #(Long. %) (str/split v1-position #","))) )

(defn revert-v2-to-v1 [v2-position]
  (str/join [(v2-position :row) "," (v2-position :col)]) )

