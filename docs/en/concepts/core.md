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

1.  **Constant:** An atomic (indivisible) value that serves as an endpoint in the unification process. The key principle is that a constant only unifies with the exact same constant or with a free variable. It has no internal *logical* structure that the unifier could recursively match.

    Since Tipster is a superset of Clojure, a constant can be **any native Clojure/Java data type** that is treated as a single unit. The responsibility for checking their equality is delegated to Clojure's standard `(=)` function.

    Examples of constants include, but are not limited to:
    *   **Numbers:** `42`, `3.14`, `22/7`
    *   **Strings:** `"hello world"`
    *   **Symbols:** `'some-name`
    *   **Keywords:** `:some-keyword`
    *   **Booleans:** `true`, `false`, and `nil`
    *   **Dates/Times, UUIDs, and other similar types:** `#inst "..."`, `#uuid "..."`

    It is important to note that native Clojure collections (vectors, maps, etc.) are **not** logical constants, as they have an internal structure relevant to unification. They fall into the category of **compound terms**.

2.  **Variable:** A symbol that acts as a placeholder and can be bound to any other term during unification.

    **Naming Convention:** Tipster adopts a key convention idiomatic to Lisp-like languages: **any free (not yet bound) symbol encountered in a term's argument position is considered a logical variable.**

    This means there is no need for special syntax (like `?` or `_` prefixes, or the requirement for uppercase letters as in Prolog). The compiler determines if a symbol is a variable by checking if it already has a value in the current lexical scope.

    *   In the term `(parent "alice" Child)`, the symbol `Child` is a variable if it has not been previously defined.
    *   In the rule `(defn|l grandparent [x z] (parent x y) (parent y z))`, the symbols `x`, `y`, and `z` are variables within the scope of that rule.

3.  **Compound Term:** A structural unit that groups other terms. In Tipster, for idiomatic integration with the host language, we divide compound terms into two semantic categories:

    *   **Relations (Lists):** A list of the form `(f t₁ ... tₙ)` is used to represent logical relations or predicates. The first element, `f`, is the **functor**—a symbol that names the relation. It is the "verb" of the logical language.
        *   Example: `(parent "alice" Child)`

    *   **Data Structures (Vectors, Maps, Sets):** Native Clojure collections are used to represent structured data. Their "functor" is their type itself. These are the "nouns" of the language.
        *   **Vectors:** `[1 2 Z]`—for ordered data.
        *   **Maps:** `{:name "John" :age Age}`—for key-value pairs.
        *   **Sets:** `#{:red :green :blue}`—for unique, unordered elements.

    This separation allows writing code that is both logically powerful and natural for a Clojure developer. For example, one can define a fact about an object using a map for its data: `(def (user 1 {:name "Alice" :role :admin}))`.

    The unification of compound terms always occurs structurally: the system attempts to recursively match their components.

    > **Detailed Information:** Detailed algorithms for term decomposition for efficient unification are described in the [Term Decomposition Specification](../terms/term-decomposition.md).

### Dual Semantics of a Term

Now that we have defined the different kinds of terms, we can clarify their key feature in Tipster—**dual semantics**. This means that the same syntactic construct can be interpreted either as a **logical pattern for matching** or as a **computable expression**. This duality manifests differently for different terms.

1.  **Logical Interpretation ($Φ_L$)**: The term is treated as a **pattern** that is used by the unification engine to find matches in the knowledge base. This is the primary mode of operation inside logical rules (`defn|l`).
    *   `X` is a variable to be bound.
    *   `(parent "alice" Child)` is a pattern to search for facts about the `parent` relation.
    *   `[1 Y]` is a pattern for matching against vectors whose first element is `1`.

2.  **Computational Interpretation ($Φ_C$)**: The term is treated as a **Clojure expression** to be evaluated to produce a value. This mode is used in regular Clojure code and can also be explicitly invoked inside rules using the `|f` modifier.
    *   `(+ 1 2)` evaluates to the constant `3`.
    *   `(str "hello" " " "world")` evaluates to the constant `"hello world"`.

**Applying Semantics to Term Types:**

