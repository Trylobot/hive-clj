(ns hive.core.domain.board-test
  (:require [clojure.test :refer :all]))
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])

(deftest board-test
  
  ; place piece
  (testing "place_piece, empty board"
    (is (= 
      (board/place_piece 
        board/create 
        (piece/create "White" "Queen Bee") 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}} )))

  (testing "place_piece, board w/ single piece"
    (is (= 
      (board/place_piece 
        {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}}
        {:color "Black", :type "Beetle"} 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}, {:color "Black", :type "Beetle"}]}} )))

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
        {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}} 
        (position/create 0 0))
      board/create )))
  
  (testing "remove_piece, board w/ stack of two pieces"
    (is (= 
      (board/remove_piece 
        {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}, {:color "Black", :type "Beetle"}]}} 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}} )))
  
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
        {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}}
        {:row 0, :col 0}
        {:row -2, :col 0})
      {:pieces {{:row -2, :col 0} [{:color "White", :type "Queen Bee"}]}} )))

  ; count pieces
  (testing "count_pieces, empty board"
    (is (=
      (board/count_pieces board/create)
      0) ))

  (testing "count_pieces, board with one piece"
    (is (=
      (board/count_pieces {:pieces {{:row 0, :col 0} [{:color "White", :type "Queen Bee"}]}})
      1) ))

)
