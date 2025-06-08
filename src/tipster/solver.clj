(ns tipster.solver
  (:require [tipster.bindings :as bindings]
            [tipster.knowledge :as knowledge]))

;; === ЛОГИЧЕСКИЙ СОЛВЕР ===

(defn solve-goals 
  "Решение множества целей"
  ([goals] (solve-goals goals (bindings/empty-bindings)))
  ([goals bindings]
   (cond
     (empty? goals) 
     [bindings] ; Все цели решены
     
     :else
     (let [goal (first goals)
           remaining-goals (rest goals)]
       (lazy-cat
         ;; Поиск фактов
         (mapcat (fn [{:keys [bindings fact]}]
                   (solve-goals remaining-goals bindings))
                 (knowledge/matching-facts goal bindings))
         
         ;; Поиск правил
         (mapcat (fn [{:keys [bindings body]}]
                   (solve-goals (concat body remaining-goals) bindings))
                 (knowledge/matching-rules goal bindings)))))))

(defn solve-goal 
  "Решение одной цели"
  ([goal] (solve-goal goal (bindings/empty-bindings)))
  ([goal bindings]
   (solve-goals [goal] bindings)))
