(ns tipster.knowledge
  (:require [tipster.terms :as terms]
            [tipster.unification :as unif]))

;; === БАЗА ЗНАНИЙ ===

(defonce knowledge-base (atom {:facts #{} :rules #{}}))

(defn add-fact! 
  "Добавление факта в базу знаний"
  [fact]
  (swap! knowledge-base update :facts conj fact))

(defn add-rule! 
  "Добавление правила в базу знаний"
  [head body]
  (swap! knowledge-base update :rules conj {:head head :body body}))

(defn clear-knowledge-base! 
  "Очистка базы знаний"
  []
  (reset! knowledge-base {:facts #{} :rules #{}}))

;; --- Поиск подходящих фактов и правил ---

(defn matching-facts 
  "Поиск фактов, унифицируемых с целью"
  [goal bindings]
  (keep (fn [fact]
          (when-let [unified-bindings (unif/unify goal fact bindings)]
            {:bindings unified-bindings :fact fact}))
        (:facts @knowledge-base)))

(defn rename-variables 
  "Переименование переменных в правиле для избежания конфликтов"
  [term]
  (let [term (terms/auto-convert-term term)
        var-map (atom {})]
    (letfn [(rename [t]
              (let [t (terms/auto-convert-term t)]
                (cond
                  (terms/is-variable? t)
                  (if-let [renamed (@var-map (:id t))]
                    renamed
                    (let [new-var (terms/make-variable (:name t))]
                      (swap! var-map assoc (:id t) new-var)
                      new-var))
                  
                  (terms/is-compound? t)
                  (terms/make-compound (:functor t) (map rename (:args t)))
                  
                  (coll? t) ; Обработка коллекций
                  (map rename t)
                  
                  :else t)))]
      (rename term))))

(defn matching-rules 
  "Поиск правил, унифицируемых с целью"
  [goal bindings]
  (keep (fn [rule]
          (let [rule-head (rename-variables (:head rule))
                rule-body (vec (rename-variables (:body rule)))]
            (when-let [unified-bindings (unif/unify goal rule-head bindings)]
              {:bindings unified-bindings 
               :head rule-head 
               :body rule-body})))
        (:rules @knowledge-base)))
