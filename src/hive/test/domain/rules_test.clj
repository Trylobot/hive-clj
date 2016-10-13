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

)
