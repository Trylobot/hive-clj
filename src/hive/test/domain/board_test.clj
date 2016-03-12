(ns hive.test.domain.board-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])

(deftest board-test
  
  ; place piece
  (testing "place-piece, empty board"
    (is (= 
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
      (board/place-piece 
        board/create 
        (piece/create :white :queen-bee) 
        (position/create 0 0)) )))

  (testing "place-piece, board w/ single piece"
    (is (= 
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
      (board/place-piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
        {:color :black, :type :beetle} 
        (position/create 0 0)) )))

  (testing "place-piece, empty board, nil piece"
    (is (=
      board/create
      (board/place-piece
        board/create
        nil
        (position/create 0 0)) )))
  
  ; remove piece
  (testing "remove-piece, empty board"
    (is (= 
      board/create
      (board/remove-piece
        board/create
        (position/create 0 0)) )))

  (testing "remove-piece, board w/ single piece"
    (is (= 
      board/create
      (board/remove-piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}} 
        (position/create 0 0)) )))
  
  (testing "remove-piece, board w/ stack of two pieces"
    (is (= 
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
      (board/remove-piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}} 
        (position/create 0 0)) )))
  
  ; move piece
  (testing "move-piece, empty board"
    (is (=
      board/create
      (board/move-piece
        board/create
        (position/create 0 0)
        (position/create -2 0)) )))

  (testing "move-piece, board with one piece"
    (is (=
      {:pieces {{:row -2, :col 0} [{:color :white, :type :queen-bee}]}}
      (board/move-piece
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
        {:row 0, :col 0}
        {:row -2, :col 0}) )))

  ; count pieces
  (testing "count-pieces, empty board"
    (is (=
      0
      (board/count-pieces board/create) )) )

  (testing "count-pieces, board with one piece"
    (is (=
      1
      (board/count-pieces {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}) )) )

  (testing "count-pieces, board with three pieces"
    (is (=
      3
      (board/count-pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }}) )))

  (testing "count-pieces, filtered by color, board with three pieces"
    (is (=
      2
      (board/count-pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }}
        :white nil) ) ))

  (testing "count-pieces, filtered by type, board with three pieces"
    (is (=
      1
      (board/count-pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }}
        nil :soldier-ant) ) ))

  (testing "search-pieces, unfiltered, board with two pieces"
    (is (=
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}}
        {:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}})
      (board/search-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil nil) )))

  (testing "search-pieces, filtered by color, board with two pieces"
    (is (=
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}})
      (board/search-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        :white nil) )))

  (testing "search-pieces, filtered by type, board with two pieces"
    (is (=
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}})
      (board/search-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil :beetle) )))

  (testing "search-top-pieces, unfiltered, board with two pieces"
    (is (=
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}})
      (board/search-top-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil nil) )))

  (testing "search-top-pieces, filtered by color, board with two pieces"
    (is (=
      '()
      (board/search-top-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        :white nil) )))

  (testing "search-top-pieces, filtered by type, board with two pieces"
    (is (=
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}})
      (board/search-top-pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil :beetle) )))

  (testing "search-top-pieces, board with three stacks"
    (is (=
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}}
        {:position {:row 2, :col 0}, :layer 1, :piece {:color :white, :type :beetle}}
        {:position {:row -2 :col 0}, :layer 1, :piece {:color :black, :type :beetle}})
      (board/search-top-pieces {:pieces {
        {:row 0,  :col 0} [{:color :white, :type :queen-bee}]
        {:row 2,  :col 0} [{:color :black, :type :soldier-ant}, {:color :white, :type :beetle}]
        {:row -2, :col 0} [{:color :white, :type :grasshopper}, {:color :black, :type :beetle}] }}
        nil nil) )))
)
