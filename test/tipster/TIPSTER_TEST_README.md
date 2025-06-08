# Testing Tipster Logic Engine

This project uses the standard Clojure testing structure with `clojure.test` to verify the correctness of the unification engine and logical solver.

## Test Structure

```
test/
└── tipster/
    └── core_test.clj    - Main tests for the logic engine
```

## Running Tests

### Basic execution of all tests
```bash
clj -M:test
```

### Interactive testing through REPL
```bash
clj
```
Then in REPL:
```clojure
(require 'tipster.core-test)
(run-tests 'tipster.core-test)

;; Or run a specific test
(test-unify-compound-terms)
```

## Test Categories

### Basic Component Tests

#### Term Creation Tests
- `test-term-creation` - Verification of variable, atom, and compound term creation
- `test-bindings-operations` - Operations with variable bindings
- `test-deref-term` - Term dereferencing with bindings

```clojure
(deftest test-term-creation
  ;; Checks correctness of creating all term types
  ;; and their properties through ITerm protocol
  )
```

### Unification Tests

#### Basic Unification
- `test-unify-identical-terms` - Unification of identical terms
- `test-unify-different-atoms` - Failed unification of different atoms
- `test-unify-variable-with-atom` - Binding variable with atom
- `test-unify-variable-with-variable` - Unification of variables together

#### Complex Unification
- `test-unify-compound-terms` - Unification of compound terms with variables
- `test-unify-different-functors` - Failed unification of different functors
- `test-unify-different-arity` - Failed unification of terms with different arity
- `test-occurs-check` - Occurs check (preventing infinite structures)

```clojure
(deftest test-unify-compound-terms
  (testing "Unification of compound terms"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          compound1 (tipster/make-compound 'f var1 'b)
          compound2 (tipster/make-compound 'f 'a var2)
          result (tipster/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (tipster/make-atom 'a) (tipster/lookup-binding var1 result)))
      (is (= (tipster/make-atom 'b) (tipster/lookup-binding var2 result))))))
```

### Knowledge Base Tests

#### Knowledge Management
- `test-knowledge-base-operations` - Adding facts and rules
- `test-clear-knowledge-base` - Clearing knowledge base

```clojure
(deftest test-knowledge-base-operations
  ;; Checks correctness of adding and storing
  ;; facts and rules in the knowledge base
  )
```

### Logical Solver Tests

#### Basic Solving
- `test-solve-simple-fact` - Solving simple facts
- `test-solve-fact-with-variable` - Solving with variables
- `test-solve-rule` - Applying rules for inference
- `test-solve-multiple-solutions` - Multiple solutions

```clojure
(deftest test-solve-rule
  (testing "Solving through rule"
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
```

### Integration Tests with Clojure

#### Data Conversion
- `test-clojure-to-tipster-conversion` - Clojure → Tipster
- `test-tipster-to-clojure-conversion` - Tipster → Clojure

```clojure
(deftest test-clojure-to-tipster-conversion
  ;; Checks correctness of converting various
  ;; Clojure structures to Tipster terms
  )
```

### Macro Tests

#### Convenient Syntax
- `test-deffact-macro` - Fact definition macro
- `test-query-macro` - Query macro

```clojure
(deftest test-deffact-macro
  (testing "deffact macro"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    
    ;; Check that fact is correctly added to knowledge base
    ))
```

### Integration Tests

#### Complex Scenarios
- `test-complex-family-relations` - Complex family relationships
- `test-performance-with-many-facts` - Performance with large data

```clojure
(deftest test-complex-family-relations
  (testing "Complex family relationships"
    ;; Creates complex system of family relationships
    ;; and checks correctness of logical inference
    ))
```

## Testing Conventions

### Test Naming
- Basic function tests: `test-function-name`
- Complex scenario tests: `test-scenario-description`
- Integration tests: `test-integration-aspect`

