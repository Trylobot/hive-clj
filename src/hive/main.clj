(ns hive.main (:gen-class))

(require '[hive.core.domain.position :as position])
(require '[hive.core.domain.piece :as piece])
(require '[hive.core.domain.board :as board])
(require '[hive.core.schema.position-schema :as position-schema])
(require '[hive.core.schema.piece-schema :as piece-schema])
(require '[hive.core.schema.board-schema :as board-schema])

(require 'clojure.test)
(require 'hive.test.domain.board-test)
(require 'hive.test.domain.position-test)
(require 'hive.test.schema.board-schema-test)

(defn run-hive-tests []
  (clojure.test/run-all-tests #"hive.test\..*") )
