(ns hive.test.domain.rules-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])
(require '[hive.core.domain.rules :as rules])

(deftest game-over?-test
  
  (testing "game-over?, no queens"
    (is (= 
      {:game-over false, :is-draw false, :winner nil}
      (rules/game-over?
        {:pieces {
          {:row 0, :col 0} [{:color :white, :type :spider}]
          {:row 2, :col 0} [{:color :black, :type :spider}] }}) )))

)(deftest force-queen-placement?-test

  (testing "force-queen-placement?, queen bee placement not yet required"
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

  (testing "force-queen-placement?, queen bee must now be placed"
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
        (-> board/create
          (board/place-piece (piece/create :white :spider) (position/create 0 0))
          (board/place-piece (piece/create :black :spider) (position/create -2 0))
          (board/place-piece (piece/create :white :beetle) (position/create -3 1))
          (board/place-piece (piece/create :black :spider) (position/create 1 1))
          (board/place-piece (piece/create :white :soldier-ant) (position/create 1 -1))
          (board/place-piece (piece/create :black :soldier-ant) (position/create -3 -1))
          (board/place-piece (piece/create :white :queen-bee) (position/create -4 0))
          (board/place-piece (piece/create :black :queen-bee) (position/create 2 0)) )
        8) )))

)
