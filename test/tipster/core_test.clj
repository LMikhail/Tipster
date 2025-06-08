(ns tipster.core-test
  "Основные интеграционные тесты Tipster.
   
   Для запуска модульных тестов используйте:
   (require '[tipster.all-tests :as tests])
   (tests/run-all-tests)
   
   Отдельные модули:
   (tests/run-module-tests \"terms\")
   (tests/run-module-tests \"bindings\")
   (tests/run-module-tests \"unification\")
   (tests/run-module-tests \"knowledge\")
   (tests/run-module-tests \"solver\")
   (tests/run-module-tests \"integration\")"
  (:require [clojure.test :refer :all]
            [tipster.core :as tipster]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]
            [tipster.unification :as unif]
            [tipster.knowledge :as knowledge]
            [tipster.solver :as solver]
            [tipster.all-tests :as all-tests]))

(defn reset-tipster-for-test! []
  "Сброс состояния Tipster перед каждым тестом"
  (tipster/reset-tipster!))

;; === ТЕСТЫ БАЗОВЫХ КОМПОНЕНТОВ ===

(deftest test-term-creation
  (testing "Создание различных типов термов"
    (let [var (terms/make-variable "X")
          atom (terms/make-atom 'hello)
          compound (terms/make-compound 'f 'a 'b)]
      
      (is (terms/is-variable? var) "Переменная должна быть переменной")
      (is (not (terms/is-variable? atom)) "Атом не должен быть переменной")
      (is (not (terms/is-variable? compound)) "Составной терм не должен быть переменной")
      
      (is (terms/is-compound? compound) "Составной терм должен быть составным")
      (is (not (terms/is-compound? var)) "Переменная не должна быть составной")
      (is (not (terms/is-compound? atom)) "Атом не должен быть составным")
      
      (is (= :variable (terms/term-type var)))
      (is (= :atom (terms/term-type atom)))
      (is (= :compound (terms/term-type compound)))
      
      (is (= 'hello (terms/term-value atom)))
      (is (= compound (terms/term-value compound))))))

(deftest test-bindings-operations
  (testing "Операции со связываниями переменных"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          atom (terms/make-atom 'test)
          empty-bindings (bindings/empty-bindings)
          bindings-with-var1 (bindings/bind-variable var1 atom empty-bindings)]
      
      (is (= {} empty-bindings))
      (is (nil? (bindings/lookup-binding var1 empty-bindings)))
      (is (= atom (bindings/lookup-binding var1 bindings-with-var1)))
      (is (nil? (bindings/lookup-binding var2 bindings-with-var1))))))

(deftest test-deref-term
  (testing "Дереференсация термов"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          atom (terms/make-atom 'test)
          empty-bindings (bindings/empty-bindings)
          bindings {(:id var1) atom, (:id var2) var1}]
      
      ;; Несвязанная переменная
      (is (= var1 (bindings/deref-term var1 empty-bindings)))
      
      ;; Переменная связанная с атомом
      (is (= atom (bindings/deref-term var1 bindings)))
      
      ;; Цепочка переменных
      (is (= atom (bindings/deref-term var2 bindings)))
      
      ;; Атом остается атомом
      (is (= atom (bindings/deref-term atom bindings))))))

;; === ТЕСТЫ УНИФИКАЦИИ ===

(deftest test-unify-identical-terms
  (testing "Унификация идентичных термов"
    (let [atom1 (terms/make-atom 'test)
          atom2 (terms/make-atom 'test)
          compound1 (terms/make-compound 'f 'a 'b)
          compound2 (terms/make-compound 'f 'a 'b)]
      
      (is (= (bindings/empty-bindings) (unif/unify atom1 atom2)))
      (is (= (bindings/empty-bindings) (unif/unify compound1 compound2))))))

(deftest test-unify-different-atoms
  (testing "Неудачная унификация различных атомов"
    (let [atom1 (terms/make-atom 'test1)
          atom2 (terms/make-atom 'test2)]
      
      (is (nil? (unif/unify atom1 atom2))))))

(deftest test-unify-variable-with-atom
  (testing "Унификация переменной с атомом"
    (let [var (terms/make-variable "X")
          atom (terms/make-atom 'test)
          result (unif/unify var atom)]
      
      (is (not (nil? result)))
      (is (= atom (bindings/lookup-binding var result))))))

(deftest test-unify-variable-with-variable
  (testing "Унификация переменной с переменной"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          result (unif/unify var1 var2)]
      
      (is (not (nil? result)))
      (is (or (= var2 (bindings/lookup-binding var1 result))
              (= var1 (bindings/lookup-binding var2 result)))))))

(deftest test-unify-compound-terms
  (testing "Унификация составных термов"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          compound1 (terms/make-compound 'f var1 'b)
          compound2 (terms/make-compound 'f 'a var2)
          result (unif/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (terms/make-atom 'a) (bindings/lookup-binding var1 result)))
      (is (= (terms/make-atom 'b) (bindings/lookup-binding var2 result))))))

(deftest test-unify-different-functors
  (testing "Неудачная унификация термов с различными функторами"
    (let [compound1 (terms/make-compound 'f 'a)
          compound2 (terms/make-compound 'g 'a)]
      
      (is (nil? (unif/unify compound1 compound2))))))

(deftest test-unify-different-arity
  (testing "Неудачная унификация термов с различной арностью"
    (let [compound1 (terms/make-compound 'f 'a 'b)
          compound2 (terms/make-compound 'f 'a)]
      
      (is (nil? (unif/unify compound1 compound2))))))

(deftest test-occurs-check
  (testing "Проверка вхождения (occurs check)"
    (let [var (terms/make-variable "X")
          compound (terms/make-compound 'f var)
          bindings (bindings/empty-bindings)]
      
      (is (bindings/occurs-check var compound bindings))
      (is (nil? (unif/unify var compound))))))

;; === ТЕСТЫ БАЗЫ ЗНАНИЙ ===

(deftest test-knowledge-base-operations
  (testing "Операции с базой знаний"
    (reset-tipster-for-test!)
    
    (let [fact1 (terms/make-compound 'human 'alice)
          fact2 (terms/make-compound 'human 'bob)
          rule-head (terms/make-compound 'mortal (terms/make-variable "X"))
          rule-body [(terms/make-compound 'human (terms/make-variable "X"))]]
      
      ;; Добавление фактов
      (knowledge/add-fact! fact1)
      (knowledge/add-fact! fact2)
      
      ;; Добавление правила
      (knowledge/add-rule! rule-head rule-body)
      
      ;; Проверка содержимого базы знаний
      (let [kb @knowledge/knowledge-base]
        (is (= 2 (count (:facts kb))))
        (is (= 1 (count (:rules kb))))
        (is (contains? (:facts kb) fact1))
        (is (contains? (:facts kb) fact2))))))

(deftest test-clear-knowledge-base
  (testing "Очистка базы знаний"
    (reset-tipster-for-test!)
    
    (knowledge/add-fact! (terms/make-compound 'test 'value))
    (knowledge/add-rule! (terms/make-compound 'head 'x) [(terms/make-compound 'body 'x)])
    
    (is (> (count (:facts @knowledge/knowledge-base)) 0))
    (is (> (count (:rules @knowledge/knowledge-base)) 0))
    
    (knowledge/clear-knowledge-base!)
    
    (is (= 0 (count (:facts @knowledge/knowledge-base))))
    (is (= 0 (count (:rules @knowledge/knowledge-base))))))

;; === ТЕСТЫ ЛОГИЧЕСКОГО СОЛВЕРА ===

(deftest test-solve-simple-fact
  (testing "Решение простого факта"
    (reset-tipster-for-test!)
    
    (let [fact (terms/make-compound 'human 'alice)
          query (terms/make-compound 'human 'alice)]
      
      (knowledge/add-fact! fact)
      (let [solutions (solver/solve-goal query)]
        (is (= 1 (count solutions)))
        (is (= (bindings/empty-bindings) (first solutions)))))))

(deftest test-solve-fact-with-variable
  (testing "Решение факта с переменной"
    (reset-tipster-for-test!)
    
    (let [fact (terms/make-compound 'human 'alice)
          var (terms/make-variable "X")
          query (terms/make-compound 'human var)]
      
      (knowledge/add-fact! fact)
      (let [solutions (solver/solve-goal query)
            solution (first solutions)]
        (is (= 1 (count solutions)))
        (is (= (terms/make-atom 'alice) (bindings/lookup-binding var solution)))))))

(deftest test-solve-rule
  (testing "Решение через правило"
    (reset-tipster-for-test!)
    
    (let [fact (terms/make-compound 'human 'alice)
          rule-head (terms/make-compound 'mortal (terms/make-variable "X"))
          rule-body [(terms/make-compound 'human (terms/make-variable "X"))]
          var (terms/make-variable "Y")
          query (terms/make-compound 'mortal var)]
      
      (knowledge/add-fact! fact)
      (knowledge/add-rule! rule-head rule-body)
      
      (let [solutions (solver/solve-goal query)
            solution (first solutions)]
        (is (= 1 (count solutions)))
        (is (= (terms/make-atom 'alice) (bindings/lookup-binding var solution)))))))

(deftest test-solve-multiple_solutions
  (testing "Множественные решения"
    (reset-tipster-for-test!)
    
    (let [fact1 (terms/make-compound 'human 'alice)
          fact2 (terms/make-compound 'human 'bob)
          var (terms/make-variable "X")
          query (terms/make-compound 'human var)]
      
      (knowledge/add-fact! fact1)
      (knowledge/add-fact! fact2)
      
      (let [solutions (solver/solve-goal query)]
        (is (= 2 (count solutions)))
        (let [bound-values (set (map #(bindings/lookup-binding var %) solutions))]
          (is (contains? bound-values (terms/make-atom 'alice)))
          (is (contains? bound-values (terms/make-atom 'bob))))))))

;; === ТЕСТЫ ИНТЕГРАЦИИ С CLOJURE ===

(deftest test-clojure-to-tipster-conversion
  (testing "Преобразование Clojure-данных в термы Tipster"
    (let [atom-term (terms/clojure-term->tipster-term 'hello)
          var-term (terms/clojure-term->tipster-term '?X)
          list-term (terms/clojure-term->tipster-term '(f a b))
          vector-term (terms/clojure-term->tipster-term '[a b c])]
      
      (is (= :atom (terms/term-type atom-term)))
      (is (= 'hello (terms/term-value atom-term)))
      
      (is (= :variable (terms/term-type var-term)))
      (is (= "X" (:name var-term)))
      
      (is (= :compound (terms/term-type list-term)))
      (is (= 'f (:functor list-term)))
      (is (= 2 (count (:args list-term))))
      
      (is (= :compound (terms/term-type vector-term)))
      (is (= 'vector (:functor vector-term))))))

(deftest test-tipster-to-clojure-conversion
  (testing "Преобразование термов Tipster в Clojure-данные"
    (let [atom-term (terms/make-atom 'hello)
          var-term (terms/make-variable "X")
          compound-term (terms/make-compound 'f 'a 'b)
          bindings (bindings/empty-bindings)]
      
      (is (= 'hello (terms/tipster-term->clojure-term atom-term bindings bindings/deref-term)))
      (is (= '?X (terms/tipster-term->clojure-term var-term bindings bindings/deref-term)))
      (is (= '(f a b) (terms/tipster-term->clojure-term compound-term bindings bindings/deref-term))))))

;; === ТЕСТЫ МАКРОСОВ ===

(deftest test-deffact-macro
  (testing "Макрос deffact"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    
    (let [kb @knowledge/knowledge-base
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
      
      (is (= 2 (count grandfathers))) ; боб->дэвид, чарли->? (нет внуков у чарли)
      (is (= 1 (count grandmothers))) ; алиса->чарли
      )))

(deftest test-performance-with-many-facts
  (testing "Производительность с большим количеством фактов"
    (reset-tipster-for-test!)
    
    ;; Добавляем много фактов
    (doseq [i (range 100)]
      (knowledge/add-fact! (terms/make-compound 'number i)))
    
    ;; Запрос должен найти все факты
    (let [var (terms/make-variable "X")
          query (terms/make-compound 'number var)
          solutions (solver/solve-goal query)]
      
      (is (= 100 (count solutions))))))

(defn run-tipster-tests []
  "Запуск всех тестов Tipster"
  (println "🔄 Запуск основных интеграционных тестов...")
  (run-tests 'tipster.core-test))

(defn run-all-modular-tests []
  "Запуск всех модульных тестов"
  (all-tests/run-all-tipster-tests)) 
