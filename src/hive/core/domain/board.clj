(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about 

(def origin (position/create 0 0))

(def create {:pieces {}})

(defn- purge-empties [pieces]
  (into {} (filter #(not (empty? (second %))) pieces)) )

(defn place-piece [board piece position]
  (cond
    (and board piece position)
      (update-in board [:pieces position] conj piece)
    :else
      board) )

(defn remove-piece [board position]
  (cond
    (and board position)
      (-> board
        (update-in [:pieces position] pop)
        (update-in [:pieces] purge-empties) )
    :else
      board) )

(defn move-piece [board position_0 position_1]
  (cond
    (and board position_0 position_1)
      (let [
        {{stack position_0} :pieces} board
        piece (last stack)]
        (-> board
          (remove-piece position_0)
          (place-piece piece position_1) ))
    :else
      board) )

(defn count-pieces 
  ([board]
    (->> board
      :pieces
      vals
      (map count)
      (reduce +)))
  ([board color_filter type_filter]
    (let [piece_predicate #(piece/is? % color_filter type_filter)
          filter_pieces #(filter piece_predicate %)]
      (->> board
        :pieces
        vals
        (map filter_pieces)
        (map count)
        (reduce +)) )))

(defn search-pieces [board color_filter type_filter]
  (->> board
    :pieces
    (map (fn [board_position]
      (let [position (first board_position)
            stack (second board_position)]
        (map-indexed (fn [index piece]
          {:position position, :layer index, :piece piece})
          stack) )))
    first ; <-- TODO: remove extra sequence wrapping data here
    (filter #(piece/is? (:piece %) color_filter type_filter)) ))

(defn search-top-pieces [board color_filter type_filter]
  (->> board
    :pieces
    (map (fn [board_position]
      (let [position (first board_position)
            stack (second board_position)]
        {:position position, :layer (->> stack count dec), :piece (last stack) }) ))
    (filter #(piece/is? (:piece %) color_filter type_filter)) ))

(defn lookup-occupied-positions [board]
  nil )



