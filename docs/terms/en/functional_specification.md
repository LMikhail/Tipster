# Tipster Core Functional Specification

## 1. Purpose and Scope

This document describes the functional requirements and software interfaces of all key modules of the Tipster system. It serves as a technical bridge between the conceptual foundations (defined in [core.md](../concepts/en/core.md)) and the software implementation.

All terminology and mathematical notation should be interpreted in strict accordance with the document [Tipster Core Concepts](../concepts/en/core.md).

---

## 2. Module Architecture and Dependencies

### 2.1 Dependency Hierarchy

```
types.clj           (foundation - protocols and base types)
    ↓
terms.clj           (term operations)
    ↓
unification.clj     (unification algorithm)
    ↓
solver.clj          (reasoning engine)
    ↓
knowledge.clj       (knowledge base)
    ↓
core.clj            (public API)
```

### 2.2 Modular Architecture Principles

- **Minimal dependencies**: each module depends only on strictly necessary lower-level modules
- **Clear interfaces**: all interactions between modules happen through explicitly defined protocols
- **Testability**: each module can be tested independently

---

## 3. Module `tipster.types`

### 3.1 Purpose
Fundamental module defining basic data types and protocols for implementing dual semantics of terms ($Φ_L$ and $Φ_C$).

### 3.2 Key Protocols

#### 3.2.1 Dual Semantics Protocol
```clojure
(defprotocol IDualSemantics
  "Protocol for implementing dual semantics of terms"
  (logical-eval [this context] 
    "Logical interpretation (Φ_L): term as pattern for unification")
  (computational-eval [this context] 
    "Computational interpretation (Φ_C): term as expression for evaluation"))
```

#### 3.2.2 Base Term Protocol
```clojure
(defprotocol ITerm
  "Base protocol for all term types"
  (term-type [this] "Returns term type: :variable, :constant, :compound")
  (is-ground? [this] "Checks if term is ground (no variables)")
  (variables [this] "Returns set of all variables in term")
  (subst [this substitution] "Applies substitution to term"))
```

#### 3.2.3 Execution Context Protocol
```clojure
(defprotocol IExecutionContext
  "Protocol for execution context management"
  (get-binding [this var] "Gets variable value")
  (add-binding [this var value] "Adds variable binding")
  (get-knowledge-base [this] "Gets current knowledge base"))
```

### 3.3 Functional Requirements

**R3.1**: Module must define protocols `IDualSemantics`, `ITerm`, `IExecutionContext`

**R3.2**: All base term types must implement `IDualSemantics` and `ITerm`

**R3.3**: Execution context must support immutable binding operations

---

## 4. Module `tipster.terms`

### 4.1 Purpose
Implements concrete term types and functions for working with them, including conversions between Clojure and Tipster representations.

### 4.2 Data Structures

#### 4.2.1 Variable
```clojure
(defrecord Variable [name id]
  ITerm
  IDualSemantics
  ;; method implementations
  )
```

#### 4.2.2 Constant
```clojure
(defrecord Constant [value]
  ITerm
  IDualSemantics
  ;; method implementations
  )
```

#### 4.2.3 Compound Term
```clojure
(defrecord Compound [functor args]
  ITerm
  IDualSemantics
  ;; method implementations
  )
```

### 4.3 Key Functions

#### 4.3.1 Constructors
```clojure
(defn make-variable [name] ...)
(defn make-constant [value] ...)
(defn make-compound [functor & args] ...)
```

#### 4.3.2 Conversions
```clojure
(defn clojure->term [data] 
  "Implements mapping φ: Clojure structures → Tipster terms")

(defn term->clojure [term context] 
  "Implements mapping φ⁻¹: Tipster terms → Clojure structures")
```

#### 4.3.3 Utilities
```clojure
(defn occurs-check [var term] 
  "Checks for circular references")

(defn rename-variables [term prefix] 
  "Renames variables in term")
```

### 4.4 Functional Requirements

**R4.1**: Must implement structures `Variable`, `Constant`, `Compound`

**R4.2**: All structures must implement protocols `ITerm` and `IDualSemantics`

**R4.3**: Function `clojure->term` must correctly convert all basic Clojure types

**R4.4**: Function `term->clojure` must consider variable bindings from context

---

## 5. Module `tipster.unification`

### 5.1 Purpose
Implements Robinson's unification algorithm for term matching, including substitution system.

### 5.2 Key Functions

#### 5.2.1 Unification
```clojure
(defn unify [term1 term2]
  "Finds most general unifier (MGU) for two terms
   Returns: {:success true :substitution {...}} or {:success false}")

(defn unify-with-subst [term1 term2 existing-subst]
  "Unification with existing substitutions")
```

#### 5.2.2 Substitutions
```clojure
(defn empty-substitution [] 
  "Creates empty substitution")

(defn compose-substitutions [subst1 subst2] 
  "Substitution composition")

(defn apply-substitution [term substitution] 
  "Applies substitution to term")
```

