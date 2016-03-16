(ns hive.test.domain.board-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])

(deftest place-piece-test
  
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

)(deftest remove-piece-test

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

)(deftest move-piece-test

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

)(deftest count-pieces-test

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

)(deftest search-pieces-test

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

)(deftest search-top-pieces-test

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

)(deftest lookup-occupied-positions-test
  
  (testing "lookup-occupied-positions, empty board"
    (is (=
      '()
      (board/lookup-occupied-positions {:pieces {}}) )))
  
  (testing "lookup-occupied-positions, small board"
    (is (=
      '({:row 0, :col 0})
      (board/lookup-occupied-positions
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }}) )))

)(deftest lookup-piece-stack-test
  
  (testing "lookup-piece-stack, empty board"
    (is (=
      nil
      (board/lookup-piece-stack {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-piece-stack, small board"
    (is (=
      [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
      (board/lookup-piece-stack 
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }} {:row 0, :col 0}) )))

)(deftest lookup-piece-stack-height-test
  
  (testing "lookup-piece-stack-height, empty board"
    (is (=
      0
      (board/lookup-piece-stack-height {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-piece-stack-height, small board"
    (is (=
      2
      (board/lookup-piece-stack-height 
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }} {:row 0, :col 0}) )))

)(deftest lookup-piece-test
  
  (testing "lookup-piece, empty board"
    (is (=
      nil
      (board/lookup-piece {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-piece, small board"
    (is (=
      {:color :black, :type :beetle}
      (board/lookup-piece 
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }} {:row 0, :col 0}) )))

)(deftest lookup-piece-at-height-test
  
  (testing "lookup-piece-at-height, empty board"
    (is (=
      nil
      (board/lookup-piece-at-height {:pieces {}} {:row 0, :col 0} 0) )))

  (testing "lookup-piece-at-height, small board, height 0"
    (is (=
      {:color :white, :type :queen-bee}
      (board/lookup-piece-at-height 
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }} {:row 0, :col 0} 0) )))
  
  (testing "lookup-piece-at-height, small board, height 1"
    (is (=
      {:color :black, :type :beetle}
      (board/lookup-piece-at-height 
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}]
        }} {:row 0, :col 0} 1) )))

)(deftest lookup-adjacent-positions-test
  
  (testing "lookup-adjacent-positions, empty board"
    (is (=
      { 0 {:contents nil, :direction 0, :height 0, :position {:col 0, :row -2}},
        60 {:contents nil, :direction 60, :height 0, :position {:col 1, :row -1}},
        120 {:contents nil, :direction 120, :height 0, :position {:col 1, :row 1}},
        180 {:contents nil, :direction 180, :height 0, :position {:col 0, :row 2}},
        240 {:contents nil, :direction 240, :height 0, :position {:col -1, :row 1}},
        300 {:contents nil, :direction 300, :height 0, :position {:col -1, :row -1}}}
      (board/lookup-adjacent-positions {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-adjacent-positions, medium board"
    (is (=
      { 0   {:direction 0,   :position {:row -2, :col  0}, :height 2, :contents [{:color :white, :type :grasshopper}, {:color :black, :type :beetle}]}
        60  {:direction 60,  :position {:row -1, :col  1}, :height 0, :contents nil}
        120 {:direction 120, :position {:row  1, :col  1}, :height 0, :contents nil}
        180 {:direction 180, :position {:row  2, :col  0}, :height 2, :contents [{:color :black, :type :soldier-ant}, {:color :white, :type :beetle}]}
        240 {:direction 240, :position {:row  1, :col -1}, :height 0, :contents nil}
        300 {:direction 300, :position {:row -1, :col -1}, :height 0, :contents nil} }
      (board/lookup-adjacent-positions 
        {:pieces {
          {:row 0,  :col 0} [{:color :white, :type :queen-bee}]
          {:row 2,  :col 0} [{:color :black, :type :soldier-ant}, {:color :white, :type :beetle}]
          {:row -2, :col 0} [{:color :white, :type :grasshopper}, {:color :black, :type :beetle}] }}
        {:row 0, :col 0}) )))

)(deftest generate-can-slide-lookup-table-test

  (testing "generate-can-slide-lookup-table"
    (is (=
      { ; can-slide-lookup-table (written by hand in javascript and ported to clojure, then moved to test
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
      }
      (board/generate-can-slide-lookup-table board/can-slide-lookup-table-seed) )))

)(deftest encode-slide-lookup-key-from-adjacencies-test
  
  (testing "encode-slide-lookup-key-from-adjacencies, medium board"
    (is (=
      "1..1.."
      (board/encode-slide-lookup-key-from-adjacencies
        (board/lookup-adjacent-positions {:pieces {
          {:row 0,  :col 0} [{:color :white, :type :queen-bee}]
          {:row 2,  :col 0} [{:color :black, :type :soldier-ant}, {:color :white, :type :beetle}]
          {:row -2, :col 0} [{:color :white, :type :grasshopper}, {:color :black, :type :beetle}] }}
        {:row 0, :col 0})) )))

)(deftest render-valid-positions-from-slide-lookup-val-test
  
  (testing "render-valid-positions-from-slide-lookup-val, lookup val with two positions"
    (is (=
      '({:row -2, :col 0}, {:row 2, :col 0})
      (board/render-valid-positions-from-slide-lookup-val "1..1.." {:row 0, :col 0}) )))

)(deftest lookup-adjacent-slide-positions-test
  
  (testing "lookup-adjacent-slide-positions, empty board"
    (is (=
      '()
      (board/lookup-adjacent-slide-positions {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-adjacent-slide-positions, the girl next door"
    (is (=
      '({:row -1, :col 1}, {:row -1, :col -1})
      (board/lookup-adjacent-slide-positions 
        (board/remove-piece {:pieces {
          {:row 0,  :col 0} [{:color :white, :type :spider}]
          {:row -2, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

  (testing "lookup-adjacent-slide-positions, climb out of the pit"
    (is (=
      '({:row -2, :col 0}, {:row -1, :col 1})
      (board/lookup-adjacent-slide-positions
        (board/remove-piece {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row 1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 2, :col 0} [{:color :black, :type :soldier-ant}]
          {:row 1, :col -1} [{:color :black, :type :soldier-ant}]
          {:row -1, :col -1} [{:color :black, :type :soldier-ant}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

  (testing "lookup-adjacent-slide-positions, imminent loss"
    (is (=
      '()
      (board/lookup-adjacent-slide-positions
        (board/remove-piece {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :soldier-ant}]
          {:row -1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}]
          {:row -1, :col -1} [{:color :black, :type :beetle}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

)(deftest lookup-adjacent-climb-positions-test
  
  (testing "lookup-adjacent-climb-positions, empty board"
    (is (=
      '()
      (board/lookup-adjacent-climb-positions {:pieces {}} {:row 0, :col 0}) )))

  (testing "lookup-adjacent-climb-positions, the girl next door"
    (is (=
      '({:row -2, :col 0})
      (board/lookup-adjacent-climb-positions 
        (board/remove-piece {:pieces {
          {:row 0,  :col 0} [{:color :white, :type :spider}]
          {:row -2, :col 0} [{:color :white, :type :queen-bee}, {:color :black, :type :beetle}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

  (testing "lookup-adjacent-climb-positions, climb out of the pit"
    (is (=
      '({:row 1, :col 1} {:row 2, :col 0} {:row 1, :col -1} {:row -1, :col -1})
      (board/lookup-adjacent-climb-positions
        (board/remove-piece {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row 1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 2, :col 0} [{:color :black, :type :soldier-ant}]
          {:row 1, :col -1} [{:color :black, :type :soldier-ant}]
          {:row -1, :col -1} [{:color :black, :type :soldier-ant}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

  (testing "lookup-adjacent-climb-positions, imminent loss"
    (is (=
      '({:row -2, :col 0} {:row -1, :col 1} {:row 1, :col 1} {:row 2, :col 0} {:row 1, :col -1} {:row -1, :col -1})
      (board/lookup-adjacent-climb-positions
        (board/remove-piece {:pieces {
          {:row 0, :col 0} [{:color :white, :type :queen-bee}]
          {:row -2, :col 0} [{:color :black, :type :soldier-ant}]
          {:row -1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 1, :col 1} [{:color :black, :type :soldier-ant}]
          {:row 2, :col 0} [{:color :black, :type :spider}]
          {:row 1, :col -1} [{:color :black, :type :spider}]
          {:row -1, :col -1} [{:color :black, :type :beetle}] }} {:row 0, :col 0})
        {:row 0, :col 0}) )))

)(deftest board-movement-meta-test
  
  

)(deftest lookup-occupied-adjacencies-test
  
  (testing "lookup-occupied-adjacencies, U-shape"
    (is (=
      '({:row 1, :col 1} {:row 2, :col 0} {:row 1, :col -1} {:row -1, :col -1})
      (board/lookup-occupied-adjacencies {:pieces {
        {:row 0, :col 0} [{:color :white, :type :queen-bee}]
        {:row 1, :col 1} [{:color :black, :type :soldier-ant}]
        {:row 2, :col 0} [{:color :black, :type :soldier-ant}]
        {:row 1, :col -1} [{:color :black, :type :soldier-ant}]
        {:row -1, :col -1} [{:color :black, :type :soldier-ant}] }} {:row 0, :col 0}) )))

)

