(ns hive.core.domain.piece)

; piece
;   represents a single hive piece

(def colors_enum #{
    "White"
    "Black"
})

(def types_enum #{
    "Queen Bee"
    "Beetle"
    "Grasshopper"
    "Spider"
    "Soldier Ant"
    "Mosquito"
    "Ladybug"
    "Pillbug"
})

(defn create [color type]
    {:pre [
        (contains? colors_enum color)
        (contains? types_enum type)]}
    {:color color, :type type})




