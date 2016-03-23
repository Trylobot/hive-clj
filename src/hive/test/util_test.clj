(ns hive.test.util-test)
(require '[hive.core.util :as util])
(require '[clojure.test :refer [deftest testing is]])

(deftest rotate-string-left-test

  (testing "rotate-string-left, \"1.....\""
    (is (=
      ".....1"
      (util/rotate-string-left "1.....") )))

)
