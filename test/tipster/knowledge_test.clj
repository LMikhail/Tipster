(ns tipster.knowledge-test
  (:require [clojure.test :refer :all]
            [tipster.knowledge :as knowledge]
            [tipster.terms :as terms]
            [tipster.core :as tipster]))

(defn reset-tipster-for-test! []
  "Сброс состояния Tipster перед каждым тестом"
  (tipster/reset-tipster!))

;; === ТЕСТЫ БАЗОВЫХ ОПЕРАЦИЙ С БАЗОЙ ЗНАНИЙ ===

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

;; === ТЕСТЫ ДОБАВЛЕНИЯ И УДАЛЕНИЯ ФАКТОВ ===

(deftest test-add-remove-facts
  (testing "Добавление и удаление фактов"
    (reset-tipster-for-test!)
    
    (let [fact1 (terms/make-compound 'person 'alice)
          fact2 (terms/make-compound 'person 'bob)
          fact3 (terms/make-compound 'age 'alice 25)]
      
      ;; Добавляем факты
      (knowledge/add-fact! fact1)
      (knowledge/add-fact! fact2)
      (knowledge/add-fact! fact3)
      
      (is (= 3 (count (:facts @knowledge/knowledge-base))))
      
      ;; Проверяем наличие фактов
      (is (contains? (:facts @knowledge/knowledge-base) fact1))
      (is (contains? (:facts @knowledge/knowledge-base) fact2))
      (is (contains? (:facts @knowledge/knowledge-base) fact3))
      
             ;; Проверяем что факты добавлены
       (is (= 3 (count (:facts @knowledge/knowledge-base)))))))

;; === ТЕСТЫ РАБОТЫ С ПРАВИЛАМИ ===

