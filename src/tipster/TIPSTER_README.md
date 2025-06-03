# Tipster – Functional-Logic Engine

Implementation of a unification engine and logic solver for the Tipster language, providing integration of Clojure functional programming with declarative logic programming.

## Architecture

### Main Components

```
src/tipster/core.clj          - Core logic engine code
test/tipster/core_test.clj    - Unification and logical inference tests
deps.edn                      - Project dependencies
```

## Usage

### Basic Work with Terms

```clojure
(require '[tipster.core :as t])

;; Creating terms
(def var-x (t/make-variable "X"))
(def atom-a (t/make-atom 'a))
(def compound (t/make-compound 'f 'a 'b))

;; Unification
(t/unify var-x atom-a)  ; => {<id-var-x> atom-a}
```

### Working with Facts and Rules

```clojure
;; Defining facts via macro
(t/deffact (person alice))
(t/deffact (person bob))
(t/deffact (parent alice bob))

;; Defining rules
(t/defrule (grandfather ?X ?Z)
  [(parent ?X ?Y) (parent ?Y ?Z)])

;; Queries
(t/query (person ?X))        ; Find all people
(t/query (grandfather alice ?Z))  ; Find Alice's grandchildren
```

### Integration with Clojure

```clojure
;; Data conversion
(t/clojure-term->tipster-term '(f ?X a))
(t/tipster-term->clojure-term compound {})

;; Working with Clojure predicates
(t/integrate-clojure-predicate integer?)
```

## Key Features

### 1. Robinson Unification Algorithm

* **Full unification** of any terms
* **Occurs check** to prevent infinite structures
* **Efficient dereferencing** of variables

```clojure
;; Unification of compound terms
(t/unify (t/make-compound 'f (t/make-variable "X") 'b)
         (t/make-compound 'f 'a (t/make-variable "Y")))
;; => {<X-id> atom-a, <Y-id> atom-b}
```

### 2. Logic Solver

* **Resolution** using SLD strategy
* **Breadth-first search** of all solutions
* **Lazy evaluation** for efficient processing of large search spaces

```clojure
;; Solving multiple goals
(t/solve-goals [(t/make-compound 'parent 'alice (t/make-variable "X"))
                (t/make-compound 'age (t/make-variable "X") (t/make-variable "Y"))])
```

### 3. Knowledge Base

* **Facts** – basic assertions
* **Rules** – logical implications
* **Dynamic update** at runtime

```clojure
;; Programmatic knowledge base control
(t/add-fact! (t/make-compound 'likes 'mary 'food))
(t/add-rule! head-term [body-term1 body-term2])
(t/clear-knowledge-base!)
```

### 4. Clojure Integration

* **Bidirectional data conversion**
* **Use of Clojure predicates** in logic rules
* **Macros** for convenient syntax

```clojure
;; Built-in predicates
'= '> '< 'integer? 'string?

;; User-defined predicates
(defn age-greater-than [person age]
  (and (t/solve-goal (t/make-compound 'age person (t/make-variable "A")))
       (> age 18)))
```

## Architectural Solutions

### 1. ITerm Protocol

Unified interface for all types of terms:

```clojure
(defprotocol ITerm
  (term-type [this])
  (term-value [this])
  (is-variable? [this])
  (is-compound? [this]))
```

### 2. Variable Binding System

```clojure
;; Binding environment - simple hash table
{variable-id term}

;; Operations
(empty-bindings)
(lookup-binding var bindings)
(bind-variable var term bindings)
```

### 3. Variable Renaming

Automatic variable renaming in rules to avoid conflicts:

```clojure
(defn rename-variables [term]
  ;; Generates new unique names for all variables
  )
```

## Performance

### Optimizations

1. **Lazy sequences** – solutions are computed on demand
2. **Memoization** – caching of unification results
3. **Efficient dereferencing** – minimal recursive calls
4. **Fact indexing** – fast lookup by functor

### Scalability

* Support for large knowledge bases (thousands of facts)
* Efficient handling of deep recursion
* Memory management via Clojure's GC

## Comparison with Alternatives

| Feature         | WAM    | Tipster       | SWI-Prolog |
| --------------- | ------ | ------------- | ---------- |
| Target language | Prolog | Clojure+Logic | Prolog     |
| Integration     | Low    | High          | Medium     |
| Performance     | High   | Medium        | High       |
| Ease of use     | Low    | High          | Medium     |
| Extensibility   | Low    | High          | Medium     |

## Future Development

### Planned Improvements

1. **CLP(FD)** – Constraint Logic Programming over Finite Domains
2. **Tabling** – memoization to prevent infinite recursion
3. **Compiler** – Tipster code optimization
4. **Indexing** – advanced data structures for search
5. **Parallelism** – leveraging JVM capabilities

### Ecosystem Integration

* **DataScript** – logical queries to databases
* **Datomic** – temporal queries and transactions
* **core.logic** – compatibility with existing solutions

## Usage Examples

### Family Relationships

```clojure
;; Facts
(t/deffact (parent tom bob))
(t/deffact (parent bob pat))
(t/deffact (parent pat ann))

;; Rules
(t/defrule (grandfather ?X ?Z)
  [(parent ?X ?Y) (parent ?Y ?Z)])

(t/defrule (ancestor ?X ?Z)
  [(parent ?X ?Z)])

(t/defrule (ancestor ?X ?Z)
  [(parent ?X ?Y) (ancestor ?Y ?Z)])

;; Queries
(t/query (grandfather tom ?X))   ; => [(grandfather tom pat)]
(t/query (ancestor tom ?X))      ; => [(ancestor tom bob) (ancestor tom pat) (ancestor tom ann)]
```

### Complex Data Structures

```clojure
;; Lists
(t/deffact (list [1 2 3]))
(t/deffact (length [1 2 3] 3))

;; Trees
(t/deffact (tree (node leaf 5 leaf)))
(t/defrule (in-tree ?X (node ?L ?X ?R)) [])
(t/defrule (in-tree ?X (node ?L ?V ?R)) [(in-tree ?X ?L)])
(t/defrule (in-tree ?X (node ?L ?V ?R)) [(in-tree ?X ?R)])
```

## Conclusion

Tipster is a modern alternative to classic logic languages, providing:

* **Ease of use** thanks to Clojure syntax
* **Power of logic programming** with full unification
* **JVM ecosystem integration** for practical applications
* **Architectural flexibility** for various domains

This makes Tipster an ideal tool for building expert systems, knowledge management systems, and complex business logic on a modern technology platform.

---
