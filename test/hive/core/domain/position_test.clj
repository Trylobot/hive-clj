(ns hive.core.domain.position-test
  (:require [clojure.test :refer :all]))
(require '[hive.core.domain.position :as position])

(deftest position-test
  (testing "position/create"
    (is (= (position/create 0 0) {:row 0, :col 0}))))


