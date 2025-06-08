(ns tipster.terms-test
  (:require [clojure.test :refer :all]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]))

;; === ТЕСТЫ СОЗДАНИЯ И ТИПИЗАЦИИ ТЕРМОВ ===

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

;; === ТЕСТЫ ПРЕОБРАЗОВАНИЯ ДАННЫХ ===

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

;; === ТЕСТЫ РАБОТЫ С ПЕРЕМЕННЫМИ ===

(deftest test-variable-operations
  (testing "Операции с переменными"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          var3 (terms/make-variable "X")] ; другая переменная с тем же именем
      
      (is (not= var1 var3) "Переменные с одинаковыми именами должны быть разными объектами")
      (is (= "X" (:name var1)) "Имя переменной должно сохраняться")
      (is (symbol? (:id var1)) "У переменной должен быть уникальный ID"))))

;; === ТЕСТЫ СОСТАВНЫХ ТЕРМОВ ===

(deftest test-compound-term-operations
  (testing "Операции с составными термами"
    (let [simple-compound (terms/make-compound 'f 'a)
          complex-compound (terms/make-compound 'g 'a 'b 'c)
          nested-compound (terms/make-compound 'h (terms/make-compound 'f 'a) 'b)]
      
      (is (= 'f (:functor simple-compound)))
      (is (= 1 (count (:args simple-compound))))
      (is (= 3 (count (:args complex-compound))))
      
      (is (terms/is-compound? (first (:args nested-compound))) 
          "Аргумент может быть составным термом"))))

;; === ТЕСТЫ ПРОВЕРКИ СТРУКТУРЫ ===

(deftest test-term-structure-validation
  (testing "Проверка структуры термов"
    (let [var (terms/make-variable "X")
          atom (terms/make-atom 'test)
          compound (terms/make-compound 'pred atom var)]
      
      ;; Проверяем что терм валиден
      (is (or (terms/is-variable? var) 
              (terms/is-compound? var) 
              (= :atom (terms/term-type var))))
      
      ;; Проверяем структуру составного терма
      (is (= 'pred (:functor compound)))
      (is (= 2 (count (:args compound))))
      (is (= atom (first (:args compound))))
      (is (= var (second (:args compound)))))))

(defn run-terms-tests []
  "Запуск тестов модуля terms"
  (run-tests 'tipster.terms-test))
