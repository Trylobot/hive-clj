(ns hive.schema.board-schema)
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
  #"\d+,\d+")

(def v1 "board version 1; schema for board model compatible with original, javascript format"
  {"pieces" {
    v1-serialized-position [
      {"color" v1-piece-color, "type" v1-piece-type} ]} })
; {"pieces":{"0,0":[{"color":"White","type":"Beetle"},{"color":"Black","type":"Queen Bee"}]}}

; ---------------------

(def v2-piece-colors "intermediate type; enum for hive piece colors, as keywords"
  (s/enum 
    :white
    :black ))

(def v2-piece-types "intermediate type; enum for hive piece colors, as keywords"
  (s/enum
    :queen-bee
    :beetle
    :grasshopper
    :spider
    :soldier-ant
    :mosquito
    :ladybug
    :pillbug ))

(def v2-position-directions "intermediate type; enum for hive piece relative angles, as integers (degrees clockwise of north)"
  (s/enum
    0
    60
    120
    180
    240
    300 ))

(def v2-piece-colors "intermediate type; enum for hive piece colors, as keywords"
  (apply s/enum (map #(-> % string/lower-case (string/replace " " "-")) piece/colors_enum)))

(def v2 "board version 2; clojure derivative of version 1; position-keys are structures, and keywords where possible"
  {:pieces {
    {:row s/Num, :col s/Num} [
      {:color s/Keyword, :type s/Keyword} ]} })
; {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}}

(def v3 "board version 3; compressed derivative of version 2"
  [[s/Num s/Num] [
    [s/Keyword s/Keyword] ]])
; [[0 0] [[:white :beetle] [:black :queen-bee]]]

(def v4 "board version 4; sequence of piece-stacks packed into a nil-padded spiral"
  [(s/maybe [[s/Keyword s/Keyword]])] )
; [nil [[:white :beetle] [:black :queen-bee]]]

(def v5 "board version 5; sequence of pieces with 3D position vectors"
  {[s/Num s/Num s/Num] [s/Keyword s/Keyword]})
; {[0 0 0] [:white :queen-bee], [0 0 1] [:black :beetle]}

  ; {:a {:b s/Str
  ;      :c s/Int}
  ;  :d [{:e s/Keyword
  ;       :f [s/Num]}]})

; (s/validate
;   Data
;   {:a {:b "abc"
;        :c 123}
;    :d [{:e :bc
;         :f [12.2 13 100]}
;        {:e :bc
;         :f [-1]}]})
;; Success!

; (s/validate
;   Data
;   {:a {:b 123
;        :c "ABC"}})
;; Exception -- Value does not match schema:
;;  {:a {:b (not (instance? java.lang.String 123)),
;;       :c (not (integer? "ABC"))},
;;   :d missing-required-key}

