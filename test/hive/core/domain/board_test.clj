(ns hive.core.domain.board-test
  (:require [clojure.test :refer :all]))
(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.board :as board])

(deftest board-test
  
  (testing "place_piece"
    (is (= 
      (board/place_piece board/create "piece" (position/create 0 0))
      {:pieces {{:row 0, :col 0} ["piece"]}} )))

)
