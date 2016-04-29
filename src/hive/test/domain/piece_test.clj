(ns hive.test.domain.piece-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.piece :as piece])

(deftest like?-test

  (testing "like?, piece should match exact spec"
    (is (=
      true
      (piece/like? {:color :black, :type :queen-bee} :black :queen-bee) )))

  (testing "like?, piece should match color-only spec"
    (is (=
      true
      (piece/like? {:color :black, :type :queen-bee} :black nil) )))

  (testing "like?, piece should match type-only spec"
    (is (=
      true
      (piece/like? {:color :black, :type :queen-bee} nil :queen-bee) )))

  (testing "like?, piece should match loosest-possible spec (nothing specified)"
    (is (=
      true
      (piece/like? {:color :black, :type :queen-bee} nil nil) )))

)