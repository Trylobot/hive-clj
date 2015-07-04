(ns hive.core.domain.board-test
  (:require [clojure.test :refer :all]))
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.board :as board])

(deftest board-test
  
  ; place piece
  (testing "place_piece, empty board"
    (is (= 
      (board/place_piece 
        board/create 
        "piece" 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} ["piece"]}} )))

  (testing "place_piece, board w/ single piece"
    (is (= 
      (board/place_piece 
        {:pieces {{:row 0, :col 0} ["piece"]}}
        "piece" 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} ["piece", "piece"]}} )))
  
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
        {:pieces {{:row 0, :col 0} ["piece"]}} 
        (position/create 0 0))
      board/create )))
  
  (testing "remove_piece, board w/ stack of two pieces"
    (is (= 
      (board/remove_piece 
        {:pieces {{:row 0, :col 0} ["piece", "piece"]}} 
        (position/create 0 0))
      {:pieces {{:row 0, :col 0} ["piece"]}} )))
  
  

)
