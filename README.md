# Tipster — Functional-Logical Programming Language with Dual Semantics

[![Clojure](https://img.shields.io/badge/Clojure-1.12+-blue.svg)](https://clojure.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-0.0.3--dev-orange.svg)](docs/en/general/roadmap.md)

---

**Tipster** is an innovative programming language that unifies functional and logical programming through **dual semantics**. Every expression can be both a computation (like Clojure) and a logical fact (like Prolog), enabling flexible development of intelligent systems, knowledge bases, and complex business logic.

## 🎯 What Makes Tipster Unique?

**Dual Semantics Innovation:**
```clojure
;; Same expression, dual interpretation:
(employee "John" 30 "IT" 75000)

;; As function (computational semantics):
(employee "John" 30 "IT" 75000)  ; → creates employee record

;; As logical pattern (logical semantics):
(employee ?name ?age "IT" ?salary)  ; → finds all IT employees

;; Combined power:
(->> (employee ?name ?age "IT" ?salary)
     (filter #(> (:age %) 25))
     (map #(calculate-bonus (:salary %))))
```

**No artificial boundaries** between data, functions, rules, and queries — everything lives in a unified knowledge space.

---

## 🚨 Current Status: v0.0.3 - Early Development

**This is early development stage** implementing basic logical inference capabilities. The current version provides initial foundation for logic programming, with v0.1.0 MVP and full dual semantics vision coming in future releases.

**✅ What works now (v0.0.3):**
- Core unification algorithm with occurs check
- Variable binding and logical inference
- Facts, rules, and pattern matching queries
- 6 interactive examples with comprehensive documentation
- Full bilingual support (English/Russian)
- REPL-friendly development environment

**🚀 Full vision roadmap:** [docs/en/general/roadmap.md](docs/en/general/roadmap.md) | [docs/ru/general/roadmap.md](docs/ru/general/roadmap.md)

---

## 🌍 Bilingual Project

Full support for **English** and **Russian**:

- **English**: You're reading it! 🇺🇸  
- **Русский**: [ru/README.md](ru/README.md) 🇷🇺

Use `--lang ru` for Russian interface in all tools.

---

## 🚀 Quick Start (30 seconds)

**Experience logic programming capabilities:**

```bash
git clone <this-repo>
cd tipster
./start.sh
```

This launches interactive examples showing logical inference in action!

**Step-by-step learning:**
- English: [examples/README.md](examples/README.md)
- Russian: [ru/QUICKSTART.md](ru/QUICKSTART.md)

---

## 💡 Why Tipster?

Born from real enterprise needs — integrating diverse business systems and automating complex processes. Tipster eliminates the "impedance mismatch" between different programming paradigms and data models.

**Core Philosophy:**
- **Unified Knowledge Space**: Facts, rules, functions, and data coexist naturally
- **Dual Semantics**: Every expression works as both computation and logical pattern
- **Zero Friction**: Native Clojure syntax, no DSLs or ORMs
- **Seamless Integration**: Full JVM ecosystem compatibility

---

## 🎨 Key Features

### 🔄 Dual Semantics
Every predicate expression serves dual purposes without special syntax:
```clojure
;; Define once, use everywhere:
(defn customer [name id orders])

;; Computational use:
(customer "Alice" 123 [...])

;; Logical queries:
(customer ?name ?id ?orders)
```

### 🧠 Unified Knowledge Space (PKVT)
All knowledge stored in Parent-Key-Value-Type tuples:
```clojure
{:parent "fact-1" :key "employee" :value ["John" 30 "IT"] :type :fact}
{:parent "rule-1" :key "senior" :value "..." :type :rule}
{:parent "fn-1" :key "bonus" :value "..." :type :function}
```

### 🔍 Universal Search
Same combinatorial search engine for:
- Logical inference
- Data queries  
- Function calls
- Pattern matching

### 🌐 Natural Aggregations
Aggregations emerge naturally from logical inference:
```clojure
;; Logical inference finds solutions:
(->> (employee ?name ?age ?dept ?salary)
     (filter #(> (:age %) 30))
     (group-by :dept)  ; Natural grouping
     (map #(reduce + (map :salary %))))  ; Standard functions
```

---

## 📝 Current Code Example (v0.0.3)

```clojure
;; Facts about family relationships
(deffact (person alice))
(deffact (person bob))
(deffact (parent alice bob))

;; Logical rules
(defrule (grandparent ?X ?Z) 
         [(parent ?X ?Y) (parent ?Y ?Z)])

;; Queries - automatic logical inference!
(query (person ?who))        ; → Find all persons
(query (grandparent ?x ?y))  ; → Find grandparent relationships
```

> **⚠️ Evolution Note:** The `deffact`/`defrule`/`query` macros are temporary v0.0.x constructs for early development. Starting v0.1.0 MVP, these will evolve toward dual semantics, with full implementation in v0.2.0+ where any expression naturally serves both computational and logical purposes.

---

## 🎓 Learning & Examples

**Interactive Learning:**
```bash
./scripts/run-example.sh all     # Complete tour
./scripts/run-example.sh basic   # Facts and queries
./scripts/run-example.sh family  # Relationships
./scripts/run-example.sh rules   # Logical inference
```

**Experimentation:**
```bash
./scripts/repl.sh               # Interactive REPL
./scripts/debug.sh              # Debug mode
```

**Learning Resources:**
- [examples/README.md](examples/README.md) - Comprehensive examples
- [ru/QUICKSTART.md](ru/QUICKSTART.md) - Quick start guide (Russian)

---

## 🏗️ Architecture Evolution

### Current (v0.0.3): Early Development
- ✅ Basic unification algorithm
- ✅ Simple variable binding  
- ✅ Minimal knowledge base
- ✅ Basic pattern matching

### Near Future (v0.1.0): MVP Logic Foundation
- 🚀 Complete unification algorithm
- 🚀 Robust variable binding system
- 🚀 Comprehensive knowledge base
- 🚀 Advanced pattern matching

### Future (v0.2.0+): Dual Semantics
- 🚀 Universal combinatorial search engines
- 🚀 Modular search strategies (backtrack, heuristic, breadth-first)
- 🚀 True dual semantics without special macros

### Full Vision (v0.3.0+): Unified Knowledge Platform
- 🌟 PKVT knowledge space (1B+ objects)
- 🌟 Transparent external data integration
- 🌟 Distributed combinatorial machines
- 🌟 Enterprise-ready platform

---

## 🆚 Comparison

|                    | Prolog      | SQL/NoSQL    | Tipster                |
|--------------------|-------------|--------------|------------------------|
| **Paradigm**       | Logic only  | Data only    | Logic + Functional     |
| **Syntax**         | Prolog      | SQL/JSON     | Pure Clojure/EDN       |
| **Data Model**     | Facts/Rules | Tables/Docs  | Unified PKVT Space     |
| **Queries**        | Logic       | Declarative  | Dual Semantics         |
| **Integration**    | Limited     | ORM/APIs     | Native JVM             |
| **Aggregations**   | Manual      | Built-in     | Natural from inference |
| **Learning Curve** | Steep       | Medium       | Gentle (if know Clojure)|

**Tipster = Best of all worlds, unified.**

---

## 📚 Documentation

> **📋 Centralized Navigation:** Complete documentation is organized into **general**, **concepts**, and **terms** sections with easy navigation via the [Documentation Portal](docs/en/README.md).

### English
- [📚 **Complete Documentation Portal**](docs/en/README.md) - All documentation with easy navigation
- [📖 General Description](docs/en/general/general_description.md) - Project overview and vision
- [🏗️ Architecture](docs/en/general/architecture.md) - Technical deep dive  
- [🗺️ Roadmap](docs/en/general/roadmap.md) - Development plan
- [🔧 Technical Specs](docs/en/terms/) - Core concepts, algorithms, and specifications
- [💡 Examples](examples/README.md) - Hands-on learning

### Русский  
- [📚 **Портал документации**](docs/ru/README.md) - Вся документация с удобной навигацией
- [📖 Общее описание](docs/ru/general/general_description.md) - Обзор проекта и видение
- [🏗️ Архитектура](docs/ru/general/architecture.md) - Техническое описание
- [🗺️ Дорожная карта](docs/ru/general/roadmap.md) - План развития
- [💡 Примеры](ru/QUICKSTART.md) - Практическое изучение

---

## 🤝 Contributing

**Open Source & Community Driven:**
- 📝 Issues and discussions welcome
- 🔧 Pull requests for features and fixes
- 🌟 Help shape the future of programming languages
- 📖 Documentation and examples contributions

**Areas needing help:**
- Core engine optimizations
- Additional examples and tutorials
- Integration adapters
- Performance benchmarking

---

## 📄 License

MIT License - freely use, modify, and distribute.

---

## 📞 Contact & Community

- **Issues & Discussions**: [GitHub Issues](https://github.com/LMikhail/Tipster/issues)
- **Enterprise & Partnerships**: Contact via GitHub profile
- **Community**: Join the evolution in programming languages!

---

> **Tipster** — Where logic meets computation, and knowledge becomes code. 🚀
