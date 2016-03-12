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

(defn move-piece [board position-0 position-1]
  (cond
    (and board position-0 position-1)
      (let [
        {{stack position-0} :pieces} board
        piece (last stack)]
        (-> board
          (remove-piece position-0)
          (place-piece piece position-1) ))
    :else
      board) )

(defn count-pieces 
  ([board]
    (->> board :pieces vals (map count) (reduce +)))
  ([board color-filter type-filter]
    (let [piece-predicate #(piece/is? % color-filter type-filter)
          filter-pieces #(filter piece-predicate %)]
      (->> board :pieces vals (map filter-pieces) (map count) (reduce +)) )))

(defn search-pieces [board color-filter type-filter]
  (->> board
    :pieces
    (map (fn [board-position]
      (let [position (first board-position)
            stack (second board-position)]
        (map-indexed (fn [index piece]
          {:position position, :layer index, :piece piece})
          stack) )))
    first
    (filter #(piece/is? (:piece %) color-filter type-filter)) ))

(defn search-top-pieces [board color-filter type-filter]
  (->> board
    :pieces
    (map (fn [board-position]
      (let [position (first board-position)
            stack (second board-position)]
        {:position position, :layer (->> stack count dec), :piece (last stack) }) ))
    (filter #(piece/is? (:piece %) color-filter type-filter)) ))

(defn lookup-occupied-positions [board]
  (keys (:pieces board)) )

(defn lookup-piece-stack [board position]
  (position (:pieces board)) )

(defn lookup-piece-stack-height [board position]
  (count (lookup-piece-stack board position)) )

(defn lookup-piece [board position]
  (last (lookup-piece-stack board position)) )

(defn lookup-piece-at-height [board position height]
  ((lookup-piece-stack board position) height) )

(defn lookup-adjacent-positions [board position]
  (zipmap
    position/directions-enum
    (map #((let [adjacent-position (position/translation %)] {
      :direction %
      :position adjacent-position
      :contents (lookup-piece-stack board adjacent-position)
    })) position/directions-enum) ))

; keys in this lookup table are specified as follows:
;   - keys have one character for each of six directions
;   - the sequence begins with 12 o'clock and proceeds clockwise
;   - the characters represent the contents of the position
;       one unit of distance away from an origin piece in the associated direction
;   - the character will be "1" if that direction is occupied
;   - the character will be "." if that direction is NOT occupied
; values in this lookup table correspond to the keys as follows:
;   - values have one character for each of six directions
;   - the character will be "1" if that direction is valid to slide into, given the occupied adjacencies
;   - the character will be "." if that direction is NOT valid to slide into, given the occupied adjacencies
; TODO: there must be a function that describes this data more compactly
(def can-slide-lookup-table {
  "......" "......" ; island cannot move
  ".....1" "1...1." ; slide around single piece
  "....1." "...1.1" ; slide around single piece
  "....11" "1..1.." ; slide alongside pair of adjacent pieces
  "...1.." "..1.1." ; slide around single piece
  "...1.1" "1.1..." ; slide up and out of crater
  "...11." "..1..1" ; slide alongside pair of adjacent pieces
  "...111" "1.1..." ; slide up and out of crater
  "..1..." ".1.1.." ; slide around single piece
  "..1..1" "11.11." ; slide between friends
  "..1.1." ".1...1" ; slide up and out of crater
  "..1.11" "11...." ; slide out of corner
  "..11.." ".1..1." ; slide alongside pair of adjacent pieces
  "..11.1" "11...." ; slide out of corner
  "..111." ".1...1" ; slide up and out of crater
  "..1111" "11...." ; slide to escape from pit
  ".1...." "1.1..." ; slide around single piece
  ".1...1" "..1.1." ; slide up and out of crater
  ".1..1." "1.11.1" ; slide between friends
  ".1..11" "..11.." ; slide out of corner
  ".1.1.." "1...1." ; slide up and out of crater
  ".1.1.1" "......" ; nearly-surrounded piece cannot move
  ".1.11." "1....1" ; slide out of corner
  ".1.111" "......" ; nearly-surrounded piece cannot move
  ".11..." "1..1.." ; slide alongside pair of adjacent pieces
  ".11..1" "...11." ; slide out of corner
  ".11.1." "1....1" ; slide out of corner
  ".11.11" "......" ; nearly-surrounded piece cannot move
  ".111.." "1...1." ; slide up and out of crater
  ".111.1" "......" ; nearly-surrounded piece cannot move
  ".1111." "1....1" ; slide to escape from pit
  ".11111" "......" ; nearly-surrounded piece cannot move
  "1....." ".1...1" ; slide around single piece
  "1....1" ".1..1." ; slide alongside pair of adjacent pieces
  "1...1." ".1.1.." ; slide up and out of crater
  "1...11" ".1.1.." ; slide up and out of crater
  "1..1.." ".11.11" ; slide between friends
  "1..1.1" ".11..." ; slide out of corner
  "1..11." ".11..." ; slide out of corner
  "1..111" ".11..." ; slide to escape from pit
  "1.1..." "...1.1" ; slide up and out of crater
  "1.1..1" "...11." ; slide out of corner
  "1.1.1." "......" ; nearly-surrounded piece cannot move
  "1.1.11" "......" ; nearly-surrounded piece cannot move
  "1.11.." "....11" ; slide out of corner
  "1.11.1" "......" ; nearly-surrounded piece cannot move
  "1.111." "......" ; nearly-surrounded piece cannot move
  "1.1111" "......" ; nearly-surrounded piece cannot move
  "11...." "..1..1" ; slide alongside pair of adjacent pieces
  "11...1" "..1.1." ; slide up and out of crater
  "11..1." "..11.." ; slide out of corner
  "11..11" "..11.." ; slide to escape from pit
  "11.1.." "....11" ; slide out of corner
  "11.1.1" "......" ; nearly-surrounded piece cannot move
  "11.11." "......" ; nearly-surrounded piece cannot move
  "11.111" "......" ; nearly-surrounded piece cannot move
  "111..." "...1.1" ; slide up and out of crater
  "111..1" "...11." ; slide to escape from pit
  "111.1." "......" ; nearly-surrounded piece cannot move
  "111.11" "......" ; nearly-surrounded piece cannot move
  "1111.." "....11" ; slide to escape from pit
  "1111.1" "......" ; nearly-surrounded piece cannot move
  "11111." "......" ; nearly-surrounded piece cannot move
  "111111" "......" ; completely surrounded piece cannot move
})

;(defn lookup-adjacent-slide-positions [board position]
;   )