*   **Constants and Variables:** For constants (numbers, strings, etc.), both interpretations essentially coincide: "evaluating" them yields themselves. For variables, the computational interpretation means retrieving the value bound to them.
*   **Relations (Lists `(f ...)`):** Here, the duality is most pronounced. The term `(grandparent "alice" X)` in a logical context ($Φ_L$) is a **goal to be searched for**, while in a computational context ($Φ_C$) it is a **call to the Clojure function of the same name**.
*   **Data Structures (Vectors, Maps, Sets):** Their primary role is to serve as **logical patterns ($Φ_L$)**. Their "computational interpretation" ($Φ_C$) is that they are **data literals**. If their elements are computable expressions (e.g., `(* 2 5)`), they are evaluated *before* the entire structure is used as a logical pattern. For example, in a rule, the term `[A (* 2 A)]` with `A=5` will first be evaluated into the pattern `[5 10]`, which is then used for unification.

## 4. Ground Term

**Definition:**

A **Ground Term** is a term that **contains no variables**. Such terms represent concrete, fully defined data.

*   **Examples of ground terms:** `42`, `"alice"`, `(location "Paris" "France")`, `[10, "ready"]`, `{:status :ok}`.
*   **Examples of non-ground terms** (containing variables): `X`, `(location City "France")`, `[Count, "ready"]`, `{:status Status}`.

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

**Implementation in Tipster:** To allow the compiler to distinguish between a rule and a function, a **definition modifier `|l`** (logic) is used.

*   `(defn|l ...)` defines a **Rule**. The compiler transforms it into a multi-valued function that returns a lazy sequence of solutions.
*   `(defn ...)` (without a modifier) defines a standard Clojure **Function**.

This allows for the definition of a predicate and a computational function of the same name without any conflicts:

```clojure
;; RULE (implementation of the P_grandparent predicate)
(defn|l grandparent [x z]
  (parent x y)
  (parent y z))

;; FUNCTION (implementation of the C_grandparent computational function)
(defn grandparent [x z]
  (println "Fast check invoked!")
  ; ... code for a fast check using an in-memory graph ...
)
```

## 6. Invocation and World Interaction

Tipster's key innovation is managing invocation semantics through **explicit modifiers**. Since Tipster is a superset of Clojure, there is no need for special "gateways" or APIs. Any entity can be invoked from any context using the appropriate modifier.

### Default Context

*   **Inside `defn|l` (a rule)**, all invocations are interpreted as **logical goals (`|l`)** by default.
*   **Inside `defn` (a function) and in the REPL**, all invocations are considered **computational (`|f`)** by default.

### Invocation Modifiers

*   `|f` (function): Execute as a standard function call. Returns a single scalar value.
*   `|l` (logic): Execute as a logical goal in **streaming mode**. Returns a **lazy sequence of solution tuples**. Each tuple contains a complete set of bound variables for one solution.
*   `|seq` (sequence): Execute as a logical goal but **materialize the result**. The engine gathers all solutions into a single **lazy sequence** and binds it as a whole to a variable.

### Invocation Examples

**1. Invoking Logic from Standard Code:**

No special operator is needed to find all of "alice"'s grandchildren. A direct call to the rule with the `|l` modifier is sufficient. The `_` symbol is used as a placeholder for the "unknown" we want to find.

```clojure
;; Query: (grandparent "alice" _)
;; Result: '(["alice" "charlie"] ["alice" "david"]) - a lazy sequence
(let [solutions (grandparent|l "alice" _)
      ;; `solutions` is now a standard collection that can be processed
      ;; with standard Clojure functions.
      grandchildren (map second solutions)]
  (println "Alice's grandchildren:" grandchildren))
```

**2. Using `|f` and `|seq` within a Rule:**

```clojure
(defn|l child-count [parent count]
  ;; Invoke `parent` in `|seq` mode to bind the `children`
  ;; variable to a lazy sequence of ALL children.
  (let [children (parent|seq parent _)]
    ;; `children` is now a standard collection.
    ;; We can apply `count` to it by explicitly
    ;; specifying `|f` mode.
    (= count (count|f children))))
```

## 7. The Knowledge Base (KB)

**Definition:** The **Knowledge Base (KB)** is the formally defined set of all **provable ground terms**.

This set is formed by the Tipster compiler based on all `def` (facts) and `defn|l` (rules) defined in the project.

## 8. Extensibility

The modifier-based architecture is easily extensible. For instance, resolution strategies like `|fl` (function-then-logic) and `|lf` (logic-then-function) can be introduced simply by teaching the compiler new semantics. 
