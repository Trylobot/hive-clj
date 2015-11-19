(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about 

(def origin (position/create 0 0))

(def create {:pieces {}})

(defn- purge_empties [pieces]
  (into {} (filter #(not (empty? (second %))) pieces)) )

(defn place_piece [board piece position]
  (cond
    (and board piece position)
      (update-in board [:pieces position] conj piece)
    :else
      board) )

(defn remove_piece [board position]
  (cond
    (and board position)
      (-> board
        (update-in [:pieces position] pop)
        (update-in [:pieces] purge_empties) )
    :else
      board) )

(defn move_piece [board position_0 position_1]
  (cond
    (and board position_0 position_1)
      (let [{{piece position_0} :pieces} board]
        (-> board
          (remove_piece position_0)
          (place_piece piece position_1) ))
    :else
      board) )

(defn count_pieces [board piece_color piece_type]
  (reduce 
    (fn [sum [position stack]] (+ sum (count stack))) ; todo: filtering
    (:pieces board) ))



