# Tipster - Quick Start 🚀

Welcome to the world of logic programming with Tipster! This guide will help you learn facts, rules, and queries in just 10 minutes.

## 🎯 What You'll Learn

- Logic programming fundamentals
- Creating facts and rules
- Queries with variables
- Practical application examples

## 📋 Requirements

- Java 8+ 
- Clojure 1.11+
- Git (for cloning)

## ⚡ Quick Installation

```bash
# Clone the repository
git clone <repository-url>
cd tipster

# Run your first example
./scripts/run-example.sh basic
```

## 🔹 Step 1: First Facts

Open REPL:

```bash
clj
```

Create facts about people:

```clojure
(require '[tipster.core :as tipster])

;; Clear the system
(tipster/reset-tipster!)

;; Add facts
(tipster/deffact (person alice))
(tipster/deffact (person bob))
(tipster/deffact (profession alice programmer))
```

## 🔍 Step 2: First Queries

Now let's query the system:

```clojure
;; Who is a person?
(tipster/query (person ?X))
;; => ((person alice) (person bob))

;; What's Alice's profession?
(tipster/query (profession alice ?Profession))
;; => ((profession alice programmer))
```

## ⚖️ Step 3: Logical Inference Rules

Create a rule:

```clojure
;; If someone is a person and has a profession, then they work
(tipster/defrule (works ?X) 
                 [(person ?X) (profession ?X ?Prof)])

;; Test the rule
(tipster/query (works ?Who))
;; => ((works alice))
```

## 👨‍👩‍👧‍👦 Step 4: Family Relationships

Create a family relationship system:

```clojure
(tipster/reset-tipster!)

;; Family facts
(tipster/deffact (parent alice bob))
(tipster/deffact (parent bob charlie))
(tipster/deffact (male bob))
(tipster/deffact (female alice))

;; Rule: grandfather
(tipster/defrule (grandfather ?X ?Z) 
                 [(parent ?X ?Y) (parent ?Y ?Z) (male ?X)])

;; Who is a grandfather?
(tipster/query (grandfather ?Who ?ToWhom))
```

## 🎮 Step 5: Interactive Examples

Try the ready-made examples:

```bash
# Basics
./scripts/run-example.sh basic

# Family relationships
./scripts/run-example.sh family

# Rules and inference
./scripts/run-example.sh rules

# Animal classification
./scripts/run-example.sh animals

# Mathematical relations
./scripts/run-example.sh math

# Interactive mode
./scripts/run-example.sh interactive

# All examples in sequence
./scripts/run-example.sh all
```

## 🧪 Step 6: Testing

Make sure everything works:

```bash
# Run all tests
clj -M:test

# Tests in Russian
clj -M:test -e "(require '[tipster.all-tests :as t]) (t/run-all-tipster-tests :lang :ru)"

# Quick tests
clj -M:test -e "(require '[tipster.all-tests :as t]) (t/run-quick-tests)"
```

## 💡 Core Concepts

### 📝 Facts
Simple statements about the world:
```clojure
(tipster/deffact (person alice))
(tipster/deffact (likes alice programming))
```

### ⚖️ Rules
Logical inferences:
```clojure
(tipster/defrule (happy ?X) 
                 [(person ?X) (likes ?X programming)])
```

### ❓ Queries
Questions with variables:
```clojure
(tipster/query (happy ?Who))  ; Who is happy?
```

### 🔗 Unification
Pattern matching:
```clojure
(tipster/query (likes ?Who programming))  ; Who likes programming?
```

## 🎯 Practical Applications

### Expert Systems
```clojure
;; Medical diagnosis
(tipster/deffact (symptom patient1 fever))
(tipster/deffact (symptom patient1 cough))
(tipster/defrule (diagnosis ?P cold) 
                 [(symptom ?P fever) (symptom ?P cough)])
```

### Recommendation Systems
```clojure
;; Movie recommendations
(tipster/deffact (likes user1 sci-fi))
(tipster/deffact (genre movie1 sci-fi))
(tipster/defrule (recommend ?U ?M) 
                 [(likes ?U ?G) (genre ?M ?G)])
```

### Planning
```clojure
;; Task planning
(tipster/deffact (depends task2 task1))
(tipster/defrule (execute-first ?T1) 
                 [(depends ?T2 ?T1)])
```

## 🚀 Next Steps

1. **Study examples**: `./scripts/run-example.sh list`
2. **Read documentation**: `examples/README.md`
3. **Explore source code**: `src/tipster/`
4. **Join community**: Contribute to the project!

## 🌍 Language Support

This guide is available in multiple languages:
- **English**: `QUICKSTART.md` (you're reading it)
- **Russian**: `ru/QUICKSTART.md`

All examples and tests support both languages via `--lang ru` parameter.

## 🤝 Need Help?

- **Examples guide**: `examples/README.md`
- **Testing guide**: `test/TESTING.md`
- **Russian documentation**: `ru/` directory
- **Source code**: `src/tipster/`

---

**Start right now: `./scripts/run-example.sh all` 🚀**
