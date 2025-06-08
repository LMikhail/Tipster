# 🚀 Tipster Examples - Logic Engine for Clojure

Welcome to the Tipster examples collection! These examples will show you how to use logic programming in Clojure to solve real-world problems.

## 🎯 Quick Start

**Run all examples at once:**
```bash
./scripts/run-example.sh all
```

**Run individual example:**
```bash
./scripts/run-example.sh basic      # Basics
./scripts/run-example.sh family     # Family relationships
./scripts/run-example.sh rules      # Rules and inference
./scripts/run-example.sh animals    # Classification
./scripts/run-example.sh math       # Mathematics
./scripts/run-example.sh interactive # Interactive mode
```

**List available examples:**
```bash
./scripts/run-example.sh list
```

## 📚 Examples Description

### 🔹 Example 1: Basics
**What you'll learn:** Facts and simple queries

This example will show you:
- How to add facts using `deffact`
- How to make queries using `query`
- Using variables `?X`, `?Y`
- Basic Tipster syntax

```clojure
;; Adding facts
(deffact (person alice))
(deffact (profession bob teacher))

;; Making queries
(query (person ?X))           ; Who is a person?
(query (profession ?Who ?What)) ; Who has what profession?
```

### 👨‍👩‍👧‍👦 Example 2: Family Relationships
**What you'll learn:** Connections between data

You'll learn how to:
- Create linked facts
- Use compound predicates
- Make queries with multiple variables
- Model real-world relationships

```clojure
(deffact (parent alice bob))
(deffact (male bob))
(query (parent ?X ?Y))  ; Who is whose parent?
```

### 🧠 Example 3: Rules and Logical Inference
**What you'll learn:** The power of logic programming

The most important part! You'll study:
- Creating rules with `defrule`
- Logical inference of new facts
- Rule chains and derived rules
- Conditions in rules

```clojure
;; Define a rule: grandfather is a male
;; who is a parent of a parent
(defrule (grandfather ?X ?Z) 
         [(parent ?X ?Y) (parent ?Y ?Z) (male ?X)])

(query (grandfather ?X ?Y))  ; System automatically infers grandfathers!
```

### 🐱 Example 4: Animal Classification
**What you'll learn:** Classification systems

We'll build an expert system:
- Hierarchical rules
- Automatic categorization
- Expert knowledge systems
- Taxonomies and classifications

```clojure
(defrule (mammal ?X) 
         [(animal ?X) (has-fur ?X)])
         
(defrule (domestic ?X) 
         [(mammal ?X)])
```

### 🔢 Example 5: Mathematical Relationships
**What you'll learn:** Numerical logic

Working with numerical data:
- Mathematical relationships
- Number classification rules
- Logical computations
- Formal systems

```clojure
(deffact (greater 3 2))
(defrule (even 2) [])
(defrule (positive ?X) [(number ?X) (greater ?X 0)])
```

### 🎓 Example 6: Interactive Mode
**What you'll learn:** Complex knowledge systems

Advanced example:
- Complex knowledge bases
- Multi-level queries
- Practical applications
- Readiness to create your own systems

```clojure
(defrule (excellent-student ?Student) 
         [(student ?Student) (grade ?Student ?Subject 5)])
```

## 🛠️ Core Tipster Concepts

### Facts
Facts are basic statements about the world:
```clojure
(deffact (person alice))            ; Simple fact
(deffact (parent alice bob))        ; Relationship
(deffact (age alice 30))            ; Fact with number
```

### Queries
Queries allow you to search for information:
```clojure
(query (person ?X))                 ; Who is a person?
(query (parent ?X bob))             ; Who is Bob's parent?
(query (age ?Who ?HowOld))          ; Who is how old?
```

### Rules
Rules derive new facts from existing ones:
```clojure
(defrule (grandfather ?X ?Z) 
         [(parent ?X ?Y) (parent ?Y ?Z) (male ?X)])
```
This means: "X is grandfather of Z if X is parent of Y, Y is parent of Z, and X is male"

### Variables
- `?X`, `?Y`, `?Z` - variables in queries and rules
- Can bind to any values
- Allow creating general patterns

## 🎮 Interactive Experimentation

After studying the examples, try:

1. **REPL for experiments:**
   ```bash
   ./scripts/repl.sh
   ```

2. **Create your own facts:**
   ```clojure
   (require '[tipster.core :as t])
   (t/reset-tipster!)
   (t/deffact (my-fact data))
   (t/query (my-fact ?X))
   ```

3. **Try your own rules:**
   ```clojure
   (t/defrule (my-rule ?X) [(condition ?X)])
   ```

## 🚀 Next Steps

1. **Run all examples** in order
2. **Experiment** in REPL
3. **Study source code** in `src/tipster/`
4. **Create your own expert system**

## 📖 Additional Resources

- **Documentation:** `src/tipster/TIPSTER_README.md`
- **Source code:** `src/tipster/`
- **Tests:** `test/tipster/core_test.clj`
- **Benchmarks:** `src/tipster/bench.clj`

## 🤝 For Developers

If you want to dive deeper into development:

```bash
./scripts/test.sh      # Run tests
./scripts/lint.sh      # Code checking
./scripts/bench.sh     # Benchmarks
./scripts/ci.sh        # Full verification
```

---

**Enjoy learning logic programming with Tipster! 🎉**
