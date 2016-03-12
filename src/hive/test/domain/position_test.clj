(ns hive.test.domain.position-test)
(require '[clojure.test :refer [deftest testing is]])
(require '[hive.core.domain.position :as position])

(deftest position-test
  
  (testing "create 0 0"
    (is (= 
      {:row 0, :col 0}
      (position/create 0 0) )))

)