#### 5.2.3 Collection Unification
```clojure
(defn unify-sequences [seq1 seq2]
  "Unification of term sequences")

(defn unify-mappings [map1 map2]
  "Unification of mappings")
```

### 5.3 Functional Requirements

**R5.1**: Function `unify` must implement complete Robinson algorithm

**R5.2**: Must implement occurs check for circular references

**R5.3**: Substitution system must support composition and application

**R5.4**: Must support unification of Clojure collections (vectors, maps, sets)

---

## 6. Module `tipster.solver`

### 6.1 Purpose
Implements logical inference engine with backtracking, returning lazy sequences of solutions.

### 6.2 Key Functions

#### 6.2.1 Main Engine
```clojure
(defn solve [goal knowledge-base]
  "Finds all solutions for logical goal
   Returns: lazy sequence of variable bindings")

(defn solve-with-context [goal context]
  "Solving with given execution context")
```

#### 6.2.2 Backtracking Management
```clojure
(defn backtrack [choice-points]
  "Implements backtracking search")

(defn create-choice-point [alternatives context]
  "Creates choice point for alternatives")
```

#### 6.2.3 Query Optimization
```clojure
(defn reorder-goals [goals knowledge-base]
  "Reorders goals for optimization")

(defn estimate-goal-cost [goal knowledge-base]
  "Estimates goal execution cost")
```

### 6.3 Functional Requirements

**R6.1**: Engine must return lazy sequences of solutions

**R6.2**: Must implement full backtracking with choice points

**R6.3**: System must support early termination and solution limits

**R6.4**: Must have basic goal execution order optimization

---

## 7. Module `tipster.knowledge`

### 7.1 Purpose
Implements knowledge base for storing facts and rules, including indexing and efficient search.

### 7.2 Key Functions

#### 7.2.1 Fact Management
```clojure
(defn add-fact [kb fact]
  "Adds fact to knowledge base")

(defn remove-fact [kb fact]
  "Removes fact from knowledge base")

(defn query-facts [kb pattern]
  "Finds facts that unify with pattern")
```

#### 7.2.2 Rule Management
```clojure
(defn add-rule [kb rule]
  "Adds rule to knowledge base")

(defn query-rules [kb head-pattern]
  "Finds rules with matching head")
```

#### 7.2.3 Indexing
```clojure
(defn build-indexes [kb]
  "Builds indexes for search acceleration")

(defn query-by-functor [kb functor arity]
  "Fast search by functor and arity")
```

### 7.3 Functional Requirements

**R7.1**: Knowledge base must support efficient fact addition and removal

**R7.2**: Must implement functor-based indexing

**R7.3**: Queries must return lazy sequences of results

**R7.4**: Knowledge base must be a persistent data structure

---

## 8. Module `tipster.core`

### 8.1 Purpose
Main API module providing high-level functions and macros for working with Tipster.

### 8.2 Key Macros and Functions

#### 8.2.1 Entity Definition
```clojure
(defmacro def [& args]
  "Extended def for fact definition")

(defmacro defn|l [name args & body]
  "Definition of logical rules")
```

#### 8.2.2 Invocation Modifiers
```clojure
(defn invoke-with-modifier [fn-name modifier & args]
  "Function invocation with semantic modifier (|l, |f, |seq)")
```

#### 8.2.3 High-level Queries
```clojure
(defn query [pattern & options]
  "High-level query interface")

(defn ask [goal & options]
  "Goal truth checking")
```

### 8.3 Functional Requirements

**R8.1**: Macros must correctly handle modifiers (|l, |f, |seq)

**R8.2**: API must be compatible with Clojure idioms

**R8.3**: Must support interactive REPL work

---

## 9. Implementation Order

### 9.1 Stage 1: Foundation (types.clj)
- Define base protocols
- Implement type system
- Basic protocol tests

### 9.2 Stage 2: Terms (terms.clj)  
- Implement concrete term types
- Conversion functions
- Tests for all term operations

### 9.3 Stage 3: Unification (unification.clj)
- Robinson algorithm
- Substitution system
- Tests for complex unification cases

### 9.4 Stage 4: Solver (solver.clj)
- Basic backtracking engine
- Lazy sequences
- Integration tests

### 9.5 Stage 5: Knowledge Base (knowledge.clj)
- Fact and rule storage
- Basic indexing
- Performance tests

### 9.6 Stage 6: Integration (core.clj)
- Public API
- Macros and syntactic sugar
- Full-featured tests

---

## 10. Readiness Criteria

### 10.1 For Each Module
- All functional requirements (R*.*) fulfilled
- Test coverage >= 90%
- All public functions documented
- Code review passed

### 10.2 For System as a Whole
- Documentation examples run successfully
- Integration tests pass
- Performance meets basic requirements
- API is stable and user-friendly
