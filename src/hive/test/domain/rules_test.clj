(ns hive.test.domain.rules-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])
(require '[hive.core.domain.rules :as rules])

(deftest force-queen-placement?-test

  (testing "force-queen-placement?, queen bee placement not yet required (not fourth turn)"
    (is (=
      false
      (rules/force-queen-placement? 
        :white
        (-> board/create
          (board/place-piece (piece/create :white :spider) (position/create 0 0))
          (board/place-piece (piece/create :black :spider) (position/create -2 0))
          (board/place-piece (piece/create :white :beetle) (position/create -3 1))
          (board/place-piece (piece/create :black :spider) (position/create 1 1)) )
        4) )))

  (testing "force-queen-placement?, queen bee must now be placed (is fourth turn)"
    (is (=
      true
      (rules/force-queen-placement? 
        :white
        (-> board/create
          (board/place-piece (piece/create :white :spider) (position/create 0 0))
          (board/place-piece (piece/create :black :spider) (position/create -2 0))
          (board/place-piece (piece/create :white :beetle) (position/create -3 1))
          (board/place-piece (piece/create :black :spider) (position/create 1 1))
          (board/place-piece (piece/create :white :soldier-ant) (position/create 1 1))
          (board/place-piece (piece/create :black :soldier-ant) (position/create 1 1)) )
        6) )))

  (testing "force-queen-placement?, queen bee already placed"
    (is (=
      false
      (rules/force-queen-placement? 
        :white
        {:pieces {
          {:row 0, :col 0}   [{:color :white, :type :spider}]
          {:row -2, :col 0}  [{:color :black, :type :spider}]
          {:row -3, :col 1}  [{:color :white, :type :beetle}]
          {:row 1, :col 1}   [{:color :black, :type :spider}]
          {:row 1, :col -1}  [{:color :white, :type :soldier-ant}]
          {:row -3, :col -1} [{:color :black, :type :soldier-ant}]
          {:row -4, :col 0}  [{:color :white, :type :queen-bee}]
          {:row 2, :col 0}   [{:color :black, :type :queen-bee}] }}
        8) )))

)(deftest allow-queen-placement?-test 
  
  (testing "allow-queen-placement?, queen placement NOT allowed (first turn)"
    (is (=
      false
      (and (rules/allow-queen-placement? 0) (rules/allow-queen-placement? 1)) )))

  (testing "allow-queen-placement?, queen placement ALLOWED (not first turn)"
    (is (=
      true
      (and (rules/allow-queen-placement? 2) (rules/allow-queen-placement? 3)) )))

)(deftest any-movement-allowed?-test

  (testing "any-movement-allowed?, NO movement is allowed (no queen placed)"
    (is (=
      false
      (rules/any-movement-allowed?
        :white
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row -2, :col 0} [{:color :black, :type :spider}] }} ) )))

  (testing "any-movement-allowed?, movement IS allowed (queen placed)"
    (is (=
      true
      (rules/any-movement-allowed?
        :white
        {:pieces {
          {:row -4, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :queen-bee}] }} ) )))

)(deftest game-over?-test
  
  (testing "game-over?, no; zero queens placed"
    (is (= 
      {:game-over false, :is-draw false, :winner nil}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}) )))

  (testing "game-over?, no; white queen nearly surrounded (5/6 sides)"
    (is (= 
      {:game-over false, :is-draw false, :winner nil}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :spider}]
          {:row -1, :col 1} [{:color :black, :type :spider}]
          {:row 1, :col 1} [{:color :black, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}] }}) )))

  (testing "game-over?, yes; white queen completely surrounded (all sides)"
    (is (= 
      {:game-over true, :is-draw false, :winner :black}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :spider}]
          {:row -1, :col 1} [{:color :black, :type :spider}]
          {:row 1, :col 1} [{:color :black, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}]
          {:row -1, :col -1} [{:color :black, :type :spider}] }}) )))

  (testing "game-over?, not yet"
    (is (= 
      {:game-over false, :is-draw false, :winner nil}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col 1} [{:color :black, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}]
          {:row -1, :col -1} [{:color :black, :type :spider}]
          {:row -3, :col 1} [{:color :black, :type :spider}]
          {:row -2, :col 2} [{:color :black, :type :spider}]
          {:row 0, :col 2} [{:color :black, :type :spider}] }}) )))

  (testing "game-over?, game is a draw"
    (is (= 
      {:game-over true, :is-draw true, :winner nil}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col 1} [{:color :black, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}]
          {:row -1, :col -1} [{:color :black, :type :spider}]
          {:row -3, :col 1} [{:color :black, :type :spider}]
          {:row -2, :col 2} [{:color :black, :type :spider}]
          {:row 0, :col 2} [{:color :black, :type :spider}]
          {:row -1, :col 1} [{:color :black, :type :queen-bee}] }}) )))

)(deftest find-valid-placement-positions-test

  (testing "find-valid-placement-positions for white on player's second turn"
    (is (=
      #{ {:row -2, :col  0}
         {:row -1, :col  1}
         {:row -1, :col -1} }
      (rules/find-valid-placement-positions
        :white
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}
        2) )))

  (testing "find-valid-placement-positions for black on player's second turn"
    (is (=
      #{ {:row  3, :col  1}
         {:row  4, :col  0}
         {:row  3, :col -1} }
      (rules/find-valid-placement-positions
        :black
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}
        3) )))

  (testing "find-valid-placement-positions when white has nowhere to place anything (no white pieces anywhere)" 
    (is (=
      #{}
      (rules/find-valid-placement-positions
        :white
        {:pieces {
          {:row 0, :col 0} [{:color :black, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}
        3) )))
  
  (testing "find-valid-placement-positions when white has nowhere to place anything (white piece is covered by a beetle)" 
    (is (=
      #{}
      (rules/find-valid-placement-positions
        :white
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}, {:color :black, :type :beetle}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}
        3) )))

  (testing "find-valid-placement-positions when white has only a single space available" 
    (is (=
      #{ {:row -2, :col 0} }
      (rules/find-valid-placement-positions
        :white
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col 1} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}] }}
        5) )))

)
