# Core Concepts of Tipster

## 1. Introduction

This document lays the formal foundation for the core concepts of the Tipster project. Its purpose is to serve as a canonical source, defining the system's fundamental building blocks with mathematical and semantic rigor.

At the core of Tipster lies a **compiler** that translates high-level declarative code, written as a **seamless superset of Clojure**, into efficient executable code.

## 2. Basic Terminology and Term Structure

To avoid confusion arising at the intersection of logic programming (in the spirit of Prolog) and Clojure, it is necessary to clearly define the basic terms.

*   **Symbol:** In Clojure, a `Symbol` is an identifier that typically refers to something (e.g., a function or a variable). In Tipster, following this semantic, we use symbols to denote functors, variables, and other logical constructs. This concept is closest to what is called an **atom** in Prolog—a unique symbolic constant.

*   **Atom:** In Clojure, an `Atom` is a specific reference data type used to manage synchronous, uncoordinated state changes. **This term is not used in Tipster documentation** to avoid confusion with its Prolog homonym. We use the term **Symbol** to denote symbolic constants.

## 3. Term

In Tipster, as in classical logic programming, a **term** is the basic data structure.

### Term Structure

Structurally, a term can be one of three things:

1.  **Constant:** An immutable value. Constants can be:
    *   **Symbols** (as previously defined, e.g., `some-name`)
    *   Numbers (e.g., `42`)
    *   Strings (e.g., `"hello"`)
2.  **Variable:** An identifier that can be bound to another term during unification (e.g., `X`, `Person`).
3.  **Compound Term:** A structure of the form `(f t₁ ... tₙ)`, where `f` is a name called a **functor**, and `t₁ ... tₙ` are terms that are its arguments (e.g., `(point X 1)`). A more rigorous definition of a functor, revealing its semantics in Tipster, is provided below.

### Dual Semantics of a Term

The key distinction and extension of the concept in Tipster lies in its **dual semantics**. Every term is a syntactic construct that can be subjected to two different but interconnected types of interpretation:

1.  **Logical Interpretation ($Φ_L$)**: The term is treated as a **logical pattern** for unification and search. The interpretation $Φ_L(t, B) → S$ maps a term $t$ and a set of current variable bindings $B$ to a stream (possibly infinite) of solution sets $S$.
2.  **Computational Interpretation ($Φ_C$)**: The term is treated as a **computational expression** to be executed. The interpretation $Φ_C(t, C) → v$ maps a term $t$ and a computation context $C$ to a single resulting value $v$.

## 4. Ground Term

**Definition:**

A **Ground Term** is a term that **contains no variables**. It consists exclusively of functors and constants. Such terms represent concrete, fully defined data.

*   **Examples of ground terms:** `42`, `"alice"`, `(location "Paris" "France")`.
*   **Examples of non-ground terms** (containing variables): `X`, `(location City "France")`.

Further in this document, when referring to "true terms" in the context of facts and predicates, we will specifically mean **ground terms**.

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
