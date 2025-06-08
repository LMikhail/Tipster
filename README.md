# Tipster — A Functional-Logical Programming Language and Universal DBMS

[![Clojure](https://img.shields.io/badge/Clojure-1.11+-blue.svg)](https://clojure.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-0.1.0-orange.svg)](VERSION.md)

---

**Tipster** is a next-generation language and open programming environment that combines the declarative power of logic programming languages (in the spirit of Prolog) with the flexibility and expressiveness of Clojure/EDN. The project is being developed as a universal open-source platform for building complex business systems, modern web services, expert and intelligent systems on a unified technological foundation.

## 🚨 Current Status: v0.1.0 - MVP Logic Engine

**This is the first working release** of the ambitious Tipster vision. While the current implementation provides a solid foundation for logic programming in Clojure, it represents the initial step toward the full platform described in our [documentation](docs/eng/general_description.md).

**✅ What works now (v0.1.0):**
- Core logic engine with unification and variable binding
- Basic facts, rules, and queries with pattern matching  
- 6 interactive examples demonstrating key concepts
- Comprehensive test suite (60 tests, 177 assertions)
- Full bilingual support (English/Russian)
- REPL-friendly development environment

**🚀 Full vision roadmap:** See [VERSION.md](VERSION.md) for detailed development plans through v2.0.0.

---

## 🌍 Language Support

This project supports both **English** (default) and **Russian**:

- **English documentation**: You're reading it! 🇺🇸
- **Русская документация**: [ru/README.md](ru/README.md) 🇷🇺

Use `--lang ru` flag for Russian interface in examples and tools.

---

## 🚀 Quick Start (30 seconds)

**New to logic programming?** Jump right in:

```bash
git clone <this-repo>
cd tipster
./start.sh
```

This runs interactive examples showing facts, queries, and logical rules in action!

**For step-by-step learning:**
- English: `cat examples/README.md`
- Russian: `cat ru/QUICKSTART.md` 

---

## Why Tipster?

Tipster was born out of a real engineering need — automating and integrating diverse business processes and accounting systems in a large enterprise with many subsystems. It solves the conceptual problems of automation and lets you focus on business logic itself, not on "gluing together" incompatible solutions.

**Tipster's core principles:**

* Preserves the full functionality of Clojure and its standard library.
* Allows you to create logic rules and knowledge bases using native EDN/Clojure syntax.
* Makes logic inference, knowledge management, and data operations "first-class citizens" of the ecosystem.

---

## Key Features

* **Dual Semantics:** Every expression can be both a computation (as in Clojure) and a logical fact (as in Prolog).
* **Unified Syntax** for logic and functional tasks: no need to write a separate DSL for logic.
* **Seamless integration with Clojure/JVM:** Use any existing libraries and tools.
* **Flexible Architecture:** Works as in-memory for prototyping and REPL, as well as with external databases for enterprise tasks (future versions).
* **No "impedance mismatch":** Database operations are as close as possible to the logic of the language, with no bulky ORMs or intermediate layers.
* **Scalability:** Suitable for small projects as well as distributed systems with large knowledge bases.

**Future features (roadmap):**
* **Powerful runtime DBMS:** Knowledge storage and search based on the 4-tuple model (Parent-Key-Value-Type, PKVT) - *v0.3.0+*
* **Advanced query system:** SQL-like syntax with optimization - *v0.4.0+*
* **REST API and web interface:** Full platform capabilities - *v1.0.0+*

---

## Code Example (Current v0.1.0)

```clojure
;; Facts
(deffact (person alice))
(deffact (parent alice bob))
(deffact (age alice 45))

;; Rules
(defrule (grandparent ?X ?Z) 
         [(parent ?X ?Y) (parent ?Y ?Z)])

;; Queries - system finds answers automatically!
(query (person ?Who))        ; Find all persons
(query (grandparent ?X ?Y))  ; Find grandparent relationships
```

> **⚠️ Note:** The `deffact`, `defrule`, and `query` macros are **temporary constructs** introduced in v0.1.0 to simplify sequential logic development. In future versions, these will be removed in favor of full Clojure integration with **dual semantics**, where any predicate expression can serve as both computation and logical fact without special syntax.

Queries to data and logic look the same — you always work with familiar Clojure/EDN syntax and get results as data structures.

---

## 📚 Examples and Learning

**Interactive Examples:** Learn by doing with guided examples:
```bash
./scripts/run-example.sh all    # All examples
./scripts/run-example.sh basic  # Facts and queries
./scripts/run-example.sh family # Family relationships
./scripts/run-example.sh rules  # Logical inference
```

**Experiment:** Use the interactive REPL:
```bash
./scripts/repl.sh              # Interactive experimentation
```

**Documentation:**
- `examples/README.md` - Detailed examples guide
- `ru/QUICKSTART.md` - Quick start in Russian
- `src/tipster/` - Source code with documentation

---

## Current Architecture (v0.1.0)

**What's implemented:**
* **Core Logic Engine:** Unification algorithm, variable binding, basic knowledge base
* **Query System:** Pattern matching and simple rule evaluation
* **REPL Integration:** Interactive development and testing
* **Bilingual Support:** English/Russian interface

**Future Architecture (Full Vision):**
* **Tipster Compiler:** Parsing, analysis, and code generation for JVM/JS, REPL support, API, hot-reloading of knowledge
* **Runtime Server:** Knowledge base (in-memory/persistent), logic engine, indexing, optimization, integration with external sources (SQL/NoSQL), visualization and audit tools

See [VERSION.md](VERSION.md) for detailed roadmap.

---

## Why Not Just Prolog or SQL?

|                | Prolog    | SQL            | Tipster                    |
| -------------- | --------- | -------------- | -------------------------- |
| Paradigm       | Logic     | Declarative DB | Logic + Functional         |
| Syntax         | Specific  | Tabular        | Clojure/EDN                |
| Extensibility  | Difficult | Only via SQL   | JVM/JS/CLR, any libraries  |
| I/O            | Limited   | DB-only        | Directly in the language   |
| Modularity     | Primitive | DB Schemas     | Namespaces, modules        |
| Learning curve | High      | Medium         | Low for Clojure developers |

Tipster combines the advantages of all these worlds and minimizes their shortcomings.

---

## Documentation

* [**General Description and Architecture**](docs/eng/general_description.md)
* [Code Examples](examples/)
* [Version History and Roadmap](VERSION.md)

---

## How to Contribute?

The project is open to everyone:

* Public repository with contributions via Pull Requests
* Discussions, new modules and integrations are welcome!
* Contributors help develop the standard library, tools, and drivers.

---

## License

Tipster is released under the MIT license. All components and source code are freely available for use and modification.

---

## Contact & Support

* Questions, suggestions, and bug reports — via [Issues](https://github.com/LMikhail/Tipster/issues)
* For partnerships and large-scale deployments — contact via GitHub profile.

---

> **Tipster** — your tool for flexible automation, building expert systems, and knowledge management for the 21st century!
