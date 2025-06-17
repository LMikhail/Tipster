# Detailed Data Structure Specification (DDS) - Tipster Core

## 1. Document Purpose

This document defines **all data structures, protocols, and types** used in the Tipster system. It serves as a technical reference specification for developers, describing precise implementation details at the Clojure code level.

The document is linked to the [Functional Specification](./functional_specification.md), which defines system **behavior**, while DDS defines **data structure**.

---

## 2. Base Protocols (tipster.types)

### 2.1 Dual Semantics Protocol

```clojure
(defprotocol IDualSemantics
  "Protocol for implementing dual semantics of terms (Φ_L and Φ_C)"
  (logical-eval [this context] 
    "Logical interpretation: term as pattern for unification
     Returns: pattern for matching with facts/rules")
  (computational-eval [this context] 
    "Computational interpretation: term as expression for evaluation
     Returns: result of expression evaluation"))
```

### 2.2 Base Term Protocol

```clojure
(defprotocol ITerm
  "Base protocol for all term types"
  (term-type [this] 
    "Returns term type: :variable | :constant | :compound")
  (is-ground? [this] 
    "Checks if term is ground (no variables)
     Returns: boolean")
  (variables [this] 
    "Returns set of all variables in term
     Returns: #{Variable}")
  (subst [this substitution] 
    "Applies substitution to term
     Parameters: substitution - {Variable -> Term}
     Returns: new term with applied substitution"))
```

### 2.3 Execution Context Protocol

```clojure
(defprotocol IExecutionContext
  "Protocol for execution context management"
  (get-binding [this var] 
    "Gets variable value
     Returns: Term | nil")
  (add-binding [this var value] 
    "Adds variable binding (immutably)
     Returns: new context")
  (get-knowledge-base [this] 
    "Gets current knowledge base
     Returns: KnowledgeBase"))

(defrecord ExecutionContext [bindings knowledge-base]
  IExecutionContext
  (get-binding [this var] 
    (get (:bindings this) var))
  (add-binding [this var value] 
    (assoc this :bindings (assoc (:bindings this) var value)))
  (get-knowledge-base [this] 
    (:knowledge-base this)))
```

### 2.4 Base Data Types

```clojure
;; Substitution - mapping from variables to terms
(def Substitution 
  "Type for representing substitutions"
  '{Variable Term})

;; Unification result
(defrecord UnificationResult [success substitution]
  ;; success: boolean
  ;; substitution: Substitution | nil
  )

;; Query solution - set of variable bindings
(def Solution
  "Type for representing one solution"
  '{Variable Term})
```

---

## 3. Term Structures (tipster.terms)

### 3.1 Variable

```clojure
(defrecord Variable [name id]
  ITerm
  (term-type [_] :variable)
  (is-ground? [_] false)
  (variables [this] #{this})
  (subst [this substitution] 
    (get substitution this this))
  
  IDualSemantics
  (logical-eval [this context] 
    ;; In logical context, variable is a pattern for binding
    this)
  (computational-eval [this context] 
    ;; In computational context - get value from context
    (or (get-binding context this) this))
  
  Object
  (toString [this] (str "?" (:name this)))
  (equals [this other] 
    (and (instance? Variable other) (= (:id this) (:id other))))
  (hashCode [this] (hash (:id this))))

;; Variable constructor
(defn make-variable 
  ([name] (->Variable (str name) (gensym name)))
  ([name id] (->Variable (str name) id)))
```

### 3.2 Constant

```clojure
(defrecord Constant [value]
  ITerm
  (term-type [_] :constant)
  (is-ground? [_] true)
  (variables [_] #{})
  (subst [this _] this)
  
  IDualSemantics
  (logical-eval [this _] 
    ;; In logical context, constant matches itself
    this)
  (computational-eval [this _] 
    ;; In computational context, constant returns its value
    (:value this))
  
  Object
  (toString [this] (str (:value this)))
  (equals [this other] 
    (and (instance? Constant other) (= (:value this) (:value other))))
  (hashCode [this] (hash (:value this))))

;; Constant constructor
(defn make-constant [value] 
  (->Constant value))
```

### 3.3 Compound Term

```clojure
(defrecord Compound [functor args]
  ITerm
  (term-type [_] :compound)
  (is-ground? [this] 
    (every? is-ground? (:args this)))
  (variables [this] 
    (reduce into #{} (map variables (:args this))))
  (subst [this substitution] 
    (->Compound (:functor this) 
                (mapv #(subst % substitution) (:args this))))
  
  IDualSemantics
  (logical-eval [this context] 
    ;; In logical context - pattern for unification
    this)
  (computational-eval [this context] 
    ;; In computational context - function call
    (let [fn-symbol (:functor this)
          evaluated-args (map #(computational-eval % context) (:args this))]
      (if-let [fn-var (resolve fn-symbol)]
        (apply @fn-var evaluated-args)
        (throw (ex-info "Unknown function" {:functor fn-symbol})))))
  
  Object
  (toString [this] 
    (str "(" (:functor this) " " (clojure.string/join " " (:args this)) ")"))
  (equals [this other] 
    (and (instance? Compound other) 
         (= (:functor this) (:functor other))
         (= (:args this) (:args other))))
  (hashCode [this] 
    (hash [(:functor this) (:args this)])))

;; Compound term constructor
(defn make-compound [functor & args] 
  (->Compound functor (vec args)))
```

---

