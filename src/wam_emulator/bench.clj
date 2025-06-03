(ns wam-emulator.bench
  (:require [criterium.core :as crit]
            [wam-emulator.core :as wam]))

(defn bench-put-constant []
  "Бенчмарк инструкции put_constant"
  (wam/reset-wam!)
  (crit/bench
   (wam/execute-instruction [:put_constant 'test-value 0])
   :verbose))

(defn bench-get-constant-success []
  "Бенчмарк успешной унификации констант"
  (wam/reset-wam!)
  (wam/set-X! 0 (wam/const-tag 'test-value))
  (reset! wam/P 0)
  (crit/bench
   (wam/execute-instruction [:get_constant 'test-value 0])
   :verbose))

(defn bench-heap-operations []
  "Бенчмарк операций с кучей"
  (wam/reset-wam!)
  (crit/bench
   (wam/heap-push! (wam/const-tag 'test-value))
   :verbose))

(defn bench-variable-creation []
  "Бенчмарк создания переменных"
  (wam/reset-wam!)
  (crit/bench
   (wam/execute-instruction [:get_variable 'X 0])
   :verbose))

(defn bench-simple-program []
  "Бенчмарк выполнения простой программы"
  (crit/bench
   (do
     (wam/reset-wam!)
     (reset! wam/program-code
             [[:put_constant 'a 0]
              [:get_constant 'a 0]
              [:proceed]])
     (wam/run-wam))
   :verbose))

(defn bench-backtracking-setup []
  "Бенчмарк создания точки выбора"
  (wam/reset-wam!)
  (crit/bench
   (wam/execute-instruction [:try_me_else 10])
   :verbose))

(defn run-benchmarks []
  "Запуск всех бенчмарков"
  (println "=== WAM Emulator Benchmarks ===\n")
  
  (println "1. put_constant instruction:")
  (bench-put-constant)
  
  (println "\n2. get_constant (success) instruction:")
  (bench-get-constant-success)
  
  (println "\n3. Heap operations:")
  (bench-heap-operations)
  
  (println "\n4. Variable creation:")
  (bench-variable-creation)
  
  (println "\n5. Simple program execution:")
  (bench-simple-program)
  
  (println "\n6. Backtracking setup:")
  (bench-backtracking-setup)
  
  (println "\n=== Benchmarks Complete ==="))

(defn quick-bench [f]
  "Быстрый бенчмарк для разработки"
  (crit/quick-bench f)) 
