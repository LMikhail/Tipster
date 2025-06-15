# Core Concepts of Tipster

## 1. Introduction

This document lays the formal foundation for the core concepts of the Tipster project. Its purpose is to serve as a canonical source of truth, defining the system's fundamental building blocks with mathematical and semantic rigor.

At the core of Tipster lies a **compiler** that translates high-level declarative code, written as a **seamless superset of Clojure**, into efficient executable code.

## 2. Basic Terminology and Term Structure

(The sections "Basic Terminology: Atom and Symbol," "Term," and "Ground Term" remain unchanged as they define universal structural elements.)

...

## 5. Defining Entities: `def` and `defn`

The Tipster architecture elegantly separates definitions into two types: **assertions** (`def`) and **computational abstractions** (`defn`).

### Facts (`def`)

**Definition:** A **fact** is an unconditional, declarative statement that a certain relation is true for a given set of **ground terms**.

**Implementation in Tipster:** A fact is defined using `def`, with a ground term as its argument. This syntax emphasizes its nature as an immutable assertion.

```clojure
(def (parent "alice" "bob"))
(def (parent "alice" "charlie"))
```

### Rules and Functions (`defn`)

**Definition:** Both rules and functions are **computational abstractions**—they accept arguments and produce a result. Therefore, the single, canonical Clojure operator `defn` is used to define them.

**Implementation in Tipster:** To allow the compiler to distinguish between a rule and a function, a **definition modifier `::l`** (logic) is used.

*   `(defn::l ...)` defines a **Rule**. The compiler transforms it into a multi-valued function that returns a lazy sequence of solutions.
*   `(defn ...)` (without a modifier) defines a standard Clojure **Function**.

This allows for the definition of a predicate and a computational function of the same name without any conflicts:

```clojure
;; RULE (implementation of the P_grandparent predicate)
(defn::l grandparent [?x ?z]
  (parent ?x ?y)
  (parent ?y ?z))

;; FUNCTION (implementation of the C_grandparent computational function)
(defn grandparent [x z]
  (println "Fast check invoked!")
  ; ... code for a fast check using an in-memory graph ...
)
```

## 6. Invocation and World Interaction

Tipster's key innovation is managing invocation semantics through **explicit modifiers**. Since Tipster is a superset of Clojure, there is no need for special "gateways" or APIs. Any entity can be invoked from any context using the appropriate modifier.

### Default Context

*   **Inside `defn::l` (a rule)**, all invocations are interpreted as **logical goals (`::l`)** by default.
*   **Inside `defn` (a function) and in the REPL**, all invocations are considered **computational (`::f`)** by default.

### Invocation Modifiers

*   `::f` (function): Execute as a standard function call. Returns a single scalar value.
*   `::l` (logic): Execute as a logical goal in **streaming mode**. Returns a **lazy sequence of solution tuples**. Each tuple contains a complete set of bound variables for one solution.
*   `::seq` (sequence): Execute as a logical goal but **materialize the result**. The engine gathers all solutions into a single **lazy sequence** and binds it as a whole to a variable.

### Invocation Examples

**1. Invoking Logic from Standard Code:**

No special operator is needed to find all of "alice"'s grandchildren. A direct call to the rule with the `::l` modifier is sufficient. The `_` symbol is used as a placeholder for the "unknown" we want to find.

```clojure
;; Query: (grandparent "alice" _)
;; Result: '(["alice" "charlie"] ["alice" "david"]) - a lazy sequence
(let [solutions (grandparent::l "alice" _)
      ;; `solutions` is now a standard collection that can be processed
      ;; with standard Clojure functions.
      grandchildren (map second solutions)]
  (println "Alice's grandchildren:" grandchildren))
```

**2. Using `::f` and `::seq` within a Rule:**

```clojure
(defn::l child-count [?parent ?count]
  ;; Invoke `parent` in `::seq` mode to bind the `children`
  ;; variable to a lazy sequence of ALL children.
  (let [children (parent::seq ?parent _)]
    ;; `children` is now a standard collection.
    ;; We can apply `count` to it by explicitly
    ;; specifying `::f` mode.
    (= ?count (count::f children))))
```

## 7. The Knowledge Base (KB)

**Definition:** The **Knowledge Base (KB)** is the formally defined set of all **provable ground terms**.

This set is formed by the Tipster compiler based on all `def` (facts) and `defn::l` (rules) defined in the project.

## 8. Extensibility

The modifier-based architecture is easily extensible. For instance, resolution strategies like `::fl` (function-then-logic) and `::lf` (logic-then-function) can be introduced simply by teaching the compiler new semantics. 
