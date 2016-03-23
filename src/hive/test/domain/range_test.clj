(ns hive.test.domain.range-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.range :as range])

(deftest is-range?-test
  
  (testing "is-range?, nil"
    (is (=
      {:min 0, :max :infinity}
      (range/is-range? nil) )))

  (testing "is-range?, number"
    (is (=
      {:min 3, :max 3}
      (range/is-range? 3) )))

  (testing "is-range?, vector of two numbers"
    (is (=
      {:min 0, :max 3}
      (range/is-range? [0 3]) )))

  (testing "is-range?, map with :min,:max"
    (is (=
      {:min 0, :max 3}
      (range/is-range? {:min 0, :max 3}) )))

  (testing "is-range?, vector of two numbers, out of order"
    (is (=
      {:min 0, :max 3}
      (range/is-range? [3 0]) )))

  (testing "is-range?, map with :min,:max, out of order"
    (is (=
      {:min 0, :max 3}
      (range/is-range? {:min 3, :max 0}) )))

)(deftest is-range-seq?-test
  
  (testing "is-range-seq?, vector of nils"
    (is (=
      [{:min 0, :max :infinity}, {:min 0, :max :infinity}, {:min 0, :max :infinity}]
      (range/is-range-seq? [nil nil nil]) )))

  (testing "is-range-seq?, vector of numbers (aka simple-range)"
    (is (=
      [{:min 3, :max 3}, {:min 3, :max 3}, {:min 3, :max 3}]
      (range/is-range-seq? [3 3 3]) )))

  (testing "is-range-seq?, vector of simple-ranges"
    (is (=
      [{:min 0, :max 3}, {:min 0, :max 3}, {:min 0, :max 3}]
      (range/is-range-seq? [[0 3] [0 3] [0 3]]) )))

  (testing "is-range-seq?, list of maps (aka normalized simple-ranges)"
    (is (=
      [{:min 0, :max 3}, {:min 0, :max 3}, {:min 0, :max 3}]
      (range/is-range-seq? '({:min 0, :max 3}, {:min 0, :max 3}, {:min 0, :max 3})) )))

  (testing "is-range-seq?, nil (x 3)"
    (is (=
      [{:min 0, :max :infinity}, {:min 0, :max :infinity}, {:min 0, :max :infinity}]
      (range/is-range-seq? nil 3) )))

  (testing "is-range-seq?, 3 (x 3)"
    (is (=
      [{:min 3, :max 3}, {:min 3, :max 3}, {:min 3, :max 3}]
      (range/is-range-seq? 3 3) )))

  (testing "is-range-seq?, simple-range (x 3)"
    (is (=
      [{:min 0, :max 3}, {:min 0, :max 3}, {:min 0, :max 3}]
      (range/is-range-seq? [0 3] 3) )))

)
