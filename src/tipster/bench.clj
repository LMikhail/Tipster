(ns tipster.bench
  (:require [criterium.core :as criterium]
            [tipster.core :as tipster]))

(defn benchmark-unification []
  "Бенчмарк унификации термов"
  (println "\n=== Benchmarking Unification ===")
  
  (let [var-x (tipster/make-variable "X")
        var-y (tipster/make-variable "Y")
        atom-a (tipster/make-atom 'a)
        compound1 (tipster/make-compound 'f var-x 'b)
        compound2 (tipster/make-compound 'f 'a var-y)]
    
    (println "\nVariable-Atom unification:")
    (criterium/quick-bench (tipster/unify var-x atom-a))
    
    (println "\nCompound terms unification:")
    (criterium/quick-bench (tipster/unify compound1 compound2))))

(defn benchmark-fact-solving []
  "Бенчмарк решения фактов"
  (println "\n=== Benchmarking Fact Solving ===")
  
  (tipster/reset-tipster!)
  
  ;; Добавляем факты
  (doseq [i (range 100)]
    (tipster/add-fact! (tipster/make-compound 'number i)))
  
  (let [var (tipster/make-variable "X")
        query (tipster/make-compound 'number var)]
    
    (println "\nSolving query with 100 facts:")
    (criterium/quick-bench (doall (tipster/solve-goal query)))))

(defn benchmark-rule-solving []
  "Бенчмарк решения правил"
  (println "\n=== Benchmarking Rule Solving ===")
  
  (tipster/reset-tipster!)
  
  ;; Семейные отношения
  (tipster/add-fact! (tipster/make-compound 'родитель 'алиса 'боб))
  (tipster/add-fact! (tipster/make-compound 'родитель 'боб 'чарли))
  (tipster/add-fact! (tipster/make-compound 'родитель 'чарли 'дэвид))
  
  (tipster/add-rule! 
    (tipster/make-compound 'дедушка (tipster/make-variable "X") (tipster/make-variable "Z"))
    [(tipster/make-compound 'родитель (tipster/make-variable "X") (tipster/make-variable "Y"))
     (tipster/make-compound 'родитель (tipster/make-variable "Y") (tipster/make-variable "Z"))])
  
  (let [var-x (tipster/make-variable "X")
        var-z (tipster/make-variable "Z")
        query (tipster/make-compound 'дедушка var-x var-z)]
    
    (println "\nSolving rule-based query:")
    (criterium/quick-bench (doall (tipster/solve-goal query)))))

(defn benchmark-complex-terms []
  "Бенчмарк сложных структур"
  (println "\n=== Benchmarking Complex Terms ===")
  
  (let [deep-term (reduce (fn [acc i]
                            (tipster/make-compound 'f acc i))
                          (tipster/make-atom 'base)
                          (range 10))
        var (tipster/make-variable "X")]
    
    (println "\nCreating deep compound terms:")
    (criterium/quick-bench 
      (reduce (fn [acc i]
                (tipster/make-compound 'f acc i))
              (tipster/make-atom 'base)
              (range 10)))
    
    (println "\nUnifying with deep compound terms:")
    (criterium/quick-bench (tipster/unify deep-term var))))

(defn benchmark-macro-performance []
  "Бенчмарк производительности макросов"
  (println "\n=== Benchmarking Macro Performance ===")
  
  (tipster/reset-tipster!)
  
  (println "\nMacro deffact performance:")
  (criterium/quick-bench 
    (tipster/deffact (test-fact sample-value)))
  
  (tipster/deffact (human alice))
  (tipster/deffact (human bob))
  
  (println "\nMacro query performance:")
  (criterium/quick-bench 
    (tipster/query (human ?X))))

(defn run-benchmarks 
  "Запуск всех бенчмарков"
  [_]
  (println "🚀 Starting Tipster Logic Engine Benchmarks")
  (println "=" * 50)
  
  (benchmark-unification)
  (benchmark-fact-solving)
  (benchmark-rule-solving)
  (benchmark-complex-terms)
  (benchmark-macro-performance)
  
  (println "\n" "=" * 50)
  (println "✅ All benchmarks completed!")
  (println "\nInterpretation:")
  (println "- Lower execution time = better performance")
  (println "- Watch for memory allocations in complex operations")
  (println "- Unification should be sub-millisecond for simple terms")
  (println "- Rule solving time grows with knowledge base size"))

(defn -main []
  "Точка входа для запуска бенчмарков"
  (run-benchmarks {}))