### Test Structure
1. **State reset:** Always start with `(reset-tipster-for-test!)`
2. **Setup:** Create necessary terms and add facts/rules
3. **Execution:** Perform tested operation (unification, goal solving)
4. **Verification:** Ensure result correctness

### Checking Unification Results

```clojure
;; Check successful unification
(is (not (nil? (tipster/unify term1 term2))))

;; Check variable binding
(let [result (tipster/unify var term)]
  (is (= expected-value (tipster/lookup-binding var result))))

;; Check failed unification
(is (nil? (tipster/unify incompatible-term1 incompatible-term2)))
```

### Checking Solutions

```clojure
;; Check number of solutions
(let [solutions (tipster/solve-goal query)]
  (is (= expected-count (count solutions))))

;; Check solution contents
(let [solutions (tipster/solve-goal query)
      bound-values (set (map #(tipster/lookup-binding var %) solutions))]
  (is (contains? bound-values expected-value)))
```

## Test Debugging

### Detailed State Output

```clojure
(deftest debug-test
  (testing "Debug specific behavior"
    (reset-tipster-for-test!)
    
    ;; Term creation
    (println "Terms created:" term1 term2)
    
    ;; Unification
    (let [result (tipster/unify term1 term2)]
      (println "Unification result:" result)
      
      ;; Checks
      )))
```

### Step-by-step Execution in REPL

```clojure
;; Creating test data
(def var-x (tipster/make-variable "X"))
(def atom-a (tipster/make-atom 'a))

;; Unification
(tipster/unify var-x atom-a)

;; Check knowledge base
(tipster/add-fact! (tipster/make-compound 'human 'alice))
@tipster/knowledge-base

;; Solve query
(tipster/solve-goal (tipster/make-compound 'human (tipster/make-variable "X")))
```

## Test Coverage

### Current Coverage

- ✅ **Term creation** - all types (variables, atoms, compounds)
- ✅ **Unification** - basic and complex, with occurs check
- ✅ **Dereferencing** - with binding chains
- ✅ **Knowledge base** - adding, removing, searching facts and rules
- ✅ **Logical solver** - solving facts and rules
- ✅ **Clojure integration** - data conversion
- ✅ **Macros** - convenient syntax for working
- ✅ **Complex scenarios** - family relationships, performance

### Planned Tests

- ⚠️ **Constraint Logic Programming** - working with constraints
- ⚠️ **Optimizations** - indexing, memoization
- ❌ **Parallel computations** - multithreading  
- ❌ **Database integration** - external knowledge sources

## Automation

### CI/CD Integration

```bash
# Command for CI
clj -M:test

# With detailed output
clj -M:test :verbose
```

### Performance Profiling

```clojure
;; Measure execution time
(time (run-tests 'tipster.core-test))

;; Profile specific operations
(time (tipster/solve-goal large-query))
```

## Extending Tests

### Adding New Tests

1. **For new unification algorithms:**
   ```clojure
   (deftest test-new-unification-feature
     (testing "Description of the new feature"
       (reset-tipster-for-test!)
       ;; Create test data
       ;; Execute operation
       ;; Check results
       ))
   ```

2. **For new term types:**
   ```clojure
   (deftest test-new-term-type
     (testing "New term type behavior"
       ;; Check creation
       ;; Check unification
       ;; Check integration with existing components
       ))
   ```

3. **For performance:**
   ```clojure
   (deftest test-performance-scenario
     (testing "Performance with specific data patterns"
       ;; Create large amount of data
       ;; Measure execution time
       ;; Check results
       ))
   ```

## Best Practices

### Test Isolation
- Always use `reset-tipster-for-test!` at the beginning of each test
- Don't rely on test execution order
- Create minimally necessary data for each test

### Readability
- Use descriptive names for tests and variables
- Add comments for complex test scenarios
- Group related checks in one test

### Test Performance
- Avoid creating unnecessarily large data structures
- Use lazy sequences for large results
- Profile slow tests

This testing architecture ensures reliable verification of all aspects of the Tipster logic engine and guarantees correct system operation during further development. 
