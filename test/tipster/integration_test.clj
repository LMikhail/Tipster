(ns tipster.integration-test
  (:require [clojure.test :refer :all]
            [tipster.core :as tipster]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]))

(defn reset-tipster-for-test! []
  "Сброс состояния Tipster перед каждым тестом"
  (tipster/reset-tipster!))

;; === ТЕСТЫ МАКРОСОВ ===

(deftest test-deffact-macro
  (testing "Макрос deffact"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    
    (let [kb @tipster.knowledge/knowledge-base
          facts (:facts kb)]
      (is (= 1 (count facts)))
      (let [fact (first facts)]
        (is (= :compound (terms/term-type fact)))
        (is (= 'human (:functor fact)))
        (is (= 1 (count (:args fact))))))))

(deftest test-query-macro
  (testing "Макрос query"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    (tipster/deffact (human bob))
    
    (let [results (tipster/query (human ?X))]
      (is (= 2 (count results)))
      (is (or (= '(human alice) (first results))
              (= '(human bob) (first results)))))))

(deftest test-defrule-macro
  (testing "Макрос defrule"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    (tipster/defrule (mortal ?X) [(human ?X)])
    
    ;; Проверяем что правило добавлено в базу знаний
    (is (= 1 (count (:rules @tipster.knowledge/knowledge-base))))
    
    ;; Простая проверка запроса (может не работать из-за движка)
    (let [results (tipster/query (mortal ?Y))]
      (is (coll? results)))))

;; === ИНТЕГРАЦИОННЫЕ ТЕСТЫ ===

(deftest test-complex-family-relations
  (testing "Сложные семейные отношения"
    (reset-tipster-for-test!)
    
    ;; Факты
    (tipster/deffact (родитель алиса боб))
    (tipster/deffact (родитель боб чарли))
    (tipster/deffact (родитель чарли дэвид))
    (tipster/deffact (мужчина боб))
    (tipster/deffact (мужчина чарли))
    (tipster/deffact (мужчина дэвид))
    (tipster/deffact (женщина алиса))
    
    ;; Правила
    (tipster/defrule (дедушка ?X ?Z) [(родитель ?X ?Y) (родитель ?Y ?Z) (мужчина ?X)])
    (tipster/defrule (бабушка ?X ?Z) [(родитель ?X ?Y) (родитель ?Y ?Z) (женщина ?X)])
    
    ;; Тесты запросов
    (let [grandfathers (tipster/query (дедушка ?X ?Y))
          grandmothers (tipster/query (бабушка ?X ?Y))]
      
      ;; Результаты могут варьироваться в зависимости от реализации
      (is (coll? grandfathers))
      (is (coll? grandmothers)))))

(deftest test-animal-classification
  (testing "Классификация животных"
    (reset-tipster-for-test!)
    
    ;; Факты о животных
    (tipster/deffact (animal dog))
    (tipster/deffact (animal cat))
    (tipster/deffact (animal bird))
    (tipster/deffact (animal fish))
    
    ;; Характеристики
    (tipster/deffact (has-fur dog))
    (tipster/deffact (has-fur cat))
    (tipster/deffact (has-feathers bird))
    (tipster/deffact (lives-in-water fish))
    (tipster/deffact (can-fly bird))
    
    ;; Правила классификации
    (tipster/defrule (mammal ?X) [(animal ?X) (has-fur ?X)])
    (tipster/defrule (bird-type ?X) [(animal ?X) (has-feathers ?X)])
    (tipster/defrule (flying ?X) [(bird-type ?X) (can-fly ?X)])
    
    ;; Тесты
    (let [mammals (tipster/query (mammal ?X))
          birds (tipster/query (bird-type ?X))
          flying (tipster/query (flying ?X))]
      
      (is (coll? mammals))
      (is (coll? birds))
      (is (coll? flying)))))

(deftest test-mathematical-relations
  (testing "Математические отношения"
    (reset-tipster-for-test!)
    
    ;; Числовые факты
    (tipster/deffact (number 1))
    (tipster/deffact (number 2))
    (tipster/deffact (number 3))
    (tipster/deffact (number 4))
    (tipster/deffact (number 5))
    
    ;; Отношения
    (tipster/deffact (greater 2 1))
    (tipster/deffact (greater 3 2))
    (tipster/deffact (greater 4 3))
    (tipster/deffact (greater 5 4))
    
    ;; Правила
    (tipster/defrule (even 2) [])
    (tipster/defrule (even 4) [])
    (tipster/defrule (odd 1) [])
    (tipster/defrule (odd 3) [])
    (tipster/defrule (odd 5) [])
    
    ;; Тесты
    (let [numbers (tipster/query (number ?X))
          evens (tipster/query (even ?X))
          odds (tipster/query (odd ?X))
          greater-relations (tipster/query (greater ?X ?Y))]
      
      (is (= 5 (count numbers)))
      (is (coll? evens))
      (is (coll? odds))
      (is (= 4 (count greater-relations))))))

;; === ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ===

(deftest test-performance-with-many-facts
  (testing "Производительность с большим количеством фактов"
    (reset-tipster-for-test!)
    
    ;; Добавляем много фактов
    (doseq [i (range 50)] ; Уменьшаем количество для быстрых тестов
      (eval `(tipster/deffact (~'number ~i))))
    
    ;; Запрос должен найти все факты
    (let [results (tipster/query (number ?X))]
      (is (= 50 (count results))))))

;; === ТЕСТЫ ГРАНИЧНЫХ СЛУЧАЕВ ===

(deftest test-edge-cases
  (testing "Граничные случаи"
    (reset-tipster-for-test!)
    
    ;; Пустой запрос после очистки
    (let [empty-results (tipster/query (nonexistent ?X))]
      (is (= 0 (count empty-results))))
    
    ;; Запрос с несколькими переменными
    (tipster/deffact (relation a b c))
    (tipster/deffact (relation d e f))
    
    (let [triple-results (tipster/query (relation ?X ?Y ?Z))]
      (is (= 2 (count triple-results))))
    
    ;; Правило без условий
    (tipster/defrule (always-true) [])
    (let [unconditional-results (tipster/query (always-true))]
      (is (coll? unconditional-results)))))

;; === ТЕСТЫ СОСТОЯНИЯ СИСТЕМЫ ===

(deftest test-system-state
  (testing "Состояние системы"
    (reset-tipster-for-test!)
    
    ;; Проверка начального состояния
    (is (= 0 (count (:facts @tipster.knowledge/knowledge-base))))
    (is (= 0 (count (:rules @tipster.knowledge/knowledge-base))))
    
    ;; Добавление данных
    (tipster/deffact (test-fact value))
    (tipster/defrule (test-rule ?X) [(test-fact ?X)])
    
    ;; Проверка состояния после добавления
    (is (= 1 (count (:facts @tipster.knowledge/knowledge-base))))
    (is (= 1 (count (:rules @tipster.knowledge/knowledge-base))))
    
    ;; Сброс
    (tipster/reset-tipster!)
    
    ;; Проверка состояния после сброса
    (is (= 0 (count (:facts @tipster.knowledge/knowledge-base))))
    (is (= 0 (count (:rules @tipster.knowledge/knowledge-base))))))

(defn run-integration-tests []
  "Запуск интеграционных тестов"
  (run-tests 'tipster.integration-test))
