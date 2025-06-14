(ns bench
  (:require [criterium.core :as criterium]
            [tipster.core :as tipster]
            [tipster.terms :as terms]
            [tipster.bindings :as bindings]
            [tipster.unification :as unif]
            [tipster.knowledge :as knowledge]
            [tipster.solver :as solver]))

(defn benchmark-unification []
  "Бенчмарк унификации термов"
  (println "\n=== Benchmarking Unification ===")
  
  (let [var-x (terms/make-variable "X")
        var-y (terms/make-variable "Y")
        atom-a (terms/make-atom 'a)
        compound1 (terms/make-compound 'f var-x 'b)
        compound2 (terms/make-compound 'f 'a var-y)]
    
    (println "\nVariable-Atom unification:")
    (criterium/quick-bench (unif/unify var-x atom-a))
    
    (println "\nCompound terms unification:")
    (criterium/quick-bench (unif/unify compound1 compound2))))

(defn benchmark-fact-solving []
  "Бенчмарк решения фактов"
  (println "\n=== Benchmarking Fact Solving ===")
  
  (tipster/reset-tipster!)
  
  ;; Добавляем факты
  (doseq [i (range 100)]
    (knowledge/add-fact! (terms/make-compound 'number i)))
  
  (let [var (terms/make-variable "X")
        query (terms/make-compound 'number var)]
    
    (println "\nSolving query with 100 facts:")
    (criterium/quick-bench (doall (solver/solve-goal query)))))

(defn benchmark-rule-solving []
  "Бенчмарк решения правил"
  (println "\n=== Benchmarking Rule Solving ===")
  
  (tipster/reset-tipster!)
  
  ;; Семейные отношения
  (knowledge/add-fact! (terms/make-compound 'родитель 'алиса 'боб))
  (knowledge/add-fact! (terms/make-compound 'родитель 'боб 'чарли))
  (knowledge/add-fact! (terms/make-compound 'родитель 'чарли 'дэвид))
  
  (knowledge/add-rule! 
    (terms/make-compound 'дедушка (terms/make-variable "X") (terms/make-variable "Z"))
    [(terms/make-compound 'родитель (terms/make-variable "X") (terms/make-variable "Y"))
     (terms/make-compound 'родитель (terms/make-variable "Y") (terms/make-variable "Z"))])
  
  (let [var-x (terms/make-variable "X")
        var-z (terms/make-variable "Z")
        query (terms/make-compound 'дедушка var-x var-z)]
    
    (println "\nSolving rule-based query:")
    (criterium/quick-bench (doall (solver/solve-goal query)))))

(defn benchmark-complex-terms []
  "Бенчмарк сложных структур"
  (println "\n=== Benchmarking Complex Terms ===")
  
  (let [deep-term (reduce (fn [acc i]
                            (terms/make-compound 'f acc i))
                          (terms/make-atom 'base)
                          (range 10))
        var (terms/make-variable "X")]
    
    (println "\nCreating deep compound terms:")
    (criterium/quick-bench 
      (reduce (fn [acc i]
                (terms/make-compound 'f acc i))
              (terms/make-atom 'base)
              (range 10)))
    
    (println "\nUnifying with deep compound terms:")
    (criterium/quick-bench (unif/unify deep-term var))))

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
