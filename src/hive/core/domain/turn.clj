(ns hive.core.domain.turn)

; turn
;   represents a hive turn (movement, placement)

(def turn-types-enum #{
  :placement
  :movement
  :special-ability
  :forfeit
  :error
  :unknown })

(defn create-placement
  [piece-type destination]
    { :turn-type :placement
      :piece-type piece-type
      :destination destination })

(defn create-movement
  [source destination]
  { :turn-type :movement
    :source source
    :destination destination })

(defn create-special-ability
  [ability-user source destination]
  { :turn-type :special-ability
    :ability-user ability-user
    :source source
    :destination destination })

(defn create-forfeit
  []
  { :turn-type :forfeit })

(defn create-error
  [error]
  { :turn-type :error
    :error error })

(defn create-unknown
  []
  { :turn-type :unknown })

