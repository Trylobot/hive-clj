(ns hive.core.domain.rules)
(require '[clojure.set :as set])
(use 'hive.core.util)
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.range :as range])
(require '[hive.core.domain.board :as board])

; rules
;
; this module is used to represent the rules of hive.
;   it can provide a list of valid end positions for a piece, 
;   given a board state (for a placement check)
;   or a board state and a current position (for a movement check)
; 
; HIVE - by John Yianni
;   "A Game Buzzing With Possibilities"
;   http://gen42.com
; 
; Playing the Game
;   Play begins with one player placing a piece from their hand to the centre of the table
;   and the next player joining one of their own pieces to it edge to edge. Players then
;   take turns to either place or move any one of their pieces.
; 
; The Hive
;   The pieces in play define the playing surface, known as the Hive.

(defn check-force-queen-placement ""
  [color board turn-number]
     )

(defn check-allow-queen-placement ""
  [turn-number]
     )

(defn check-any-movement-allowed ""
  [color board]
     )

(defn check-if-game-over ""
  [board]
     )

(defn find-valid-placement-positions ""
  [color board turn-number]
     )

(defn find-valid-movement ""
  [board position]
     )

(defn find-valid-special-abilities ""
  [board position turn-history]
     )


; Queen Bee
;   The Queen Bee can move only one space per turn.

(defn find-valid-movement-queen-bee "get movement for position by the rules of the queen bee"
  [board position]
    (board/lookup-adjacent-slide-positions board position) )


; Beetle
;   The Beetle, like the Queen Bee, moves only space per turn around the Hive, but
;   can also move on top of the Hive. A piece with a beetle on top of it is unable
;   to move and for the purposes of the placing rules, the stack takes on the
;   colour of the Beetle.
; 
;   From its position on top of the Hive, the Beetle can move from piece to piece
;   across the top of the Hive. It can also drop into spaces that are surrounded
;   and therefore not accessible to most other creatures.
; 
;   The only way to block a Beetle that is on top of the Hive is to move another
;   Beetle on top of it. All Beetles and Mosquitoes can be stacked on top of each
;   other.
; 
;   When it is first placed, the Beetle is placed in the same way as all the other
;   pieces. It cannot be placed directly on top of the Hive, even though it can
;   be moved there later.
; 
;   http://www.boardgamegeek.com/wiki/page/Hive_FAQ#toc8
;     Q: Are beetles affected by the Freedom To Move rule?
;     A: Yes. (albeit in a different way): Beetles cannot slide through "gates"

(defn find-valid-movement-beetle "get movement for position by the rules of the beetle"
  [board position]
    (set/union
      (board/lookup-adjacent-slide-positions board position)
      (board/lookup-adjacent-climb-positions board position) ) )

(defn find-valid-movement-grasshopper "get movement for position by the rules of the grasshopper"
  [board position]
     )

(defn find-valid-movement-spider "get movement for position by the rules of the spider"
  [board position]
     )

(defn find-valid-movement-soldier-ant "get movement for position by the rules of the soldier-ant"
  [board position]
     )

(defn find-valid-movement-mosquito "get movement for position by the rules of the mosquito"
  [board position]
     )

(defn find-valid-special-abilities-mosquito "get special abilities for position by the rules of the mosquito"
  [board position turn-history]
     )

(defn find-valid-movement-ladybug "get movement for position by the rules of the ladybug"
  [board position]
     )

(defn find-valid-movement-pillbug "get movement for position by the rules of the pillbug"
  [board position]
     )

(defn find-valid-special-abilities-pillbug "get special abilities for position by the rules of the pillbug"
  [board position turn-history]
     )

; TODO: define a game-state as a schema
(defn lookup-possible-turns "given a full game state, return all possible next turns"
  [color board hand turn-number turn-history]
     )