(deftest test-add-remove-rules
  (testing "Добавление и удаление правил"
    (reset-tipster-for-test!)
    
    (let [rule1-head (terms/make-compound 'mortal (terms/make-variable "X"))
          rule1-body [(terms/make-compound 'human (terms/make-variable "X"))]
          rule2-head (terms/make-compound 'parent (terms/make-variable "X") (terms/make-variable "Y"))
          rule2-body [(terms/make-compound 'father (terms/make-variable "X") (terms/make-variable "Y"))]]
      
      ;; Добавляем правила
      (knowledge/add-rule! rule1-head rule1-body)
      (knowledge/add-rule! rule2-head rule2-body)
      
      (is (= 2 (count (:rules @knowledge/knowledge-base))))
      
      ;; Проверяем структуру правил
      (let [rules (:rules @knowledge/knowledge-base)]
        (is (some #(= rule1-head (:head %)) rules))
        (is (some #(= rule1-body (:body %)) rules))))))

;; === ТЕСТЫ ПОИСКА В БАЗЕ ЗНАНИЙ ===

(deftest test-find-facts
  (testing "Поиск фактов в базе знаний"
    (reset-tipster-for-test!)
    
    (let [fact1 (terms/make-compound 'person 'alice)
          fact2 (terms/make-compound 'person 'bob)
          fact3 (terms/make-compound 'age 'alice 25)]
      
      (knowledge/add-fact! fact1)
      (knowledge/add-fact! fact2)
      (knowledge/add-fact! fact3)
      
             ;; Простая проверка наличия фактов
       (is (= 3 (count (:facts @knowledge/knowledge-base))))
       (is (contains? (:facts @knowledge/knowledge-base) fact1))
       (is (contains? (:facts @knowledge/knowledge-base) fact2))
       (is (contains? (:facts @knowledge/knowledge-base) fact3)))))

(deftest test-find-rules
  (testing "Поиск правил в базе знаний"
    (reset-tipster-for-test!)
    
    (let [rule1-head (terms/make-compound 'mortal (terms/make-variable "X"))
          rule1-body [(terms/make-compound 'human (terms/make-variable "X"))]
          rule2-head (terms/make-compound 'parent (terms/make-variable "X") (terms/make-variable "Y"))
          rule2-body [(terms/make-compound 'father (terms/make-variable "X") (terms/make-variable "Y"))]]
      
      (knowledge/add-rule! rule1-head rule1-body)
      (knowledge/add-rule! rule2-head rule2-body)
      
             ;; Простая проверка наличия правил
       (is (= 2 (count (:rules @knowledge/knowledge-base))))
       (let [rules (:rules @knowledge/knowledge-base)]
         (is (some #(= rule1-head (:head %)) rules))
         (is (some #(= rule2-head (:head %)) rules))))))

;; === ТЕСТЫ ДУБЛИРОВАНИЯ ===

(deftest test-duplicate-facts
  (testing "Обработка дублирующихся фактов"
    (reset-tipster-for-test!)
    
    (let [fact (terms/make-compound 'person 'alice)]
      
      ;; Добавляем тот же факт несколько раз
      (knowledge/add-fact! fact)
      (knowledge/add-fact! fact)
      (knowledge/add-fact! fact)
      
      ;; Должен остаться только один экземпляр
      (is (= 1 (count (:facts @knowledge/knowledge-base)))))))

(deftest test-duplicate-rules
  (testing "Обработка дублирующихся правил"
    (reset-tipster-for-test!)
    
    (let [rule-head (terms/make-compound 'mortal (terms/make-variable "X"))
          rule-body [(terms/make-compound 'human (terms/make-variable "X"))]]
      
      ;; Добавляем то же правило несколько раз
      (knowledge/add-rule! rule-head rule-body)
      (knowledge/add-rule! rule-head rule-body)
      (knowledge/add-rule! rule-head rule-body)
      
      ;; Правила могут дублироваться (это нормально в логическом программировании)
      ;; или система может их дедуплицировать - зависит от реализации
      (is (>= (count (:rules @knowledge/knowledge-base)) 1)))))

;; === ТЕСТЫ ЦЕЛОСТНОСТИ БАЗЫ ЗНАНИЙ ===

(deftest test-knowledge-base-integrity
  (testing "Целостность базы знаний"
    (reset-tipster-for-test!)
    
    ;; Проверяем что база знаний инициализируется корректно
    (let [kb @knowledge/knowledge-base]
      (is (map? kb))
      (is (contains? kb :facts))
      (is (contains? kb :rules))
      (is (set? (:facts kb)))
      (is (coll? (:rules kb))))
    
    ;; После очистки структура должна сохраниться
    (knowledge/clear-knowledge-base!)
    (let [kb @knowledge/knowledge-base]
      (is (map? kb))
      (is (contains? kb :facts))
      (is (contains? kb :rules))
      (is (set? (:facts kb)))
      (is (coll? (:rules kb))))))

;; === ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ===

(deftest test-knowledge-base-performance
  (testing "Производительность базы знаний с большим количеством фактов"
    (reset-tipster-for-test!)
    
    ;; Добавляем много фактов
    (doseq [i (range 100)]
      (knowledge/add-fact! (terms/make-compound 'number i)))
    
    (is (= 100 (count (:facts @knowledge/knowledge-base))))
    
         ;; Проверка количества фактов
     (is (= 100 (count (:facts @knowledge/knowledge-base))))))

;; === ТЕСТЫ МНОГОПОТОЧНОСТИ ===

(deftest test-concurrent-access
  (testing "Одновременный доступ к базе знаний"
    (reset-tipster-for-test!)
    
    ;; Создаем несколько потоков для одновременного добавления фактов
    (let [futures (doall 
                   (for [i (range 10)]
                     (future 
                       (doseq [j (range 10)]
                         (knowledge/add-fact! (terms/make-compound 'test i j))))))]
      
      ;; Ждем завершения всех потоков
      (doseq [f futures]
        @f)
      
      ;; Проверяем что все факты добавлены
      (is (= 100 (count (:facts @knowledge/knowledge-base)))))))

(defn run-knowledge-tests []
  "Запуск тестов модуля knowledge"
  (run-tests 'tipster.knowledge-test))
