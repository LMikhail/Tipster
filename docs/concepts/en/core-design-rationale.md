# Tipster Core Design Rationale

## Summary of Decisions

| Concept | Key Advantage (Pro) | Main Trade-off (Con) | Decision Weight |
| :--- | :--- | :--- | :--- |
| **Clojure Superset** | Huge effort savings by reusing the ecosystem. | High entry barrier for non-Clojure developers. | `++` (Strategically sound) |
| **`Symbol` over `Atom`** | Eliminates critical confusion with `clojure.core/Atom`. | Minor unfamiliarity for Prolog veterans. | `+` (Pragmatic and necessary) |
| **Dual Semantics** | Unifies logic and computation in a single construct. | High cognitive load on the developer. | `++` (Risky, but the core innovation) |
| **`def` vs. `defn`** | Clear semantic separation of data and logic. | Deviates from Prolog's purity, where a fact is a special case of a rule. | `+` (Improves readability) |
| **`\|` Separator** | Best compromise: valid & visually clear. | Not a standard naming convention. | `+` (Pragmatic and clear) |
| **`\|l` Modifier** | Elegance and the ability to overload names. | Risk of accidental error (forgetting `\|l`). | `~` (An elegant compromise) |
| **Invocation Modifiers** | Maximum control and clarity of intent in the code. | Minor syntactic noise. | `++` (Key to flexibility) |
| **Extensibility** | Ease of adding new evaluation strategies. | Potential for future language complexity. | `+` (Useful future-proofing) |

---

## Detailed Breakdown of Decisions

Below is a detailed description of each decision with the pros and cons briefly summarized in the table above.

### 1. Tipster as a Seamless Superset of Clojure

**Concept:** Instead of creating a new language from scratch, Tipster extends Clojure by adding logic programming constructs to it.

#### Pros

*   **Ecosystem Reuse:** Allows the use of the entire rich set of libraries, tools, and existing codebase of Clojure.
*   **Low Entry Barrier for Clojure Developers:** Programmers familiar with Clojure do not need to learn a completely new syntax.
*   **Full Compatibility:** Ensures natural and seamless interaction between declarative logic code and standard functional/imperative Clojure code.

#### Cons

*   **High Entry Barrier for Non-Clojure Developers:** Programmers from the Prolog world or other languages will first need to master the basics of Clojure.
*   **Risk of Semantic Conflicts:** The compiler must be very carefully designed to ensure that new constructs do not conflict with existing Clojure semantics and lead to unexpected behavior.

### 2. Terminology: `Symbol` instead of `Atom`

**Concept:** To denote unique symbolic constants (the equivalent of `atom` in Prolog), the term and type `Symbol` from Clojure is used, and the term `Atom` is intentionally avoided.

#### Pros

*   **Eliminates Confusion:** Unambiguously resolves the well-known homonymy issue between `atom` in Prolog (a constant) and `Atom` in Clojure (a reference type for state management).
*   **Idiomatic:** Using `Symbol` for identifiers is fully in the spirit and practice of Clojure.

#### Cons

*   **Unfamiliarity for Prolog Programmers:** Logic programming veterans may need some time to get used to the new terminology.

### 3. Dual Semantics of Terms

**Concept:** Any term `(f t₁ ... tₙ)` can be interpreted both as a logical pattern for search (`Φ_L`) and as a computational expression for execution (`Φ_C`).

#### Pros

*   **Expressive Power:** This is a key innovation that unifies the worlds of logic and computation. The same code can be used for both search and direct execution.
*   **Conciseness:** Eliminates the need for "glue code" to call functions from logic rules and vice versa.

#### Cons

*   **High Cognitive Load:** The concept can be difficult to grasp. The developer must be clearly aware of which interpretation is active at any given moment to avoid subtle bugs.
*   **Compiler Complexity:** Implementing a compiler that correctly handles both semantics and the switching between them is a non-trivial task.

### 4. Separation of `def` (facts) and `defn` (rules/functions)

**Concept:** Facts (unconditional statements) are defined via `def`, while rules and functions (computational abstractions) are defined via `defn`.

#### Pros

*   **Clear Separation:** The syntax emphasizes the difference between static data (`def`) and computational logic (`defn`), which improves readability.
*   **Adherence to Clojure Idioms:** Using `def` to define "data" and `defn` to define "behavior" is canonical for Clojure.

#### Cons

*   **Departure from Prolog Purity:** In "pure" logic programming, facts and rules are merely special cases of the same concept—a predicate clause. This separation may seem artificial.

### 5. Rationale for Choosing the `|` Separator

**Concept:** A special separator symbol, `|` (pipe), was chosen to modify semantics, attached to the entity's name.

**Rationale:** An analysis of various symbols (`-`, `::`, `!`, `?`, `$` etc.) was conducted based on syntactic validity, visual clarity, and idiomatic conflicts.
*   `::` and `^` proved to be syntactically invalid.
*   `$` was syntactically the best option, but was rejected due to poor visual readability in text.
*   `!`, `?`, and `%` have strong, conflicting meanings in Clojure.
*   `-` is widely used in function and variable names, which would lead to ambiguity.
*   `|` was chosen as the best compromise: it is valid, visually clear, and has minimal conflicts.

### 6. The `|l` Modifier for Defining Rules

**Concept:** A rule is distinguished from a function not by a new macro (`defrule`), but by the `|l` modifier attached to `defn`: `(defn|l ...)`.

#### Pros

*   **Elegance and Minimalism:** Allows for the reuse of the familiar `defn` macro.
*   **Name Overloading:** Makes it possible to define a rule and an optimized function with the same name.

#### Cons

*   **Risk of Accidental Errors:** A developer might forget to specify `|l` and accidentally define a regular function instead of a rule.
*   **Non-standard Syntax:** The `symbol|modifier` naming scheme is not a standard convention in Clojure.

### 7. Explicit Invocation Modifiers (`|l`, `|f`, `|seq`)

**Concept:** The semantics of an invocation (logical, functional) are determined by an explicit modifier on the caller's side: `(grandparent|l ...)`.

#### Pros

*   **Maximum Clarity and Control:** The calling code explicitly declares its intent.
*   **Pragmatism of `|seq`:** The `|seq` modifier is a pragmatic solution that materializes a lazy sequence of solutions into a regular collection.
*   **Flexibility:** Allows for mixing paradigms in a flexible way.

#### Cons

*   **Syntactic Noise:** An abundance of modifiers can reduce readability.
*   **Increased Developer Responsibility:** One must remember the rules for applying modifiers.

### 8. Extensibility via Modifiers

**Concept:** The architecture allows for the easy addition of new modifiers (`|fl`, `|lf`) to implement mixed evaluation strategies.

#### Pros
*   **Future-Proofing:** The system can be easily extended with new execution strategies.
*   **Powerful Metaprogramming Mechanism:** Opens up possibilities for creating custom invocation semantics.

#### Cons
*   **Risk of Over-complexity:** A large number of modifiers could make the language complex.
