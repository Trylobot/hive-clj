(ns hive.core.schema.board-schema)
(require '[clojure.string :as string])
(require '[schema.core :as s])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])

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

(def v1 "board version 1; schema for board model compatible with original, javascript format"
  {(s/required-key "pieces") (s/maybe {
    v1-serialized-position [
      {(s/required-key "color") v1-piece-color, (s/required-key "type") v1-piece-type} ] })} )
; {"pieces":{"0,0":[{"color":"White","type":"Beetle"},{"color":"Black","type":"Queen Bee"}]}}

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

(def v2-position-direction "intermediate type; enum for hive piece relative angles, as integers (degrees clockwise of north)"
  (s/enum
    0
    60
    120
    180
    240
    300 ))

(def v2 "board version 2; clojure derivative of version 1; position-keys are structures, and keywords where possible"
  {:pieces (s/maybe {
    {:row s/Int, :col s/Int} [
      {:color v2-piece-color, :type v2-piece-type} ] })} )
; {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}}

(defn convert-v1-to-v2 [v1-board]
  nil )

; ---------------------

;(def v3 "board version 3; compressed derivative of version 2"
;  (s/maybe [[s/Int s/Int] [
;    [v2-piece-color v2-piece-type] ]]) )
;; [[0 0] [[:white :beetle] [:black :queen-bee]]]


