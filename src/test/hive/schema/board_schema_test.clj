(ns hive.schema.board-schema-test
  (:require [clojure.test :refer :all]))
(require '[schema.core :as s])
(require '[hive.schema.board-schema :as board-schema])

(deftest board-schema-test
  
  ; v1
  (testing "v1, default value"
    (s/validate board-schema/v1
      {"pieces" nil} ))

  (testing "v1, typical early board"
    (s/validate board-schema/v1
      {"pieces" {"0,0" [{"color" "White","type" "Beetle"},{"color" "Black","type" "Queen Bee"}]}} ))
)
