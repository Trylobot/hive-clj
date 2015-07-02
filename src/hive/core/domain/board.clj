(ns hive.core.domain.board)
(require '[hive.core.domain.position :as position])

;; board
;;   for dealing with hive board states
;;   querying the board and moving pieces about 

(defn origin [] (position/create 0 0))

(defn create [] {:pieces {}})

(defn place_piece [])

