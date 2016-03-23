(ns hive.test.util-test)
(require '[hive.core.util :as util])
(require '[clojure.test :refer [deftest testing is]])

(deftest rotate-string-left-test

  (testing "rotate-string-left, \"1.....\""
    (is (=
      ".....1"
      (util/rotate-string-left "1.....") )))

)(deftest contains-value?-test

  (testing "contains-value?, vector of strings"
    (is (=
      true
      (util/contains-value? ["a","b","c"] "b") )))

)(deftest fill-test
  
  (testing "fill, no-op"
    (is (=
      []
      (util/fill [] 0 nil) )))

  (testing "fill, pad"
    (is (=
      [nil nil nil]
      (util/fill [] 3 nil) )))
  
  (testing "fill, truncate"
    (is (=
      [nil]
      (util/fill [nil nil nil] 1 nil) )))

)
