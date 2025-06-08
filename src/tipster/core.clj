(ns tipster.core
  (:require [clojure.set :as set]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]
            [tipster.unification :as unif]
            [tipster.knowledge :as knowledge]
            [tipster.solver :as solver]))

;; === ИНТЕГРАЦИЯ С CLOJURE ===

;; --- Макросы для удобной работы ---

(defmacro deffact 
  "Макрос для определения фактов"
  [fact-expr]
  `(knowledge/add-fact! (terms/clojure-term->tipster-term '~fact-expr)))

(defmacro defrule 
  "Макрос для определения правил"
  [head-expr body-exprs]
  `(knowledge/add-rule! 
     (terms/clojure-term->tipster-term '~head-expr)
     (map terms/clojure-term->tipster-term '~body-exprs)))

(defmacro query 
  "Макрос для запросов"
  [goal-expr]
  `(let [goal# (terms/clojure-term->tipster-term '~goal-expr)
         solutions# (solver/solve-goal goal#)]
     (map #(terms/tipster-term->clojure-term goal# % bindings/deref-term) solutions#)))

;; --- Функции для работы с предикатами Clojure ---

(defn integrate-clojure-predicate 
  "Интеграция предиката Clojure в систему Tipster"
  [predicate-fn]
  (fn [& args]
    (let [clojure-result (apply predicate-fn args)]
      (if clojure-result
        [(bindings/empty-bindings)] ; Успешное решение
        []))))                      ; Неудачное решение

;; --- Встроенные предикаты ---

(defn builtin-predicates 
  "Встроенные предикаты для интеграции с Clojure"
  []
  {'= (fn [x y bindings]
        (if-let [unified (unif/unify x y bindings)]
          [unified]
          []))
   'integer? (integrate-clojure-predicate integer?)
   'string? (integrate-clojure-predicate string?)
   'number? (integrate-clojure-predicate number?)
   '> (integrate-clojure-predicate >)
   '< (integrate-clojure-predicate <)
   '>= (integrate-clojure-predicate >=)
   '<= (integrate-clojure-predicate <=)})

;; --- Функции для тестирования и демонстрации ---

(defn reset-tipster! 
  "Сброс состояния Tipster"
  []
  (knowledge/clear-knowledge-base!))

(defn demo-tipster []
  "Демонстрация возможностей Tipster"
  (println "=== Демонстрация Tipster ===")
  
  ;; Очистка базы знаний
  (reset-tipster!)
  
  ;; Добавление фактов
  (deffact (человек алиса))
  (deffact (человек боб))
  (deffact (человек чарли))
  (deffact (родитель алиса боб))
  (deffact (родитель боб чарли))
  (deffact (возраст алиса 45))
  (deffact (возраст боб 25))
  (deffact (возраст чарли 5))
  
  ;; Добавление правил
  (defrule (дедушка ?X ?Z) [(родитель ?X ?Y) (родитель ?Y ?Z)])
  (defrule (взрослый ?X) [(человек ?X) (возраст ?X 45)])
  (defrule (взрослый ?X) [(человек ?X) (возраст ?X 25)])
  
  ;; Запросы
  (println "\nЗапрос: кто является человеком?")
  (doseq [result (query (человек ?X))]
    (println "  " result))
  
  (println "\nЗапрос: кто чей родитель?")
  (doseq [result (query (родитель ?X ?Y))]
    (println "  " result))
  
  (println "\nЗапрос: кто дедушка?")
  (doseq [result (query (дедушка ?X ?Y))]
    (println "  " result))
  
  (println "\nЗапрос: кто взрослый?")
  (doseq [result (query (взрослый ?X))]
    (println "  " result)))

(defn run-tipster 
  "Запуск демонстрации Tipster"
  []
  (demo-tipster))

(defn -main [& args]
  (println "Tipster - функционально-логический движок")
  (println "Интеграция Clojure и логического программирования")
  (run-tipster))
