(def board 
  {:pieces {
    {:row 0, :col 0} [{:color :white, :type :queen-bee}]
    {:row 2, :col 0} [{:color :black, :type :grasshopper}]
    {:row 1, :col -1} [{:color :black, :type :grasshopper}] }}) 

(def color-filter :white) 

(def pieces (:pieces board)) 
; (def potentials (apply clojure.set/union (map position/adjacencies (keys pieces))))
(def potentials 
#{{:row  0, :col -2} ;           actual
  {:row  2, :col -2} ;           actual
  {:row -1, :col -1} ;           actual
  {:row  1, :col -1}
  {:row  3, :col -1} ;           actual
  {:row -2, :col  0} ; expected, actual
  {:row  0, :col  0}
  {:row  2, :col  0}
  {:row  4, :col  0} ;           actual
  {:row -1, :col  1} ; expected, actual
  {:row  1, :col  1} ;           actual
  {:row  3, :col  1}});          actual

  ; (def potential (first potentials))
  (def potential {:row  1, :col -1})

  (def is-empty (nil? (get pieces potential))) 
  (def adjacencies (board/lookup-adjacent-positions board potential)) 
  (def colors (map #(:color (last (:contents %))) (vals adjacencies))) ; <-- bug
  (def passes-filter (map #(or (nil? %) (= color-filter %)) colors)) 
  (def all-pass (reduce #(and %1 %2) passes-filter)) 
  (def is-free-space (and is-empty all-pass)) 

(def expected 
#{{:row -2, :col 0}
  {:row -1, :col 1}})

(def actual
#{{:row  0, :col -2}
  {:row  2, :col -2}
  {:row -1, :col -1}
  {:row  3, :col -1}
  {:row -2, :col  0}
  {:row  4, :col  0}
  {:row -1, :col  1}
  {:row  1, :col  1}
  {:row  3, :col  1}})
