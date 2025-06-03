(ns tipster.core-test
  (:require [clojure.test :refer :all]
            [tipster.core :as tipster]))

(defn reset-tipster-for-test! []
  "Сброс состояния Tipster перед каждым тестом"
  (tipster/reset-tipster!))

;; === ТЕСТЫ БАЗОВЫХ КОМПОНЕНТОВ ===

(deftest test-term-creation
  (testing "Создание различных типов термов"
    (let [var (tipster/make-variable "X")
          atom (tipster/make-atom 'hello)
          compound (tipster/make-compound 'f 'a 'b)]
      
      (is (tipster/is-variable? var) "Переменная должна быть переменной")
      (is (not (tipster/is-variable? atom)) "Атом не должен быть переменной")
      (is (not (tipster/is-variable? compound)) "Составной терм не должен быть переменной")
      
      (is (tipster/is-compound? compound) "Составной терм должен быть составным")
      (is (not (tipster/is-compound? var)) "Переменная не должна быть составной")
      (is (not (tipster/is-compound? atom)) "Атом не должен быть составным")
      
      (is (= :variable (tipster/term-type var)))
      (is (= :atom (tipster/term-type atom)))
      (is (= :compound (tipster/term-type compound)))
      
      (is (= 'hello (tipster/term-value atom)))
      (is (= compound (tipster/term-value compound))))))

(deftest test-bindings-operations
  (testing "Операции со связываниями переменных"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          atom (tipster/make-atom 'test)
          empty-bindings (tipster/empty-bindings)
          bindings-with-var1 (tipster/bind-variable var1 atom empty-bindings)]
      
      (is (= {} empty-bindings))
      (is (nil? (tipster/lookup-binding var1 empty-bindings)))
      (is (= atom (tipster/lookup-binding var1 bindings-with-var1)))
      (is (nil? (tipster/lookup-binding var2 bindings-with-var1))))))

(deftest test-deref-term
  (testing "Дереференсация термов"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          atom (tipster/make-atom 'test)
          empty-bindings (tipster/empty-bindings)
          bindings {(:id var1) atom, (:id var2) var1}]
      
      ;; Несвязанная переменная
      (is (= var1 (tipster/deref-term var1 empty-bindings)))
      
      ;; Переменная связанная с атомом
      (is (= atom (tipster/deref-term var1 bindings)))
      
      ;; Цепочка переменных
      (is (= atom (tipster/deref-term var2 bindings)))
      
      ;; Атом остается атомом
      (is (= atom (tipster/deref-term atom bindings))))))

;; === ТЕСТЫ УНИФИКАЦИИ ===

(deftest test-unify-identical-terms
  (testing "Унификация идентичных термов"
    (let [atom1 (tipster/make-atom 'test)
          atom2 (tipster/make-atom 'test)
          compound1 (tipster/make-compound 'f 'a 'b)
          compound2 (tipster/make-compound 'f 'a 'b)]
      
      (is (= (tipster/empty-bindings) (tipster/unify atom1 atom2)))
      (is (= (tipster/empty-bindings) (tipster/unify compound1 compound2))))))

(deftest test-unify-different-atoms
  (testing "Неудачная унификация различных атомов"
    (let [atom1 (tipster/make-atom 'test1)
          atom2 (tipster/make-atom 'test2)]
      
      (is (nil? (tipster/unify atom1 atom2))))))

(deftest test-unify-variable-with-atom
  (testing "Унификация переменной с атомом"
    (let [var (tipster/make-variable "X")
          atom (tipster/make-atom 'test)
          result (tipster/unify var atom)]
      
      (is (not (nil? result)))
      (is (= atom (tipster/lookup-binding var result))))))

(deftest test-unify-variable-with-variable
  (testing "Унификация переменной с переменной"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          result (tipster/unify var1 var2)]
      
      (is (not (nil? result)))
      (is (or (= var2 (tipster/lookup-binding var1 result))
              (= var1 (tipster/lookup-binding var2 result)))))))

(deftest test-unify-compound-terms
  (testing "Унификация составных термов"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          compound1 (tipster/make-compound 'f var1 'b)
          compound2 (tipster/make-compound 'f 'a var2)
          result (tipster/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (tipster/make-atom 'a) (tipster/lookup-binding var1 result)))
      (is (= (tipster/make-atom 'b) (tipster/lookup-binding var2 result))))))

(deftest test-unify-different-functors
  (testing "Неудачная унификация термов с различными функторами"
    (let [compound1 (tipster/make-compound 'f 'a)
          compound2 (tipster/make-compound 'g 'a)]
      
      (is (nil? (tipster/unify compound1 compound2))))))

(deftest test-unify-different-arity
  (testing "Неудачная унификация термов с различной арностью"
    (let [compound1 (tipster/make-compound 'f 'a 'b)
          compound2 (tipster/make-compound 'f 'a)]
      
      (is (nil? (tipster/unify compound1 compound2))))))

(deftest test-occurs-check
  (testing "Проверка вхождения (occurs check)"
    (let [var (tipster/make-variable "X")
          compound (tipster/make-compound 'f var)
          bindings (tipster/empty-bindings)]
      
      (is (tipster/occurs-check var compound bindings))
      (is (nil? (tipster/unify var compound))))))

;; === ТЕСТЫ БАЗЫ ЗНАНИЙ ===

(deftest test-knowledge-base-operations
  (testing "Операции с базой знаний"
    (reset-tipster-for-test!)
    
    (let [fact1 (tipster/make-compound 'human 'alice)
          fact2 (tipster/make-compound 'human 'bob)
          rule-head (tipster/make-compound 'mortal (tipster/make-variable "X"))
          rule-body [(tipster/make-compound 'human (tipster/make-variable "X"))]]
      
      ;; Добавление фактов
      (tipster/add-fact! fact1)
      (tipster/add-fact! fact2)
      
      ;; Добавление правила
      (tipster/add-rule! rule-head rule-body)
      
      ;; Проверка содержимого базы знаний
      (let [kb @tipster/knowledge-base]
        (is (= 2 (count (:facts kb))))
        (is (= 1 (count (:rules kb))))
        (is (contains? (:facts kb) fact1))
        (is (contains? (:facts kb) fact2))))))

(deftest test-clear-knowledge-base
  (testing "Очистка базы знаний"
    (reset-tipster-for-test!)
    
    (tipster/add-fact! (tipster/make-compound 'test 'value))
    (tipster/add-rule! (tipster/make-compound 'head 'x) [(tipster/make-compound 'body 'x)])
    
    (is (> (count (:facts @tipster/knowledge-base)) 0))
    (is (> (count (:rules @tipster/knowledge-base)) 0))
    
    (tipster/clear-knowledge-base!)
    
    (is (= 0 (count (:facts @tipster/knowledge-base))))
    (is (= 0 (count (:rules @tipster/knowledge-base))))))

;; === ТЕСТЫ ЛОГИЧЕСКОГО СОЛВЕРА ===

(deftest test-solve-simple-fact
  (testing "Решение простого факта"
    (reset-tipster-for-test!)
    
    (let [fact (tipster/make-compound 'human 'alice)
          query (tipster/make-compound 'human 'alice)]
      
      (tipster/add-fact! fact)
      (let [solutions (tipster/solve-goal query)]
        (is (= 1 (count solutions)))
        (is (= (tipster/empty-bindings) (first solutions)))))))

(deftest test-solve-fact-with-variable
  (testing "Решение факта с переменной"
    (reset-tipster-for-test!)
    
    (let [fact (tipster/make-compound 'human 'alice)
          var (tipster/make-variable "X")
          query (tipster/make-compound 'human var)]
      
      (tipster/add-fact! fact)
      (let [solutions (tipster/solve-goal query)
            solution (first solutions)]
        (is (= 1 (count solutions)))
        (is (= (tipster/make-atom 'alice) (tipster/lookup-binding var solution)))))))

(deftest test-solve-rule
  (testing "Решение через правило"
    (reset-tipster-for-test!)
    
    (let [fact (tipster/make-compound 'human 'alice)
          rule-head (tipster/make-compound 'mortal (tipster/make-variable "X"))
          rule-body [(tipster/make-compound 'human (tipster/make-variable "X"))]
          var (tipster/make-variable "Y")
          query (tipster/make-compound 'mortal var)]
      
      (tipster/add-fact! fact)
      (tipster/add-rule! rule-head rule-body)
      
      (let [solutions (tipster/solve-goal query)
            solution (first solutions)]
        (is (= 1 (count solutions)))
        (is (= (tipster/make-atom 'alice) (tipster/lookup-binding var solution)))))))

(deftest test-solve-multiple_solutions
  (testing "Множественные решения"
    (reset-tipster-for-test!)
    
    (let [fact1 (tipster/make-compound 'human 'alice)
          fact2 (tipster/make-compound 'human 'bob)
          var (tipster/make-variable "X")
          query (tipster/make-compound 'human var)]
      
      (tipster/add-fact! fact1)
      (tipster/add-fact! fact2)
      
      (let [solutions (tipster/solve-goal query)]
        (is (= 2 (count solutions)))
        (let [bound-values (set (map #(tipster/lookup-binding var %) solutions))]
          (is (contains? bound-values (tipster/make-atom 'alice)))
          (is (contains? bound-values (tipster/make-atom 'bob))))))))

;; === ТЕСТЫ ИНТЕГРАЦИИ С CLOJURE ===

(deftest test-clojure-to-tipster-conversion
  (testing "Преобразование Clojure-данных в термы Tipster"
    (let [atom-term (tipster/clojure-term->tipster-term 'hello)
          var-term (tipster/clojure-term->tipster-term '?X)
          list-term (tipster/clojure-term->tipster-term '(f a b))
          vector-term (tipster/clojure-term->tipster-term '[a b c])]
      
      (is (= :atom (tipster/term-type atom-term)))
      (is (= 'hello (tipster/term-value atom-term)))
      
      (is (= :variable (tipster/term-type var-term)))
      (is (= "X" (:name var-term)))
      
      (is (= :compound (tipster/term-type list-term)))
      (is (= 'f (:functor list-term)))
      (is (= 2 (count (:args list-term))))
      
      (is (= :compound (tipster/term-type vector-term)))
      (is (= 'vector (:functor vector-term))))))

(deftest test-tipster-to-clojure-conversion
  (testing "Преобразование термов Tipster в Clojure-данные"
    (let [atom-term (tipster/make-atom 'hello)
          var-term (tipster/make-variable "X")
          compound-term (tipster/make-compound 'f 'a 'b)
          bindings (tipster/empty-bindings)]
      
      (is (= 'hello (tipster/tipster-term->clojure-term atom-term bindings)))
      (is (= '?X (tipster/tipster-term->clojure-term var-term bindings)))
      (is (= '(f a b) (tipster/tipster-term->clojure-term compound-term bindings))))))

;; === ТЕСТЫ МАКРОСОВ ===

(deftest test-deffact-macro
  (testing "Макрос deffact"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    
    (let [kb @tipster/knowledge-base
          facts (:facts kb)]
      (is (= 1 (count facts)))
      (let [fact (first facts)]
        (is (= :compound (tipster/term-type fact)))
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
      (tipster/add-fact! (tipster/make-compound 'number i)))
    
    ;; Запрос должен найти все факты
    (let [var (tipster/make-variable "X")
          query (tipster/make-compound 'number var)
          solutions (tipster/solve-goal query)]
      
      (is (= 100 (count solutions))))))

(defn run-tipster-tests []
  "Запуск всех тестов Tipster"
  (run-tests 'tipster.core-test)) 
