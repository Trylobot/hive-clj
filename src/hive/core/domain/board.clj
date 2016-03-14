(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about 

(def origin "origin of the board addressing system — see doc/grid.png"
  (position/create 0 0))

(def create "initialize an empty board with no pieces"
  {:pieces {}})

(defn purge-empties "remove empty piece-stacks from board"
  [pieces]
    (into {} (filter #(not (empty? (second %))) pieces)) )

(defn place-piece "place a piece on the top of a stack at position"
  [board piece position]
    (cond
      (and board piece position)
        (update-in board [:pieces position] conj piece)
      :else
        board) )

(defn remove-piece "remove the piece at the top of the stack at position" 
  [board position]
    (cond
      (and board position)
        (-> board
          (update-in [:pieces position] pop)
          (update-in [:pieces] purge-empties) )
      :else
        board) )

(defn move-piece "remove piece from board at position-0 and place it at position-1"
  [board position-0 position-1]
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

(defn count-pieces "count number of pieces on the board, with optional filtering by color and/or type"
  ([board]
    (->> board :pieces vals (map count) (reduce +)))
  ([board color-filter type-filter]
    (let [piece-predicate #(piece/like? % color-filter type-filter)
          filter-pieces #(filter piece-predicate %)]
      (->> board :pieces vals (map filter-pieces) (map count) (reduce +)) )))

(defn search-pieces "search all pieces on the board, filtering by color and/or type"
  [{pieces :pieces} color-filter type-filter]
    (->> pieces
      (map (fn [board-position]
        (let [position (first board-position)
              stack (second board-position)]
          (map-indexed (fn [index piece]
            {:position position, :layer index, :piece piece})
            stack) )))
      first
      (filter #(piece/like? (:piece %) color-filter type-filter)) ))

(defn search-top-pieces "search only the top pieces of each stack on the board, filtering by color and/or type"
  [{pieces :pieces} color-filter type-filter]
    (->> pieces
      (map (fn [board-position]
        (let [position (first board-position)
              stack (second board-position)]
          {:position position, :layer (->> stack count dec), :piece (last stack) }) ))
      (filter #(piece/like? (:piece %) color-filter type-filter)) ))

(defn lookup-occupied-positions "return all positions on the board having 1 or more piece"
  [{pieces :pieces}]
    (filter #(> (count (get pieces %)) 0) (keys pieces)) )

(defn lookup-piece-stack "return the stack of pieces at position"
  [{pieces :pieces} position]
    (get pieces position) )

(defn lookup-piece-stack-height "get the number of pieces at position"
  [board position]
    (count (lookup-piece-stack board position)) )

(defn lookup-piece "get the visible (top) piece at the stack specified by position"
  [board position]
    (last (lookup-piece-stack board position)) )

(defn lookup-piece-at-height "get the piece at position residing at height in the stack, or nil"
  [board position height]
    (let [stack-height (lookup-piece-stack-height board position)]
      (if (and (>= stack-height 0) (< stack-height height))
        (nth (lookup-piece-stack board position) height)
        nil) ))

(defn lookup-adjacent-positions "lookup neighboring positions as a directional map"
  [board position]
    (zipmap
      position/direction-vectors
      (map (fn [direction] (let [
        adjacent-position (position/translation direction)
        piece-stack (lookup-piece-stack board adjacent-position)] {
          :direction direction
          :position adjacent-position
          :contents piece-stack
          :height (count piece-stack)
        })) position/direction-vectors) ))

; keys in this lookup table are specified as follows:
;   - keys have one character for each of six directions
;   - character order corresponds to position/direction-vectors
;   - the sequence begins with 12 o'clock and proceeds clockwise
;   - the characters represent the contents of the position
;       one unit of distance away from an origin piece in the associated direction
;   - the character will be "1" if that direction is occupied
;   - the character will be "." if that direction is NOT occupied
; values in this lookup table correspond to the keys as follows:
;   - values have one character for each of six directions
;   - the character will be "1" if that direction is valid to slide into, given the occupied adjacencies
;   - the character will be "." if that direction is NOT valid to slide into, given the occupied adjacencies
;
; TODO: there must be a function that describes this data more compactly
; OBSERVATION 1: there are repeats in this data, configurations that are merely rotations of existing data
; OBSERVATION 2: this data need not be represented with strings, but could be any data structure
; OBSERVATION 3: an input could be rotated up to 5 times to compare to existing configurations for a match, and then rotated back
; OBSERVATION 4: some of the data could be generated from existing data
; OBSERVATION 5: there is an underlying rule that was used to generate this data,
;   dealing with the fact that a "gap" must be of a certain width in order to allow a slide to take place
;   perhaps the answer is to measure the gap, and compare that against the predicted width of the shape that must pass through it
;   since there are only 6 slide directions, this seems like it might be more clean than listing every possible configuration
;   plus, I'm not actually 100% certain that all of these are correct; I believe that they're probably at least 95% correct
;   but since they were generated by a human (me), and not fully tested, errors might still be present
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

(defn encode-slide-lookup-key-from-adjacencies "transform a list of adjacency descriptors into a can-slide table lookup key"
  [position-adjacencies]
    (apply str (map #(if (nil? (:contents %)) \. \1 ) position-adjacencies) ))

; position/direction-vectors
; TODO: destructure for cleanliness and further brevity; i.e., filter direction-vectors in a single step, creating no extra structures
(defn render-valid-positions-from-slide-lookup-val "transform a can-slide table lookup value into a filtered list of positions"
  [slide-lookup-val origin-position]
    (->> (map-indexed 
      (fn [idx dir] (let [is-valid (= \1 (nth slide-lookup-val idx))] 
        [dir is-valid] )) position/direction-vectors)
      (filter #(second %))
      (map #(position/translation origin-position (first %))) ))

; assumes, for the given position, that the piece being moved (from position)
;   is already "in hand" (i.e., does not appear on the board)
(defn lookup-adjacent-slide-positions "return a list of positions into which a piece at the given position could slide"
  [board position]
    (-> (lookup-adjacent-positions board position)
      vals
      encode-slide-lookup-key-from-adjacencies
      can-slide-lookup-table
      (render-valid-positions-from-slide-lookup-val position) ))

; assumes, for the given position, that the piece being moved (from position)
;   is already "in hand" (i.e., does not appear on the board)
(defn lookup-adjacent-climb-positions "return a list of positions onto which a piece at the given position could climb"
  [board position]
    (let [
      height (lookup-piece-stack-height board position)
      neighbors (lookup-adjacent-positions board position)]
      (map :position
        (filter 
          (fn [[direction neighbor]] 
            (let [slide-height (max height (:height neighbor))]
              (and ; not slide?
                (> slide-height 0) 
                (or ; no gate?
                  (<= (:height (neighbors (position/rotation direction :cw))) slide-height)
                  (<= (:height (neighbors (position/rotation direction :ccw))) slide-height)) ) ))
          neighbors)) ))

; OBSERVATION 1: "climb" implements a functional definition of the concept of
;   being "blocked" by a "gate" while trying to move from one position to another
;   where the gate only blocks if it is at the height of the origin piece or higher
; OBSERVATION 2: "slide" is a special case of "climb"
;   where stack-height of origin and destination positions must both be 0

(defn board-movement-meta "examine all occupied positions of a board, and compile simple movement meta information"
  [board]
    (let [positions (keys (:pieces board))]
      {:meta {:positions
        (zipmap positions
          (map #({
            :can-slide true
            :can-climb false
          }) positions))
      }} ))


