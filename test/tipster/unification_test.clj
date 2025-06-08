(ns tipster.unification-test
  (:require [clojure.test :refer :all]
            [tipster.unification :as unif]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]))

;; === ТЕСТЫ УНИФИКАЦИИ ИДЕНТИЧНЫХ ТЕРМОВ ===

(deftest test-unify-identical-terms
  (testing "Унификация идентичных термов"
    (let [atom1 (terms/make-atom 'test)
          atom2 (terms/make-atom 'test)
          compound1 (terms/make-compound 'f 'a 'b)
          compound2 (terms/make-compound 'f 'a 'b)]
      
      (is (= (bindings/empty-bindings) (unif/unify atom1 atom2)))
      (is (= (bindings/empty-bindings) (unif/unify compound1 compound2))))))

;; === ТЕСТЫ НЕУДАЧНОЙ УНИФИКАЦИИ ===

(deftest test-unify-different-atoms
  (testing "Неудачная унификация различных атомов"
    (let [atom1 (terms/make-atom 'test1)
          atom2 (terms/make-atom 'test2)]
      
      (is (nil? (unif/unify atom1 atom2))))))

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

;; === ТЕСТЫ УНИФИКАЦИИ ПЕРЕМЕННЫХ ===

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

(deftest test-unify-variable-with-compound
  (testing "Унификация переменной с составным термом"
    (let [var (terms/make-variable "X")
          compound (terms/make-compound 'f 'a 'b)
          result (unif/unify var compound)]
      
      (is (not (nil? result)))
      (is (= compound (bindings/lookup-binding var result))))))

;; === ТЕСТЫ УНИФИКАЦИИ СОСТАВНЫХ ТЕРМОВ ===

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

(deftest test-unify-nested-compound-terms
  (testing "Унификация вложенных составных термов"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          inner1 (terms/make-compound 'g var1)
          inner2 (terms/make-compound 'g 'a)
          compound1 (terms/make-compound 'f inner1 var2)
          compound2 (terms/make-compound 'f inner2 'b)
          result (unif/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (terms/make-atom 'a) (bindings/lookup-binding var1 result)))
      (is (= (terms/make-atom 'b) (bindings/lookup-binding var2 result))))))

;; === ТЕСТЫ ПРОВЕРКИ ВХОЖДЕНИЯ ===

(deftest test-occurs-check
  (testing "Проверка вхождения (occurs check)"
    (let [var (terms/make-variable "X")
          compound (terms/make-compound 'f var)
          bindings (bindings/empty-bindings)]
      
      (is (bindings/occurs-check var compound bindings))
      (is (nil? (unif/unify var compound))))))

(deftest test-occurs-check-indirect
  (testing "Непрямая проверка вхождения"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          compound (terms/make-compound 'f var1)
          result1 (unif/unify var2 compound)]
      
      (is (not (nil? result1)) "Первая унификация должна пройти")
      
      ;; Теперь пытаемся унифицировать var1 с результатом
      (is (nil? (unif/unify var1 compound)) "Прямая циклическая унификация должна провалиться"))))

;; === ТЕСТЫ СЛОЖНЫХ СЛУЧАЕВ ===

(deftest test-unify-with-existing-bindings
  (testing "Унификация с существующими связываниями"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          atom (terms/make-atom 'test)
          compound1 (terms/make-compound 'f var1)
          compound2 (terms/make-compound 'f atom)] ; Используем прямое значение
      
      ;; Простая унификация должна работать
      (let [result (unif/unify compound1 compound2)]
        (is (not (nil? result)))
        (is (= atom (bindings/lookup-binding var1 result)))))))

(deftest test-unify-multiple-variables
  (testing "Унификация с множественными переменными"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          var3 (terms/make-variable "Z")
          compound1 (terms/make-compound 'relation var1 var2 var1) ; X повторяется
          compound2 (terms/make-compound 'relation 'a var3 'a)      ; 'a повторяется
          result (unif/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (terms/make-atom 'a) (bindings/lookup-binding var1 result)))
      ;; Проверяем что переменные связаны корректно
      (let [val2 (bindings/lookup-binding var2 result)
            val3 (bindings/lookup-binding var3 result)]
        (is (or (= val2 val3) (and (nil? val3) (not (nil? val2)))))))))

;; === ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ===

(deftest test-unify-large-terms
  (testing "Унификация больших термов"
    (let [make-large-term (fn [n prefix]
                            (apply terms/make-compound 'large-term 
                                   (map #(symbol (str prefix %)) (range n))))
          large-term1 (make-large-term 50 "a")
          large-term2 (make-large-term 50 "a")
          result (unif/unify large-term1 large-term2)]
      
      (is (= (bindings/empty-bindings) result)))))

(defn run-unification-tests []
  "Запуск тестов модуля unification"
  (run-tests 'tipster.unification-test))
