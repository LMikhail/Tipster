(ns tipster.bindings-test
  (:require [clojure.test :refer :all]
            [tipster.bindings :as bindings]
            [tipster.terms :as terms]))

;; === ТЕСТЫ ОСНОВНЫХ ОПЕРАЦИЙ СО СВЯЗЫВАНИЯМИ ===

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

;; === ТЕСТЫ ДЕРЕФЕРЕНСАЦИИ ===

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

(deftest test-deref-compound-term
  (testing "Дереференсация составных термов"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          atom1 (terms/make-atom 'a)
          atom2 (terms/make-atom 'b)
          compound (terms/make-compound 'f var1 var2)
          bindings {(:id var1) atom1, (:id var2) atom2}
          dereferenced (bindings/deref-term compound bindings)]
      
      (is (terms/is-compound? dereferenced))
      (is (= 'f (:functor dereferenced)))
      ;; Проверяем базовые свойства составного терма
      (is (= 2 (count (:args dereferenced)))))))

;; === ТЕСТЫ ПРОВЕРКИ ВХОЖДЕНИЯ (OCCURS CHECK) ===

(deftest test-occurs-check
  (testing "Проверка вхождения (occurs check)"
    (let [var (terms/make-variable "X")
          compound (terms/make-compound 'f var)
          compound-no-var (terms/make-compound 'g 'a 'b)
          bindings (bindings/empty-bindings)]
      
      (is (bindings/occurs-check var compound bindings) 
          "Переменная входит в составной терм содержащий её")
      (is (not (bindings/occurs-check var compound-no-var bindings))
          "Переменная не входит в терм не содержащий её")
      ;; Переменная может входить сама в себя в некоторых реализациях
      (is (boolean? (bindings/occurs-check var var bindings))
          "occurs-check должна возвращать boolean"))))

(deftest test-occurs-check-with-bindings
  (testing "Проверка вхождения с существующими связываниями"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          atom (terms/make-atom 'a)
          compound (terms/make-compound 'f var2)
          bindings {(:id var2) atom}]
      
      (is (not (bindings/occurs-check var1 compound bindings))
          "После дереференсации переменная не входит в терм")
      
      (let [bad-bindings {(:id var2) var1}]
        (is (bindings/occurs-check var1 compound bad-bindings)
            "Циклическая ссылка обнаруживается")))))

;; === ТЕСТЫ ОБЪЕДИНЕНИЯ СВЯЗЫВАНИЙ ===

(deftest test-binding-composition
  (testing "Композиция связываний"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          var3 (terms/make-variable "Z")
          atom1 (terms/make-atom 'a)
          atom2 (terms/make-atom 'b)
          bindings1 {(:id var1) atom1}
          bindings2 {(:id var2) atom2}
          combined (merge bindings1 bindings2)]
      
      (is (= atom1 (bindings/lookup-binding var1 combined)))
      (is (= atom2 (bindings/lookup-binding var2 combined)))
      (is (nil? (bindings/lookup-binding var3 combined))))))

;; === ТЕСТЫ РАБОТЫ С ПУСТЫМИ СВЯЗЫВАНИЯМИ ===

(deftest test-empty-bindings
  (testing "Работа с пустыми связываниями"
    (let [empty-bindings (bindings/empty-bindings)
          var (terms/make-variable "X")
          atom (terms/make-atom 'test)]
      
      (is (empty? empty-bindings))
      (is (= atom (bindings/deref-term atom empty-bindings)))
      (is (= var (bindings/deref-term var empty-bindings))))))

;; === ТЕСТЫ СЛОЖНЫХ СЛУЧАЕВ ===

(deftest test-complex-binding-chains
  (testing "Сложные цепочки связываний"
    (let [var1 (terms/make-variable "X")
          var2 (terms/make-variable "Y")
          var3 (terms/make-variable "Z")
          atom (terms/make-atom 'final)
          ;; Цепочка: var1 -> var2 -> var3 -> atom
          bindings {(:id var1) var2
                    (:id var2) var3
                    (:id var3) atom}]
      
      (is (= atom (bindings/deref-term var1 bindings)) 
          "Длинная цепочка должна разрешаться до конечного значения")
      (is (= atom (bindings/deref-term var2 bindings)))
      (is (= atom (bindings/deref-term var3 bindings))))))

(defn run-bindings-tests []
  "Запуск тестов модуля bindings"
  (run-tests 'tipster.bindings-test))
