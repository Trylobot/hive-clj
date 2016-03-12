(ns hive.core.domain.position-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])

(deftest position-test
  
  (testing "create 0 0"
    (is (= (position/create 0 0) {:row 0, :col 0})))

)
