(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about 

(def origin (position/create 0 0))

(def create {:pieces {}})

(defn- purge-empties [pieces]
  (into {} (filter #(not (empty? (second %))) pieces)) )

(defn place_piece [board piece position]
  (update-in board [:pieces position] conj piece) )

(defn remove_piece [board position]
  (-> board
    (update-in [:pieces position] pop)
    (update-in [:pieces] purge-empties) ))

;(defn move_piece [board position_0 position_1]
;  )