# Tipster — A Functional-Logical Programming Language and Universal DBMS

---

**Tipster** is a next-generation language and open programming environment that combines the declarative power of logic programming languages (in the spirit of Prolog) with the flexibility and expressiveness of Clojure/EDN. The project is being developed as a universal open-source platform for building complex business systems, modern web services, expert and intelligent systems on a unified technological foundation.

---

## 🚀 Quick Start (30 seconds)

**New to logic programming?** Jump right in:

```bash
git clone <this-repo>
cd tipster
./start.sh
```

This will run interactive examples showing facts, queries, and logical rules in action!

**For step-by-step learning:**
- **Russian:** `cat QUICKSTART.md` 
- **English:** `cat examples/README.md`

---

## Why Tipster?

Tipster was born out of a real engineering need — automating and integrating diverse business processes and accounting systems in a large enterprise with many subsystems. It solves the conceptual problems of automation and lets you focus on business logic itself, not on "gluing together" incompatible solutions.

**Tipster:**

* Preserves the full functionality of Clojure and its standard library.
* Allows you to create logic rules and knowledge bases using native EDN/Clojure syntax.
* Makes logic inference, knowledge management, and data operations "first-class citizens" of the ecosystem.

---

## Key Features

* **Dual Semantics:** Every expression can be both a computation (as in Clojure) and a logical fact (as in Prolog).
* **Unified Syntax** for logic and functional tasks: no need to write a separate DSL for logic.
* **Seamless integration with Clojure/JVM:** Use any existing libraries and tools.
* **Powerful runtime DBMS:** Knowledge storage and search based on the 4-tuple model (Parent-Key-Value-Type, PKVT), allowing you to build structures of any complexity without rigid schemas.
* **Flexible Architecture:** Works as in-memory for prototyping and REPL, as well as with external databases for enterprise tasks.
* **No "impedance mismatch":** Database operations are as close as possible to the logic of the language, with no bulky ORMs or intermediate layers.
* **Scalability:** Suitable for small projects as well as distributed systems with large knowledge bases.

---

## Code Example

```clojure
;; Facts
(deffact (person alice))
(deffact (parent alice bob))

;; Rules
(defrule (grandparent ?X ?Z) 
         [(parent ?X ?Y) (parent ?Y ?Z)])

;; Queries - system finds answers automatically!
(query (grandparent ?X ?Y))  ; Who are the grandparents?
```

Queries to data and logic look the same — you always work with familiar Clojure/EDN syntax and get results as data structures.

---

## 📚 Examples and Learning

**Interactive Examples:** Learn by doing with guided examples:
```bash
./scripts/examples.sh          # All examples
./scripts/examples/basic.sh     # Facts and queries
./scripts/examples/family.sh    # Relationships
./scripts/examples/rules.sh     # Logical inference
```

**Experiment:** Use the interactive REPL:
```bash
./scripts/repl.sh              # Interactive experimentation
```

**Documentation:**
- `examples/README.md` - Detailed examples guide
- `QUICKSTART.md` - Quick start in Russian
- `src/tipster/` - Source code with documentation

---

## Architecture

* **Tipster Compiler:** Parsing, analysis, and code generation for JVM/JS, REPL support, API, hot-reloading of knowledge.
* **Runtime Server:** Knowledge base (in-memory/persistent), logic engine, indexing, optimization, integration with external sources (SQL/NoSQL), visualization and audit tools.

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
* [Architecture & API](docs/eng/architecture.md)
* [Quick Start (Russian)](QUICKSTART.md)

---

## How to Contribute?

The project is open to everyone:

* Public repository and [official website](https://github.com/LMikhail/Tipster)
* Pull Requests, discussions, new modules and integrations are welcome!
* Contributors help develop the standard library, tools, and drivers.

---

## License

Tipster is released under the MIT license. All components and source code are freely available for use and modification.

---

## Contact & Support

* Questions, suggestions, and bug reports — via [Issues](https://github.com/LMikhail/Tipster/issues)
* For partnerships and large-scale deployments — write to the e-mail listed in the GitHub profile.

---

> **Tipster** — your tool for flexible automation, building expert systems, and knowledge management for the 21st century!
