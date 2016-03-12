(ns hive.core.schema.piece-schema)
(require '[clojure.string :as str])
(require '[schema.core :as s])
(require '[hive.core.domain.piece :as piece])

(def v1-piece-color "intermediate type; enum for hive piece colors, as strings"
  (s/enum 
    "White"
    "Black" ))

(def v1-piece-type "intermediate type; enum for hive piece colors, as strings"
  (s/enum
    "Queen Bee"
    "Beetle"
    "Grasshopper"
    "Spider"
    "Soldier Ant"
    "Mosquito"
    "Ladybug"
    "Pillbug" ))

; ---------------------

(def v2-piece-color "intermediate type; enum for hive piece colors, as keywords"
  (s/enum 
    :white
    :black ))

(def v2-piece-type "intermediate type; enum for hive piece colors, as keywords"
  (s/enum
    :queen-bee
    :beetle
    :grasshopper
    :spider
    :soldier-ant
    :mosquito
    :ladybug
    :pillbug ))

; ---------------------

(defn upgrade-v1-to-v2 [v1-piece] {
  :color (keyword (str/lower-case (v1-piece "color")))
  :type (keyword (str/replace (str/lower-case (v1-piece "type")) " " "-")) })


