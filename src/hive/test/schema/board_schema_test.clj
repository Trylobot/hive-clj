(ns hive.test.schema.board-schema-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[schema.core :as s])
(require '[hive.core.schema.board-schema :as board-schema])

(deftest board-schema-test
  
  ; v1
  (testing "v1, default value"
    (is (s/validate board-schema/v1
      {"pieces" nil} )))

  (testing "v1, typical early board"
    (is (s/validate board-schema/v1
      {"pieces" {"0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]}} )))

  ; v2
  (testing "v2, default value"
    (is (s/validate board-schema/v2
      {:pieces nil} )))

  (testing "v2, typical early board"
    (is (s/validate board-schema/v2
      {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}} )))

  ; conversions
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
