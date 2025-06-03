(ns wam-emulator.core-test
  (:require [clojure.test :refer :all]
            [wam-emulator.core :as wam]))

(defn reset-wam-for-test! []
  "Сброс состояния WAM перед каждым тестом"
  (wam/reset-wam!))

(deftest test-wam-state-reset
  (testing "WAM state can be reset properly"
    (reset-wam-for-test!)
    (is (= 0 @wam/P))
    (is (= -1 @wam/B))
    (is (= 0 @wam/H) "H should be 0 for empty heap (next free slot)")
    (is (= (vec (repeat 8 nil)) @wam/X))
    (is (= [] @wam/heap))
    (is (= [] @wam/stack))
    (is (= [] @wam/trail))
    (is (= [] @wam/program-code))))

(deftest test-put-constant-instruction
  (testing "put_constant instruction works correctly"
    (reset-wam-for-test!)
    (reset! wam/program-code [[:put_constant 'test-value 0]])
    (wam/execute-instruction [:put_constant 'test-value 0])
    
    (is (= {:tag :const :value 'test-value} (wam/get-X 0)))
    (is (= 1 @wam/P))))

(deftest test-get-constant-success
  (testing "get_constant succeeds with matching constant"
    (reset-wam-for-test!)
    
    ;; Сначала поместим константу в регистр
    (wam/set-X! 0 (wam/const-tag 'test-value))
    
    ;; Затем попытаемся унифицировать с той же константой
    (reset! wam/P 0)
    (wam/execute-instruction [:get_constant 'test-value 0])
    
    (is (= 1 @wam/P) "Program counter should advance on success")))

(deftest test-get-constant-with-variable
  (testing "get_constant binds variable to constant"
    (reset-wam-for-test!)
    
    ;; Создаем переменную в куче
    (let [var-heap-addr (count @wam/heap)
          _ (wam/heap-push! {:tag :var :addr var-heap-addr})
          _ (wam/set-X! 0 {:tag :var :addr var-heap-addr})]
      
      (reset! wam/P 0)
      (wam/execute-instruction [:get_constant 'bound-value 0])
      
      (is (= 1 @wam/P) "Program counter should advance")
      (is (= 2 (count @wam/heap)) "Heap should have var cell and const cell")
      
      (let [var-cell-on-heap (wam/heap-get var-heap-addr)
            const-val-on-heap (wam/heap-get (inc var-heap-addr))
            const-heap-addr (inc var-heap-addr)] 
        (is (= {:tag :ref :addr const-heap-addr} var-cell-on-heap)
            "Variable cell should now be a ref to the constant's heap addr")
        (is (= (wam/const-tag 'bound-value) const-val-on-heap)
            "Constant should be correctly stored on heap"))
      (is (= [var-heap-addr] @wam/trail) "Trail should record binding of var-heap-addr"))))

(deftest test-get-constant-failure
  (testing "get_constant fails with non-matching constant"
    (reset-wam-for-test!)
    
    ;; Поместим одну константу в регистр
    (wam/set-X! 0 (wam/const-tag 'value-a))
    
    ;; Попытаемся унифицировать с другой константой
    (reset! wam/P 0)
    
    (is (thrown? clojure.lang.ExceptionInfo
                 (wam/execute-instruction [:get_constant 'value-b 0]))
        "Should throw exception on failed unification")))

(deftest test-get-variable-instruction
  (testing "get_variable creates new variable on heap and sets X reg"
    (reset-wam-for-test!)
    (let [initial-heap-size (count @wam/heap)
          expected-var-addr initial-heap-size]
      (wam/execute-instruction [:get_variable 'UNUSED_VAR_NAME 0])
      
      (is (= 1 @wam/P) "PC should advance")
      (is (= (inc initial-heap-size) (count @wam/heap)) "Heap should grow by one cell")
      
      (let [x-reg-val (wam/get-X 0)
            heap-cell-val (wam/heap-get expected-var-addr)]
        (is (= {:tag :var :addr expected-var-addr} x-reg-val)
            "X register should contain var term pointing to heap cell")
        (is (= {:tag :var :addr expected-var-addr} heap-cell-val)
            "Heap cell should be a self-referencing var cell")))))

(deftest test-try-me-else-instruction
  (testing "try_me_else creates choice point"
    (reset-wam-for-test!)
    
    (wam/execute-instruction [:try_me_else 10])
    
    (is (= 1 @wam/P) "Program counter should advance")
    (is (= 1 (count @wam/stack)) "Stack should contain choice point")
    (is (= 0 @wam/B) "B should point to the choice point")
    
    (let [choice-point (first @wam/stack)]
      (is (vector? (:registers choice-point)) "Should save registers")
      (is (= 10 (:next_clause_P choice-point)) "Choice point should save next clause address"))))

(deftest test-proceed-instruction
  (testing "proceed instruction with continuation pointer"
    (reset-wam-for-test!)
    
    (reset! wam/CP 5)
    (wam/execute-instruction [:proceed])
    
    (is (= 5 @wam/P) "Program counter should be set to continuation pointer")))

(deftest test-proceed-instruction-end
  (testing "proceed instruction at end of program correctly terminates run-wam loop"
    (reset-wam-for-test!)
    
    (reset! wam/program-code [[:proceed]])
    (reset! wam/CP 0)
    (wam/run-wam)
    (is (= (count @wam/program-code) @wam/P) "P should be set to end of program by run-wam loop termination")))

(deftest test-simple-program-execution
  (testing "Complete execution of simple put/get program"
    (reset-wam-for-test!)
    
    ;; Программа: положить константу и проверить её
    (reset! wam/program-code
            [[:put_constant 'a 0]
             [:get_constant 'a 0]
             [:proceed]])
    
    (wam/run-wam)
    
    ;; После выполнения P должен быть больше начального значения
    ;; (точное значение зависит от логики proceed)
    (is (= (wam/const-tag 'a) (wam/get-X 0)) "X[0] should contain constant 'a")))

(deftest test-failed-program-execution
  (testing "Program execution with failed unification without choice point"
    (reset-wam-for-test!)
    
    ;; Программа: константа 'a, но проверка на 'b
    (reset! wam/program-code
            [[:put_constant 'a 0]
             [:get_constant 'b 0]
             [:proceed]])
    
    (is (thrown? clojure.lang.ExceptionInfo
                 (wam/run-wam))
        "Should throw exception on failed unification")))

(deftest test-call-instruction-known-predicate
  (testing "call instruction with known predicate"
    (reset-wam-for-test!)
    
    (reset! wam/P 5)
    (wam/execute-instruction [:call 'q-1 0])
    
    (is (= 6 @wam/CP) "CP should be P+1 before jump")
    (is (= 8 @wam/P) "P should jump to predicate address")))

(deftest test-call-instruction-unknown-predicate
  (testing "call instruction with unknown predicate"
    (reset-wam-for-test!)
    
    (is (thrown? clojure.lang.ExceptionInfo
                 (wam/execute-instruction [:call 'unknown-predicate 0]))
        "Should fail for unknown predicate")))

(deftest test-heap-operations
  (testing "Heap push and get operations with corrected H semantics"
    (reset-wam-for-test!)
    
    (is (= 0 @wam/H) "Initial H should be 0")
    (let [addr1 (wam/heap-push! (wam/const-tag 'value1))] 
      (is (= 0 addr1) "Addr of first pushed item should be 0")
      (is (= 1 @wam/H) "H should be 1 after one push")
      (is (= (wam/const-tag 'value1) (wam/heap-get addr1)))
      
      (let [addr2 (wam/heap-push! (wam/const-tag 'value2))] 
        (is (= 1 addr2) "Addr of second pushed item should be 1")
        (is (= 2 @wam/H) "H should be 2 after second push")
        (is (= (wam/const-tag 'value2) (wam/heap-get addr2)))))))

(deftest test-wam-deref-unbound-variable
  (testing "wam-deref on an unbound variable term"
    (reset-wam-for-test!)
    (let [var-heap-addr (count @wam/heap)
          _ (wam/heap-push! {:tag :var :addr var-heap-addr})
          var-term-in-reg {:tag :var :addr var-heap-addr}]
      (is (= var-term-in-reg (wam/wam-deref var-term-in-reg))
          "Unbound var term should dereference to itself"))))

(deftest test-wam-deref-bound-variable
  (testing "wam-deref on a variable bound to a constant"
    (reset-wam-for-test!)
    (let [var-heap-addr (count @wam/heap)
          _ (wam/heap-push! {:tag :var :addr var-heap-addr})
          const-val 'my-const
          const-heap-addr (count @wam/heap)
          _ (wam/heap-push! (wam/const-tag const-val))
          _ (wam/heap-set! var-heap-addr (wam/ref-tag const-heap-addr))
          var-term-in-reg {:tag :var :addr var-heap-addr}]
      (is (= (wam/const-tag const-val) (wam/wam-deref var-term-in-reg))
          "Bound var should dereference to the constant"))))

(deftest test-wam-deref-chain-of-references
  (testing "wam-deref on a chain of variable references"
    (reset-wam-for-test!)
    (let [var1-addr (wam/heap-push! {:tag :var :addr 0})
          var2-addr (wam/heap-push! {:tag :var :addr 1})
          const-addr (wam/heap-push! (wam/const-tag 'final-value))
          _ (wam/heap-set! var1-addr (wam/ref-tag var2-addr))
          _ (wam/heap-set! var2-addr (wam/ref-tag const-addr))
          var1-term-in-reg {:tag :var :addr var1-addr}]
      (is (= (wam/const-tag 'final-value) (wam/wam-deref var1-term-in-reg))
          "Chain of refs should dereference to final constant"))))

(deftest test-tag-constructors
  (testing "Tag constructor functions (excluding var-tag)"
    (is (= {:tag :const :value 'test} (wam/const-tag 'test)))
    (is (= {:tag :ref :addr 456} (wam/ref-tag 456)))
    (is (= {:tag :struct :functor 'f :arity 2} (wam/struct-tag 'f 2)))
    ))

(deftest test-backtracking-setup
  (testing "Basic backtracking setup"
    (reset-wam-for-test!)
    (wam/execute-instruction [:try_me_else 10])
    (is (= 0 @wam/B))
    (is (= 1 (count @wam/stack)))))

(deftest test-bind-var-with-trail
  (testing "Variable binding with trail recording"
    (reset-wam-for-test!)
    (let [var-heap-addr (wam/heap-push! {:tag :var :addr (count @wam/heap)})
          target-heap-addr (wam/heap-push! (wam/const-tag 'bound-value))]
      (wam/bind-var! var-heap-addr target-heap-addr)
      (is (= (wam/ref-tag target-heap-addr) (wam/heap-get var-heap-addr)))
      (is (= [var-heap-addr] @wam/trail)))))

(deftest test-fail-backtracking-simple
  (testing "FAIL! initiates backtracking and restores state"
    (reset-wam-for-test!)
    (let [initial-X (vec (take 8 (range)))
          initial-P 1
          initial-CP 2
          initial-E 3
          initial-B -1
          initial-TR-size (count @wam/trail)
          initial-H-val @wam/H
          next-clause-P 99]
      
      (reset! wam/X initial-X)
      (reset! wam/P initial-P)
      (reset! wam/CP initial-CP)
      (reset! wam/E initial-E)
      (reset! wam/B initial-B)
      (reset! wam/TR initial-TR-size)
      (reset! wam/H initial-H-val)
      
      (wam/execute-instruction [:try_me_else next-clause-P])
      (is (= 0 @wam/B) "B points to new choice point")
      (is (= (inc initial-P) @wam/P) "P advanced after try_me_else")
      
      (wam/set-X! 0 'changed-val) 
      (reset! wam/P 50)
      (let [var_to_bind_addr (wam/heap-push! {:tag :var :addr @wam/H})]
         (wam/bind-var! var_to_bind_addr (wam/heap-push! (wam/const-tag 'temp-binding))))
      (is (not= initial-X @wam/X) "X registers modified")
      (is (not= initial-TR-size (count @wam/trail)) "Trail modified")

      (wam/FAIL!)

      (is (= initial-X @wam/X) "X registers should be restored")
      (is (= initial-CP @wam/CP) "CP should be restored")
      (is (= initial-E @wam/E) "E should be restored")
      (is (= initial-B @wam/B) "B should be restored to previous B (which was -1)")
      (is (= initial-TR-size (count @wam/trail)) "Trail should be unwound to size at choice point creation")
      (is (= initial-H-val @wam/H) "H should be restored to H at choice point creation")
      (is (= next-clause-P @wam/P) "P should be set to next_clause_P from choice point"))))

(defn run-wam-tests []
  "Запуск всех тестов WAM эмулятора"
  (run-tests 'wam-emulator.core-test)) 
