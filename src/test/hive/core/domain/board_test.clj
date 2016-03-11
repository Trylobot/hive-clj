(ns hive.core.domain.board-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])

(deftest board-test
  
  ; place piece
  (testing "place_piece, empty board"
    (is (= 
      (board/place_piece 
        board/create 
        (piece/create :white :queen-bee) 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}} )))

  (testing "place_piece, board w/ single piece"
    (is (= 
      (board/place_piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
        {:color :black, :type :beetle} 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}} )))

  (testing "place_piece, empty board, nil piece"
    (is (=
      (board/place_piece
        board/create
        nil
        (position/create 0 0))
      board/create )))
  
  ; remove piece
  (testing "remove_piece, empty board"
    (is (= 
      (board/remove_piece
        board/create
        (position/create 0 0))
      board/create )))

  (testing "remove_piece, board w/ single piece"
    (is (= 
      (board/remove_piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}} 
        (position/create 0 0))
      board/create )))
  
  (testing "remove_piece, board w/ stack of two pieces"
    (is (= 
      (board/remove_piece 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}} 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}} )))
  
  ; move piece
  (testing "move_piece, empty board"
    (is (=
      (board/move_piece
        board/create
        (position/create 0 0)
        (position/create -2 0))
      board/create )))

  (testing "move_piece, board with one piece"
    (is (=
      (board/move_piece
        {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}}
        {:row 0, :col 0}
        {:row -2, :col 0})
      {:pieces {{:row -2, :col 0} [{:color :white, :type :queen-bee}]}} )))

  ; count pieces
  (testing "count_pieces, empty board"
    (is (=
      (board/count_pieces board/create)
      0) ))

  (testing "count_pieces, board with one piece"
    (is (=
      (board/count_pieces {:pieces {{:row 0, :col 0} [{:color :white, :type :queen-bee}]}})
      1) ))

  (testing "count_pieces, board with three pieces"
    (is (=
      (board/count_pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }})
      3) ))

  (testing "count_pieces, filtered by color, board with three pieces"
    (is (=
      (board/count_pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }}
        :white nil)
      2) ))

  (testing "count_pieces, filtered by type, board with three pieces"
    (is (=
      (board/count_pieces {:pieces { 
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row -2, :col 0} [{:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :soldier-ant}] }}
        nil :soldier-ant)
      1) ))

  (testing "search_pieces, unfiltered, board with two pieces"
    (is (=
      (board/search_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil nil)
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}}
        {:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}}) )))

  (testing "search_pieces, filtered by color, board with two pieces"
    (is (=
      (board/search_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        :white nil)
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}}) )))

  (testing "search_pieces, filtered by type, board with two pieces"
    (is (=
      (board/search_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil :beetle)
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}}) )))

  (testing "search_top_pieces, unfiltered, board with two pieces"
    (is (=
      (board/search_top_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil nil)
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}}) )))

  (testing "search_top_pieces, filtered by color, board with two pieces"
    (is (=
      (board/search_top_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        :white nil)
      '() )))

  (testing "search_top_pieces, filtered by type, board with two pieces"
    (is (=
      (board/search_top_pieces {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]}}
        nil :beetle)
      '({:position {:row 0, :col 0}, :layer 1, :piece {:color :black, :type :beetle}}) )))

  (testing "search_top_pieces, board with three stacks"
    (is (=
      (board/search_top_pieces {:pieces {
        {:row 0,  :col 0} [{:color :white, :type :queen-bee}]
        {:row 2,  :col 0} [{:color :black, :type :soldier-ant}, {:color :white, :type :beetle}]
        {:row -2, :col 0} [{:color :white, :type :grasshopper}, {:color :black, :type :beetle}] }}
        nil nil)
      '({:position {:row 0, :col 0}, :layer 0, :piece {:color :white, :type :queen-bee}}
        {:position {:row 2, :col 0}, :layer 1, :piece {:color :white, :type :beetle}}
        {:position {:row -2 :col 0}, :layer 1, :piece {:color :black, :type :beetle}}) )))
)
