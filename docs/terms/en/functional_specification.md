# Terms Module Functional Specification

## 1. Theoretical Foundation

### 1.1 Logic Programming and Terms

In logic programming, **terms** are the fundamental building blocks for representing knowledge and data. A term is a syntactic construct that represents objects, relationships, and structures in a logical system.

**Mathematical Definition**: Let V be a countably infinite set of variables, F be a set of function symbols (functors), and each f ∈ F has an associated arity ar(f) ≥ 0. The set of **terms** T(F,V) is the smallest set such that:

1. **V ⊆ T(F,V)** (all variables are terms)
2. **If f ∈ F and ar(f) = 0, then f ∈ T(F,V)** (nullary functors are terms)
3. **If f ∈ F, ar(f) = n > 0, and t₁, t₂, ..., tₙ ∈ T(F,V), then f(t₁, t₂, ..., tₙ) ∈ T(F,V)** (compound terms)

**Formal Classification**: A **term** t ∈ T(F,V) is either:
- A **variable** if t ∈ V
- An **atom** (atomic term) if t ∈ F and ar(t) = 0  
- A **compound term** if t = f(t₁, t₂, ..., tₙ) where f ∈ F, ar(f) = n > 0, and tᵢ ∈ T(F,V)

### 1.2 Functors

**Mathematical Definition**: A **functor** is a function symbol f ∈ F with an associated arity ar(f) ∈ ℕ₀ (non-negative integers).

**Notation**: f/n denotes a functor f with arity n.

**Properties**:
- **Arity**: ar(f) defines the number of arguments the functor takes
- **Identity**: Two functors are identical iff they have the same name and arity
- **Classification**:
  - **Constant** (atom): f/0 (arity 0)
  - **Function symbol**: f/n where n > 0

**Examples**:
- `person/2` - functor with arity 2: `person(john, 30)`
- `address/3` - functor with arity 3: `address("Main St", 123, "NY")`
- `john/0` - constant (atom): `john`

### 1.3 Compound Terms

**Mathematical Definition**: A **compound term** is a term of the form f(t₁, t₂, ..., tₙ) where:
- f ∈ F is a functor with ar(f) = n > 0
- t₁, t₂, ..., tₙ ∈ T(F,V) are terms (arguments)
- The structure is well-formed according to the arity constraint

**Formal Structure**: 
```
compound_term ::= functor(arg₁, arg₂, ..., argₙ)
where n = ar(functor) > 0
```

**Properties**:
- **Principal functor**: The outermost functor f
- **Arguments**: The sequence of terms (t₁, t₂, ..., tₙ)
- **Arity**: The number of arguments n = ar(f)
- **Depth**: Maximum nesting level of compound terms
- **Size**: Total number of functor and variable occurrences

### 1.4 Unification and Pattern Matching

Terms enable **unification** - the process of making two terms identical by finding appropriate variable bindings. This is the core mechanism that powers logical inference in Tipster.

**Mathematical Definition**: A **substitution** σ is a finite mapping from variables to terms: σ: V → T(F,V).

**Unification**: Two terms s, t ∈ T(F,V) are **unifiable** if there exists a substitution σ such that σ(s) = σ(t). The substitution σ is called a **unifier**.

**Most General Unifier (MGU)**: A unifier σ is most general if for any other unifier θ, there exists a substitution γ such that θ = γ ∘ σ.

**Example**:
```
f(X, a) unifies with f(b, Y) 
→ MGU: σ = {X ↦ b, Y ↦ a}
→ σ(f(X, a)) = σ(f(b, Y)) = f(b, a)
```

### 1.5 Dual Semantics in Tipster

In Tipster's dual semantics model, terms serve two purposes:

1. **Computational Semantics**: Terms represent data structures and function calls
2. **Logical Semantics**: Terms represent logical facts and patterns for inference

**Example**:
```clojure
;; Same term structure, dual interpretation:
(employee "John" 30 "IT")

;; Computational: creates an employee record
;; Logical: represents a fact about John being an employee
```

### 1.6 Structure Decomposition

Terms in Tipster are designed for **structure decomposition** - the ability to break down complex data structures into their constituent parts for pattern matching and logical inference.

**Mathematical Properties**:
- **Compositionality**: T(F,V) is closed under functor application
- **Decomposability**: Every compound term f(t₁, ..., tₙ) can be decomposed into f and (t₁, ..., tₙ)
- **Unifiability**: For any terms s, t ∈ T(F,V), unification is decidable

## 2. Term Types and Definitions

### 2.1 Variables

**Mathematical Definition**: A **variable** is an element v ∈ V where V is a countably infinite set disjoint from F.

**Properties**:
- **Unification**: A variable X can unify with any term t ∈ T(F,V)
- **Identity**: Each variable has a unique identifier
- **Scope**: Variables maintain consistent identity within a logical context

**Formal Notation**: Variables are denoted by uppercase letters: X, Y, Z, or symbolic names

**Syntax in Tipster**: Variables are prefixed with `?` in Clojure syntax: `?X`, `?name`

### 2.2 Atoms (Atomic Terms)

**Mathematical Definition**: An **atom** is a term a ∈ F where ar(a) = 0.

**Properties**:
- **Indivisibility**: Cannot be decomposed further
- **Unification**: An atom a unifies only with identical atoms or variables
- **Ground term**: Contains no variables

