(ns hive.core.domain.board)
(require '[clojure.set :as set])
(use 'hive.core.util)
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.range :as range])

; board
;   for dealing with hive board states
;   querying the board and moving pieces about

(def origin "origin of the board addressing system — see doc/grid.png"
  position/origin)

(defn place-piece "place a piece on the top of a stack at position"
  [board piece position]
    (cond
      (and board piece position)
        (update-in 
          board [:pieces position]
          (fn [stack piece]
            (if stack 
              (conj stack piece) 
              [piece] ))
          piece)
      :else
        board) )

(defn create "initialize a board (optionally, with some pieces)"
  ([] 
    {:pieces {}})
  ([piece-data]
    (reduce 
      (fn [board [row col color type]]
        (place-piece
          board
          (piece/create color type)
          (position/create row col) ))
      {:pieces {}}
      piece-data)))

(defn purge-empties "remove empty piece-stacks from board"
  [pieces]
    (into {} (filter #(not (empty? (second %))) pieces)) )

(defn remove-piece "remove the piece at the top of the stack at position"
  [board position]
    (cond
      (and board position)
        (-> board
          (update-in [:pieces position] pop)
          (update-in [:pieces] purge-empties) ) ; this is an inefficency
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
  [board color-filter type-filter]
    (let [pieces (:pieces board)]
      (->> pieces
        (mapcat (fn [board-position]
          (let [position (first board-position)
                stack (second board-position)]
            (map-indexed 
              (fn [index piece]
                {:position position, :layer index, :piece piece })
              stack)) ))
        (filter #(piece/like? (:piece %) color-filter type-filter)) )))

(defn search-top-pieces "search only the top pieces of each stack on the board, filtering by color and/or type"
  [board color-filter type-filter]
    (->> (:pieces board)
      (map (fn [board-position]
        (let [position (first board-position)
              stack (second board-position)]
          {:position position, :layer (->> stack count dec), :piece (last stack) }) ))
      (filter #(piece/like? (:piece %) color-filter type-filter)) ))

(defn lookup-occupied-positions "returns the occupied positions of board as a set"
  [board]
    (let [{pieces :pieces} board]
      (set (filter #(> (count (get pieces %)) 0) (keys pieces)) )))

(defn lookup-piece-stack "return the stack of pieces at position"
  [board position]
    (get (:pieces board) position) )

(defn lookup-piece-stack-height "get the number of pieces at position"
  [board position]
    (count (lookup-piece-stack board position)) )

(defn lookup-piece "get the visible (top) piece at the stack specified by position"
  [board position]
    (last (lookup-piece-stack board position)) )

(defn lookup-piece-at-height "get the piece at position residing at height in the stack, or nil"
  [board position idx]
    (let [
      stack (lookup-piece-stack board position)
      height (count stack)]
      (if (and (>= idx 0) (< idx height))
        (nth stack idx)
        nil) ))

(defn lookup-adjacent-positions "lookup neighboring positions. returns a map like  {direction: lookup-result, ...}"
  [board position]
    (zipmap
      position/direction-vectors
      (map (fn [direction] 
        (let [adjacent-position (position/translation position direction)
              piece-stack (lookup-piece-stack board adjacent-position)] 
          { :direction direction
            :position adjacent-position
            :height (count piece-stack)
            :contents piece-stack
          })) 
        position/direction-vectors) ))

(defn lookup-occupied-adjacencies "return set of occupied adjacencies"
  [board position]
    (set (map :position
      (filter #(:contents %)
        (vals (lookup-adjacent-positions board position)))) ))

(defn lookup-adjacent-piece-types "lookup neighboring piece types (tops of stacks), as a set"
  [board position]
    (set (map 
      (fn [result] (:type (:last (:contents result)))) 
      (vals (lookup-adjacent-positions board position)))) )

; keys represent six spaces adjacent to a theoretical context piece, in the same order as position/direction-vectors
;   "1" --> occupied, "." --> not occupied
; values represent possible slide movements of the context piece given the configuration specified by its key
;   "1" --> can move, "." --> cannot move
; OBSERVATION 2: this data need not be represented with strings, but could be any data structure
; OBSERVATION 5: the underlying logic of this data is identical to can-climb, but with height clamped to 0
(def can-slide-lookup-table-seed {
  "......" "......" ; island cannot move
  ".....1" "1...1." ; slide around single piece
  "....11" "1..1.." ; slide alongside pair of adjacent pieces
  "...1.1" "1.1..." ; slide up and out of crater
  "...111" "1.1..." ; slide up and out of crater
  "..1..1" "11.11." ; slide between friends
  "..1.11" "11...." ; slide out of corner
  "..11.1" "11...." ; slide out of corner
  "..1111" "11...." ; slide to escape from pit
  ".1.1.1" "......" ; nearly-surrounded piece cannot move
  ".1.111" "......" ; nearly-surrounded piece cannot move
  ".11.11" "......" ; nearly-surrounded piece cannot move
  ".11111" "......" ; nearly-surrounded piece cannot move
  "111111" "......" ; completely surrounded piece cannot move
})

(defn generate-can-slide-lookup-table [seed] "generate can-slide-lookup-table from only unique configurations, by rotation"
  (let [
    rotate-seed-pair-left (fn [[seed-key seed-val]] [(rotate-string-left seed-key) (rotate-string-left seed-val)])
    all-rotations-of-pair (fn [seed-pair]
      (rest (reduce
        (fn [result i]
          (conj result (rotate-seed-pair-left (last result))))
        [seed-pair]
        (range 5))))]
    (reduce
      (fn [result seed-pair]
        (apply conj result (all-rotations-of-pair seed-pair)))
      can-slide-lookup-table-seed ; both the initial value to expand, and the destination for the result
      can-slide-lookup-table-seed) ))

(def can-slide-lookup-table 
  (generate-can-slide-lookup-table can-slide-lookup-table-seed))

(defn encode-slide-lookup-key-from-adjacencies "transform a list of adjacency descriptors into a can-slide table lookup key"
  [position-adjacencies]
    (apply str (map (fn [adjacency]
      (if (or (zero? (:height adjacency)) (nil? (:contents adjacency)))
        \. \1))
      (vals position-adjacencies)) ))

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
      encode-slide-lookup-key-from-adjacencies
      can-slide-lookup-table
      (render-valid-positions-from-slide-lookup-val position)
      set ))

; assumes, for the given position, that the piece being moved (from position)
;   is already "in hand" (i.e., does not appear on the board)
(defn lookup-adjacent-climb-positions "return a list of positions onto which a piece at the given position could climb"
  [board position]
    (let [
      height (lookup-piece-stack-height board position)
      neighbors (lookup-adjacent-positions board position)
      can-climb-predicate (fn [neighbor]
        (let [slide-height (max height (:height neighbor))]
          (and
            (> slide-height 0) ; not a table-slide?
            (or ; not blocked by a gate?
              (<= (:height (neighbors (position/rotation (:direction neighbor) :cw))) slide-height)
              (<= (:height (neighbors (position/rotation (:direction neighbor) :ccw))) slide-height)) ) ))]
      (set (map :position
        (filter can-climb-predicate (vals neighbors)))) ))

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
         (map (fn [position] {
           :can-slide (lookup-adjacent-slide-positions board position)
           :can-climb (lookup-adjacent-climb-positions board position)
         }) positions))
     }} ))

(defn search-free-spaces "return set of open spaces with adjacencies of only the specified color"
  [board color-filter]
    (let [pieces (:pieces board)
          potentials (apply set/union (map position/adjacencies (keys pieces)))]
      (set (filter (fn [potential]
        (let [is-empty (nil? (get pieces potential))
              adjacencies (lookup-adjacent-positions board potential)
              colors (map #(:color (last (:contents %))) (vals adjacencies))
              passes-filter (map #(or (nil? %) (= color-filter %)) colors)
              all-pass (reduce #(and %1 %2) passes-filter)]
          (and is-empty all-pass)))
        potentials)) ))

(defn lookup-slide-destinations "return set of possible destinations that can be reached from a given starting position by only sliding"
  ([board start-position]
    (lookup-slide-destinations board start-position (list start-position) #{} #{}) )
  ([board start-position to-visit visited destinations]
    (if (empty? to-visit)
      destinations
      (let [cursor (first to-visit)
            slide-destinations (lookup-adjacent-slide-positions board cursor)
            new-to-visit (filter 
              #(not (contains? visited %)) 
              (apply conj (rest to-visit) slide-destinations))
            new-visited (conj visited cursor)
            new-destinations (apply conj destinations 
              (filter #(not (= start-position %)) slide-destinations))]
        (recur board start-position new-to-visit new-visited new-destinations))) ))

(defn find-free-space-in-direction "find the first non-occupied space in the given direction from position"
  [board position direction]
    (let [pieces (:pieces board)
          position (position/translation position direction)]
      (if (nil? (get pieces position))
        position
        (recur board position direction)) ))

; refer to https://en.wikipedia.org/wiki/Disjoint-set_data_structure
(defn contiguous? "return whether the board is one contiguous group"
  ([board]
    (let [occupied (set (keys (:pieces board)))
          frontier (conj #{} (first occupied))
          unexplored (set (rest occupied))]
      (if (empty? occupied)
        true
        (contiguous? occupied frontier unexplored)) ))
  ([occupied frontier unexplored]
    (if (= frontier occupied)
      true
      (let [expansion (set/difference (reduce set/union #{} (map position/adjacencies frontier)) frontier)
            frontier-additions (set/intersection expansion unexplored)
            new-frontier (set/union frontier frontier-additions)
            new-unexplored (set/difference unexplored frontier-additions)]
        (if (= unexplored new-unexplored)
          false
          (recur occupied new-frontier new-unexplored)) ))) )

(defn create-path-node "intermediate structure for caching information about path steps"
  [position parent-node] 
    (let [parent-length (:path-length parent-node)]
      { :position position
        :parent parent-node
        :path-length (if parent-length (inc parent-length) 0) } ))

(defn path-contains-position? "intermediate function to check if a position is already contained in a path"
  [path-node position]
    (if (= position (:position path-node))
      true
      (if (not (:parent path-node))
        false
        (recur (:parent path-node) position)) ) )

(defn find-adjacent-path-nodes "find all adjacent moves from position matching height limits"
  [board path-node height-range]
    (let [height-range (range/is-range? height-range)
          node-height (lookup-piece-stack-height board (:position path-node))
          slide-positions (if (and (zero? node-height) (= (:min height-range) 0)) 
            (lookup-adjacent-slide-positions board (:position path-node)) nil)
          climb-positions (if (or (> node-height 0) (> (:max height-range) 0)) 
            (lookup-adjacent-climb-positions board (:position path-node)) nil)
          adjacencies (reduce set/union slide-positions climb-positions)
          is-valid-adjacency? (fn [adjacent-position] 
              (let [adjacency-height (lookup-piece-stack-height board adjacent-position)]
                (and (range/r-contains? height-range adjacency-height)
                     (not (path-contains-position? path-node adjacent-position)) )))
          valid-adjacencies (filter is-valid-adjacency? adjacencies)
          new-path-nodes (set (map #(create-path-node % path-node) valid-adjacencies))]
      new-path-nodes ))

(defn trace-path "create a list of positions from root [inclusive] to given path node [inclusive] by traversing a path graph from given leaf to its root"
  ([path-node]
    (trace-path path-node (list (:position path-node))))
  ([path-node position-list]
    (if (not (:parent path-node))
      position-list
      (recur (:parent path-node) (conj position-list (:position (:parent path-node)))) )) )

(defn find-unique-paths-matching-conditions "find all unique paths from start-position matching required length and within height limits; returns a map of position -> path-node"
  ([board start-position distance-range height-range-seq]
    (let [distance-range (range/is-range? distance-range)
          height-range-seq (range/is-range-seq? height-range-seq)
          root-node (create-path-node start-position nil)]
      (find-unique-paths-matching-conditions
        board start-position distance-range height-range-seq #{root-node} #{} 0) ))
  ([board start-position distance-range height-range-seq branch-nodes leaf-nodes distance]
    ; (prn (map :position branch-nodes) (map :position leaf-nodes) distance)
    (if (or (>= distance (:max distance-range)) (empty? branch-nodes))
      (let [potential-paths-terminal-nodes (filter
              (fn [path-node] 
                (range/r-contains? distance-range (:path-length path-node)) )
              (set/union branch-nodes leaf-nodes) )
            unique-paths (zipmap
              (map :position potential-paths-terminal-nodes)
              (map trace-path potential-paths-terminal-nodes))]
            unique-paths)
      (let [height-range (get height-range-seq distance)
            new-path-nodes (zipmap branch-nodes 
              (map #(find-adjacent-path-nodes board % height-range) branch-nodes))
            new-leaf-nodes (reduce 
              (fn [result [path-node adjacent-nodes]]
                (if (empty? adjacent-nodes)
                  (conj result path-node)
                  result) ) 
              #{} new-path-nodes)
            new-branch-nodes (reduce 
              (fn [result [path-node adjacent-nodes]] 
                (if (not (empty? adjacent-nodes))
                  (set/union result adjacent-nodes)
                  result) )
              #{} new-path-nodes)]
        (recur board start-position distance-range height-range-seq new-branch-nodes new-leaf-nodes (inc distance)) ))) )








