(ns hive.test.schema.board-schema-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[schema.core :as s])
(require '[hive.core.schema.board-schema :as board-schema])

(deftest v1-test
  
  (testing "v1, default value"
    (is (s/validate board-schema/v1
      {"pieces" nil} )))

  (testing "v1, tiny board"
    (is (s/validate board-schema/v1
      {"pieces" {"0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]}} )))

  (testing "v1, small board"
    (is (s/validate board-schema/v1
      {"pieces" {
        "0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]
        "2,0" [{"color" "White","type" "Grasshopper"}] }} )))

)(deftest v2-test

  (testing "v2, default value"
    (is (s/validate board-schema/v2
      {:pieces nil} )))

  (testing "v2, tiny board"
    (is (s/validate board-schema/v2
      {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}} )))

  (testing "v2, small board"
    (is (s/validate board-schema/v2
      {:pieces {
        {:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]
        {:row 2, :col 0} [{:color :white, :type :beetle}] }} )))

  (testing "upgrade, v1 --> v2"
    (is (= 
      {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}}
      (board-schema/upgrade-v1-to-v2 
        {"pieces" {"0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]}} ) )))

  (testing "revert, v2 --> v1"
    (is (= 
      {"pieces" {"0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]}}
      (board-schema/revert-v2-to-v1 
        {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}} ) )))

)
