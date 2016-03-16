(def b {:pieces {{:row 0,  :col 0} [{:color :white, :type :spider}] 
                 {:row -2, :col 0} [{:color :white, :type :queen-bee}, 
                                    {:color :black, :type :beetle}] }})
(def p {:row 0, :col 0})
(board/lookup-slide-destinations b p)