**Examples**: `john`, `42`, `"hello"`, `:keyword`

### 2.3 Compound Terms

**Mathematical Definition**: A **compound term** is a term of the form f(t₁, t₂, ..., tₙ) where:
- f ∈ F with ar(f) = n > 0 (the principal functor)
- t₁, t₂, ..., tₙ ∈ T(F,V) (the arguments)

**Structural Properties**:
- **Principal functor**: f
- **Arity**: n = ar(f)
- **Arguments**: (t₁, t₂, ..., tₙ)
- **Subterms**: All tᵢ and their subterms

**Recursive Structure**: Arguments can themselves be compound terms, enabling nested composition.

**Examples**:
```clojure
person("John", 30)           ; functor: person/2, args: ["John", 30]
address("Main St", 123, "NY") ; functor: address/3, args: ["Main St", 123, "NY"]
parent(person("Alice"), person("Bob")) ; nested compound terms
```

**Unification Rule**: Two compound terms f(s₁, ..., sₙ) and g(t₁, ..., tₘ) unify iff:
1. f = g (same functor)
2. n = m (same arity)  
3. sᵢ and tᵢ unify for all i ∈ {1, ..., n}

## 3. Data Structure Integration

### 3.1 Clojure Data Structure Mapping

Tipster terms provide seamless integration with Clojure data structures through a formal mapping function:

**Mapping Function**: φ: ClojureData → T(F,V)

| Clojure Type | Tipster Term | Formal Mapping |
|--------------|--------------|----------------|
| Symbol starting with `?` | Variable | φ(?X) = X ∈ V |
| Other symbols | Atom | φ(s) = s ∈ F, ar(s) = 0 |
| Lists | Compound | φ((f a₁ ... aₙ)) = f(φ(a₁), ..., φ(aₙ)) |
| Vectors | Compound | φ([a₁ ... aₙ]) = vector(φ(a₁), ..., φ(aₙ)) |
| Primitives | Atom | φ(c) = c ∈ F, ar(c) = 0 |

### 3.2 Bidirectional Conversion

The terms module provides bidirectional conversion with inverse mapping:

**Inverse Mapping**: φ⁻¹: T(F,V) → ClojureData

**Properties**:
- **Homomorphism**: φ preserves term structure
- **Invertibility**: φ⁻¹(φ(d)) ≈ d for well-formed Clojure data
- **Type preservation**: Semantic meaning is preserved during conversion

## 4. Functional Requirements

### 4.1 Term Creation
- **R1**: Create variables v ∈ V with unique identifiers
- **R2**: Create atoms a ∈ F with ar(a) = 0 from any Clojure value
- **R3**: Create compound terms f(t₁, ..., tₙ) with f ∈ F, ar(f) = n > 0
- **R4**: Implement mapping φ: ClojureData → T(F,V)

### 4.2 Term Inspection
- **R5**: Determine term type: variable, atom, or compound
- **R6**: Extract functor and arguments from compound terms
- **R7**: Implement predicates: is-variable?, is-compound?

### 4.3 Type Conversion
- **R8**: Implement φ: ClojureData → T(F,V)
- **R9**: Implement φ⁻¹: T(F,V) → ClojureData
- **R10**: Handle nested structures recursively
- **R11**: Preserve semantic meaning during conversion

### 4.4 Integration Support
- **R12**: Support variable dereferencing with substitutions
- **R13**: Integration with unification algorithms
- **R14**: Efficient representation for pattern matching

## 5. Usage Patterns

### 5.1 Basic Term Creation
```clojure
;; Creating terms explicitly
(make-variable "X")           ; Creates X ∈ V
(make-atom 'john)            ; Creates john ∈ F, ar(john) = 0
(make-compound 'person 'john 30) ; Creates person(john, 30)

;; Automatic conversion via φ
(clojure-term->tipster-term '?X)        ; φ(?X) = X ∈ V
(clojure-term->tipster-term 'john)      ; φ(john) = john ∈ F  
(clojure-term->tipster-term '(f a b))   ; φ((f a b)) = f(a, b)
```

### 5.2 Pattern Matching
```clojure
;; Pattern with variables
(clojure-term->tipster-term '(person ?name ?age))
;; → person(X, Y) where X, Y ∈ V

;; Unification target
(clojure-term->tipster-term '(person john 30))
;; → person(john, 30)

;; Unification: σ = {X ↦ john, Y ↦ 30}
```

### 5.3 Structure Decomposition
```clojure
;; Complex nested structure
(clojure-term->tipster-term 
  '(employee (person ?name ?age) 
             (department ?dept ?manager)))
;; → employee(person(X, Y), department(Z, W))

;; Can unify with:
'(employee (person "Alice" 25) 
           (department "IT" "Bob"))
;; → employee(person("Alice", 25), department("IT", "Bob"))
```

## 6. Design Principles

### 6.1 Mathematical Rigor
All operations are based on formal mathematical definitions from term algebra and unification theory.

### 6.2 Compositionality  
Complex terms are built from simpler terms following the recursive definition of T(F,V).

### 6.3 Transparency
Seamless integration with Clojure data structures through formal mapping functions.

### 6.4 Efficiency
Efficient representation using Clojure records and protocols for optimal performance.

### 6.5 Extensibility
Protocol-based design allows for future extensions while maintaining mathematical foundations.
