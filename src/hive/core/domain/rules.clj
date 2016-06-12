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


; Placing
;   A new piece can be introduced into the game at any time. However, with the exception
;   of the first piece placed by each player, pieces may not be placed next to a piece of
;   the opponent's colour. It is possible to win the game without placing all your pieces,
;   but once a piece has been placed, it cannot be removed.
; 
; Placing your Queen Bee
;   Your Queen Bee can be placed at any time from your first to your fourth turn. You
;   must place your Queen Bee on your fourth turn if you have not placed it before.

(defn check-force-queen-placement ""
  [color board turn-number]
     )

; http://boardgamegeek.com/wiki/page/Hive_FAQ
;   You cannot place your queen as your first move.

(defn check-allow-queen-placement ""
  [turn-number]
     )

; Moving
;   Once your Queen Bee has been placed (but not before), you can decide whether to use
;   each turn after that to place another piece or to move one of the pieces that have
;   already been placed. Each creature has its own way of moving. When moving, it is
;   possible to move pieces to a position where they touch one or more of your opponent's
;   pieces. 

;   All pieces must always touch at least one other piece. If a piece is the only
;   connection between two parts of the Hive, it may not be moved. (See 'One Hive rule')

(defn check-any-movement-allowed ""
  [color board]
     )

; The End of the Game
;   The game ends as soon as one Queen Bee is completely surrounded by pieces of any colour.
;   The person whose Queen Bee is surrounded loses the game, unless the last piece to
;   surround their Queen Bee also completes the surrounding of the other Queen Bee. In that
;   case the game is drawn. A draw may also be agreed if both players are in a position where
;   they are forced to move the same two pieces over and over again, without any possibility
;   of the stalemate being resolved.

(defn check-if-game-over ""
  [board]
     )

; Unable to Move or Place
;   If a player can not place a new piece or move an existing piece, the turn passes
;   to their opponent who then takes their turn again. The game continues in this way
;   until the player is able to move or place one of their pieces, or until their
;   Queen Bee is surrounded.

;   http://boardspace.net/english/about_hive.html
;     Rules Change: at boardspace the "Queen" opening has been forbidden for both black and white.
;     John Yianni supports this change, which is intended to eliminate the problem of excess draws in "queen opening" games.

(defn find-valid-placement-positions ""
  [color board turn-number]
     )

; One Hive rule
;   The pieces in play must be linked at all times. At no time can you leave a piece
;   stranded (not joined to the Hive) or separate the Hive in two.

; Freedom to Move
;   The creatures can only move in a sliding movement. If a piece is surrounded to
;   the point that it can no longer physically slide out of its position, it may
;   not be moved. The only exceptions are the Grasshopper (which jumps into or out
;   of a space), the Beetle and Ladybug (which climb up and down) and the Mosquito
;   (which can mimic one of the three). Similarly, no piece may move into a space
;   that it cannot physically slide into.

;   When first introduced to the game, a piece may be placed into a space that is
;   surrounded as long as it does not violate any of the placing rules, in particular
;   the rule about pieces not being allowed to touch pieces of the other colour when
;   they are first placed.

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


; Grasshopper
;   The Grasshopper does not move around the outside of the Hive like the other
;   creatures. Instead it jumps from its space over any number of pieces (but
;   at least one) to the next unoccupied space along a straight row of joined
;   pieces.
; 
;   This gives it the advantage of being able to fill in a space which is
;   surrounded by other pieces.

(defn find-valid-movement-grasshopper "get movement for position by the rules of the grasshopper"
  [board position]
    (let [adjacent-positions (board/lookup-adjacent-positions board position)
          free-spaces (map (fn [direction]
            (board/lookup-free-space-in-direction board position direction))
            (keys adjacent-positions))]
      free-spaces ))

; Spider
;   The Spider moves three spaces per turn - no more, no less. It must move in a
;   direct path and cannot backtrack on itself. It may only move around pieces
;   that it is in direct contact with on each step of its move. It may not move
;   across to a piece that it is not in direct contact with.

(defn find-valid-movement-spider "get movement for position by the rules of the spider"
  [board position]
     )

; Soldier Ant
;   The Soldier Ant can move from its position to any other position around the
;   Hive provided the restrictions are adhered to.

(defn find-valid-movement-soldier-ant "get movement for position by the rules of the soldier-ant"
  [board position]
     )

; Mosquito
;   The Mosquito is placed in the same way as the other pieces. Once in play, the
;   Mosquito takes on the movement characteristics of any creature it touches at
;   the time, including your opponents', thus changing its characteristics
;   throughout the game.

;   Exception: if moved as a Beetle on top of the Hive, it continues to move as
;   a Beetle until it climbs down from the Hive. If when on the ground level it
;   is next to a stacked Beetle, it may move as a Beetle and not the piece below
;   the Beetle. If touching another Mosquito only (including a stacked Mosquito)
;   and no other piece, it may not move.

(defn find-valid-movement-mosquito "get movement for position by the rules of the mosquito"
  [board position]
     )

(defn find-valid-special-abilities-mosquito "get special abilities for position by the rules of the mosquito"
  [board position turn-history]
     )

; Ladybug
;   The Ladybug moves three spaces; two on top of the Hive, and then one down.
;   It must move exactly two on top of the Hive and then move one down on its
;   last move. It may not move around the outside of the Hive and may not end
;   its movement on top of the Hive. Even though it cannot block by landing
;   on top of other pieces like the Beetle, it can move into or out of surrounded
;   spaces. It also has the advantage of being much faster.

(defn find-valid-movement-ladybug "get movement for position by the rules of the ladybug"
  [board position]
     )

; Pillbug
;   The Pillbug moves like the Queen Bee - one space at a time - but it has a
;   special ability that it may use instead of moving. This ability allows the
;   Pillbug to move an adjacent unstacked piece (whether friend or enemy) two
;   spaces: up onto the pillbug itself, then down into an empty space adjacent
;   to itself. Some exceptions for this ability: 
;   - The Pillbug may not move the piece most recently moved by the opponent.
;   - The Pillbug may not move any piece in a stack of pieces.
;   - The Pillbug may not move a piece if it splits the hive (One Hive rule)
;   - The Pillbug may not move a piece through a too-narrow gap of stacked
;     pieces (Freedom to Move rule)
  
;   Any piece which physically moved (directly or by the Pillbug) is rendered
;   immobile on the next player's turn; it cannot move or be moved, nor use its
;   special ability. The Mosquito can mimic either the movement or special
;   ability of the Pillbug, even when the Pillbug it is touching has been rendered
;   immobile by another Pillbug.

;   Clarification from John Yianni: The Pillbug using its special ability does not
;   count as "movement" from the perspective of an opposing player's Pillbug, and
;   thus does not grant it immunity from being moved by the opposing Pillbug. Only
;   physically moved pieces have such protection.

;   Further Clarification: A stunned Pillbug (one that was just moved by a Pillbug)
;   cannot use its special ability.

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

