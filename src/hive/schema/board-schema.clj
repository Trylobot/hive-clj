(ns hive.schema.board-schema)
(require '[schema.core :as s])
(require '[hive.core.domain.board :as board])

(def v1 "board version 1; schema for board model compatible with original, javascript format"
  {"pieces" {
    s/Str [
      {"color" s/Str, "type" s/Str} ]} })
; {"pieces":{"0,0":[{"color":"White","type":"Beetle"},{"color":"Black","type":"Queen Bee"}]}}

(def v2 "board version 2; clojure derivative of version 1; position-keys are structures, and keywords where possible"
  {:pieces {
    {:row s/Num, :col s/Num} [
      {:color s/Keyword, :type s/Keyword} ]} })
; {:pieces {{:row 0, :col 0} [{:color :white, :type :beetle} {:color :black, :type :queen-bee}]}}

(def v3 "board version 3; compressed derivative of version 2"
  {})
; [[0 0] [[:white :beetle] [:black :queen-bee]]]

(def v4 "board version 4; sequence of piece-stacks packed into a nil-padded spiral"
  {})
; [nil [[:white :beetle] [:black :queen-bee]]]

(def v5 "board version 5; sequence of pieces with 3D position vectors"
  {})
; {[0 0 0] [:white :queen-bee], [0 0 1] [:black :beetle]}

  ; {:a {:b s/Str
  ;      :c s/Int}
  ;  :d [{:e s/Keyword
  ;       :f [s/Num]}]})

; (s/validate
;   Data
;   {:a {:b "abc"
;        :c 123}
;    :d [{:e :bc
;         :f [12.2 13 100]}
;        {:e :bc
;         :f [-1]}]})
;; Success!

; (s/validate
;   Data
;   {:a {:b 123
;        :c "ABC"}})
;; Exception -- Value does not match schema:
;;  {:a {:b (not (instance? java.lang.String 123)),
;;       :c (not (integer? "ABC"))},
;;   :d missing-required-key}
