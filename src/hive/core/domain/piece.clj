(ns hive.core.domain.piece)

; piece
;   represents a single hive piece

(def colors-enum #{
  :white
  :black })

(def types-enum #{
  :queen-bee
  :beetle
  :grasshopper
  :spider
  :soldier-ant
  :mosquito
  :ladybug
  :pillbug })

(defn create [color type]
  {:pre [
    (contains? colors-enum color)
    (contains? types-enum type)]}
  {:color color, :type type})

(defn is? [piece color type]
  (and (or (nil? color) (= (:color piece) color) )
       (or (nil? type)  (= (:type  piece) type)  )) )



