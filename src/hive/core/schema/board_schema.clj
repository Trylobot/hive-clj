(ns hive.core.schema.board-schema)
(require '[clojure.string :as str])
(require '[schema.core :as s])

(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.board :as board])

(require '[hive.core.schema.piece-schema :as piece-schema])
(require '[hive.core.schema.position-schema :as position-schema])


; {"pieces":{"0,0":[{"color":"White","type":"Beetle"},{"color":"Black","type":"Queen Bee"}]}}
(def v1 "board version 1; schema for board model compatible with original, javascript format"
  {(s/required-key "pieces") (s/maybe {
    position-schema/v1-serialized-position [{
      (s/required-key "color") piece-schema/v1-piece-color, 
      (s/required-key "type") piece-schema/v1-piece-type }] })} )

; ---------------------

; {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}}
(def v2 "board version 2; clojure derivative of version 1; position-keys are structures, and keywords where possible"
  {:pieces (s/maybe {
    {:row s/Int, :col s/Int} [{
      :color piece-schema/v2-piece-color, 
      :type piece-schema/v2-piece-type}] })} )

(defn upgrade-v1-to-v2 [v1-board]
  (let [pieces (get v1-board "pieces")] 
    {:pieces (zipmap
      (map position-schema/upgrade-v1-to-v2 (keys pieces))
      (map #(mapv piece-schema/upgrade-v1-to-v2 %) (vals pieces)))} ))

(defn revert-v2-to-v1 [v2-board]
  (let [pieces (:pieces v2-board)]
    {"pieces" (zipmap
      (map position-schema/revert-v2-to-v1 (keys pieces))
      (map #(mapv piece-schema/revert-v2-to-v1 %) (vals pieces)))} ))


