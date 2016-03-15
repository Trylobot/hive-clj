(ns hive.test.domain.position-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])

(deftest create
  
  (testing "create 0,0"
    (is (= 
      {:row 0, :col 0}
      (position/create 0 0) )))

)(deftest translation
  
  (testing "translate 0,0 -> 0"
    (is (=
      {:row -2, :col 0}
      (position/translation {:row 0, :col 0} 0) )))
  (testing "translate 0,0 -> 60"
    (is (=
      {:row -1, :col 1}
      (position/translation {:row 0, :col 0} 60) )))
  (testing "translate 0,0 -> 120"
    (is (=
      {:row 1, :col 1}
      (position/translation {:row 0, :col 0} 120) )))
  (testing "translate 0,0 -> 180"
    (is (=
      {:row 2, :col 0}
      (position/translation {:row 0, :col 0} 180) )))
  (testing "translate 0,0 -> 240"
    (is (=
      {:row 1, :col -1}
      (position/translation {:row 0, :col 0} 240) )))
  (testing "translate 0,0 -> 300"
    (is (=
      {:row -1, :col -1}
      (position/translation {:row 0, :col 0} 300) )))

)(deftest adjacencies

  (testing "adjacencies of -5,-1"
    (is (=
      '({:row -7, :col -1} {:row -6, :col 0} {:row -4, :col 0} {:row -3, :col -1} {:row -4, :col -2} {:row -6, :col -2})
      (position/adjacencies {:row -5, :col -1}) )))

)(deftest rotation

  (testing "rotation -> 0 clockwise"
    (is (=
      60
      (position/rotation 0 :cw) )))
  (testing "rotation -> 0 counter-clockwise"
    (is (=
      300
      (position/rotation 0 :ccw) )))
  
  (testing "rotation -> 60 clockwise"
    (is (=
      120
      (position/rotation 60 :cw) )))
  (testing "rotation -> 60 counter-clockwise"
    (is (=
      0
      (position/rotation 60 :ccw) )))
  
  (testing "rotation -> 120 clockwise"
    (is (=
      180
      (position/rotation 120 :cw) )))
  (testing "rotation -> 120 counter-clockwise"
    (is (=
      60
      (position/rotation 120 :ccw) )))
  
  (testing "rotation -> 180 clockwise"
    (is (=
      240
      (position/rotation 180 :cw) )))
  (testing "rotation -> 180 counter-clockwise"
    (is (=
      120
      (position/rotation 180 :ccw) )))
  
  (testing "rotation -> 240 clockwise"
    (is (=
      300
      (position/rotation 240 :cw) )))
  (testing "rotation -> 240 counter-clockwise"
    (is (=
      180
      (position/rotation 240 :ccw) )))
  
  (testing "rotation -> 300 clockwise"
    (is (=
      0
      (position/rotation 300 :cw) )))
  (testing "rotation -> 300 counter-clockwise"
    (is (=
      240
      (position/rotation 300 :ccw) )))

)

