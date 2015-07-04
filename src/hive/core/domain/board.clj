(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about 

(def origin (position/create 0 0))

(def create {:pieces {}})

(defn place_piece [board piece position]
  (let [
    pieces (board :pieces) 
    pieces_at (pieces position)]
  {:pieces (merge pieces {position (if pieces_at
    (conj pieces_at piece)
    [piece])} )} ))

(defn remove_piece [board position]
  (let [
    pieces (board :pieces) 
    pieces_at (pieces position)]
  (if pieces_at
    (if (> (count pieces_at) 1)
      {:pieces (merge pieces {position (pop pieces_at)})}
      {:pieces (dissoc pieces position)} )
    board ) ))

;(defn move_piece [board position_0 position_1]
;  )