## 4. Unification Structures (tipster.unification)

### 4.1 Unification Result

```clojure
(defrecord UnificationResult [success substitution]
  Object
  (toString [this] 
    (if (:success this)
      (str "SUCCESS: " (:substitution this))
      "FAILURE")))

(defn success [substitution] 
  (->UnificationResult true substitution))

(defn failure [] 
  (->UnificationResult false nil))
```

### 4.2 Substitution System

```clojure
;; Substitution represented as regular Clojure map
(def empty-substitution {})

(defn compose-substitutions 
  "Composition of two substitutions: (σ₁ ∘ σ₂)"
  [subst1 subst2]
  (merge (into {} (map (fn [[var term]] 
                         [var (apply-substitution term subst1)]) 
                       subst2))
         subst1))

(defn apply-substitution 
  "Applies substitution to term"
  [term substitution]
  (subst term substitution))

(defn occurs-check 
  "Checks if variable occurs in term (prevents cycles)"
  [var term]
  (contains? (variables term) var))
```

---

## 5. Solver Structures (tipster.solver)

### 5.1 Choice Point

```clojure
(defrecord ChoicePoint [alternatives context goal]
  ;; alternatives: lazy sequence of alternatives for backtracking
  ;; context: current execution context
  ;; goal: goal for which choice point was created
  )

(defn make-choice-point [alternatives context goal]
  (->ChoicePoint alternatives context goal))
```

### 5.2 Solver State

```clojure
(defrecord SolverState [goals context choice-points]
  ;; goals: stack of current goals to solve
  ;; context: current execution context
  ;; choice-points: stack of choice points for backtracking
  )

(defn make-initial-state [goal knowledge-base]
  (->SolverState [goal] 
                 (->ExecutionContext {} knowledge-base) 
                 []))
```

### 5.3 Solution Result

```clojure
(defrecord SolutionStream [solutions state]
  ;; solutions: lazy sequence of solutions
  ;; state: final solver state
  )
```

---

## 6. Knowledge Base Structures (tipster.knowledge)

### 6.1 Knowledge Base

```clojure
(defrecord KnowledgeBase [facts rules indexes]
  ;; facts: set of facts #{Term}
  ;; rules: set of rules #{Rule}
  ;; indexes: indexes for fast search
  )

(defn empty-knowledge-base []
  (->KnowledgeBase #{} #{} {}))
```

### 6.2 Rule

```clojure
(defrecord Rule [head body]
  ;; head: rule head term
  ;; body: sequence of goal terms in rule body
  Object
  (toString [this] 
    (str (:head this) " :- " (clojure.string/join ", " (:body this)))))

(defn make-rule [head & body]
  (->Rule head (vec body)))
```

### 6.3 Indexes

```clojure
(defrecord FunctorIndex [functor-map]
  ;; functor-map: {[functor arity] -> #{Term}}
  )

(defn build-functor-index [terms]
  (->FunctorIndex 
    (group-by (fn [term] 
                [(if (= :compound (term-type term)) 
                   (:functor term) 
                   :constant)
                 (if (= :compound (term-type term)) 
                   (count (:args term)) 
                   0)]) 
              terms)))
```

---

## 7. API Structures (tipster.core)

### 7.1 Query Configuration

```clojure
(defrecord QueryOptions [limit timeout strategy]
  ;; limit: maximum number of solutions (nil = all)
  ;; timeout: timeout in milliseconds (nil = no limit)  
  ;; strategy: search strategy (:depth-first, :breadth-first)
  )

(def default-query-options 
  (->QueryOptions nil nil :depth-first))
```

### 7.2 Query Result

```clojure
(defrecord QueryResult [solutions metadata]
  ;; solutions: sequence of solutions
  ;; metadata: execution information (time, step count, etc.)
  )
```

---

## 8. Utility Types

### 8.1 Errors and Exceptions

```clojure
(defn tipster-error [type message data]
  (ex-info message (merge {:type type} data)))

;; Error types
(def error-types
  {:unification-failure "Unification error"
   :infinite-loop "Infinite loop detected"
   :unknown-predicate "Unknown predicate"
   :syntax-error "Syntax error"})
```

### 8.2 Metadata and Debugging

```clojure
(defrecord DebugInfo [step-count backtrack-count time-ms]
  ;; step-count: number of solution steps
  ;; backtrack-count: number of backtracks
  ;; time-ms: execution time in milliseconds
  )
```

---

## 9. Validation and Specifications

### 9.1 Clojure spec for main types

```clojure
(require '[clojure.spec.alpha :as s])

(s/def ::variable (partial instance? Variable))
(s/def ::constant (partial instance? Constant))
(s/def ::compound (partial instance? Compound))
(s/def ::term (s/or :var ::variable :const ::constant :comp ::compound))

(s/def ::substitution (s/map-of ::variable ::term))
(s/def ::solution ::substitution)

(s/def ::knowledge-base (partial instance? KnowledgeBase))
(s/def ::execution-context (partial instance? ExecutionContext))
```

### 9.2 Invariants and Constraints

```clojure
;; Invariants for validation
(defn valid-term? [term]
  (satisfies? ITerm term))

(defn valid-substitution? [subst]
  (and (map? subst)
       (every? #(instance? Variable %) (keys subst))
       (every? valid-term? (vals subst))))

(defn acyclic-substitution? [subst]
  (not-any? (fn [[var term]] (occurs-check var term)) subst))
```
