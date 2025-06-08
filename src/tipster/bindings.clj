(ns tipster.bindings
  (:require [tipster.terms :as terms]))

;; === СИСТЕМА СВЯЗЫВАНИЯ ПЕРЕМЕННЫХ ===

;; --- Среда связывания переменных ---

(defn empty-bindings 
  "Пустая среда связывания"
  [] 
  {})

(defn lookup-binding 
  "Поиск связывания переменной"
  [var bindings]
  (get bindings (:id var)))

(defn bind-variable 
  "Связывание переменной с термом"
  [var term bindings]
  (assoc bindings (:id var) term))

(defn occurs-check 
  "Проверка вхождения переменной в терм (предотвращение бесконечных структур)"
  [var term bindings]
  (letfn [(occurs? [var term]
            (let [term (terms/auto-convert-term term)]
              (cond
                (terms/is-variable? term) 
                (if (= (:id var) (:id term))
                  true
                  (when-let [binding (lookup-binding term bindings)]
                    (occurs? var binding)))
                
                (terms/is-compound? term)
                (some #(occurs? var %) (:args term))
                
                :else false)))]
    (occurs? var term)))

;; --- Дереференсация термов ---

(defn deref-term 
  "Дереференсация терма с учетом связываний"
  [term bindings]
  (cond
    (satisfies? terms/ITerm term)
    (if (terms/is-variable? term)
      (if-let [binding (lookup-binding term bindings)]
        (recur binding bindings)
        term)
      term)
    
    ;; Обрабатываем нативные типы Clojure
    :else term))
