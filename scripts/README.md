
```bash
chmod +x scripts/*.sh
ls -la scripts/
```

**📋 Complete list of scripts (toolkit):**

---

**Development:**

1. **`./scripts/test-watch.sh`** – automatic test rerun on changes
2. **`./scripts/repl.sh`** – interactive REPL
3. **`./scripts/debug.sh`** – debug mode with detailed output

---

**Testing:**

4. **`./scripts/test.sh`** – runs tests
5. **`./scripts/coverage.sh`** – analyzes code coverage by tests

---

**Demonstration:**

6. **`./scripts/demo.sh`** – demonstration
7. **`./scripts/status.sh`** – project status overview

---

**CI/CD:**

8. **`./scripts/ci.sh`** – full CI pipeline with tests, linting, coverage, and benchmarks
9. **`./scripts/format.sh`** – code formatting
10. **`./scripts/lint.sh`** – code linting

---

**Performance:**

11. `./scripts/bench.sh` – benchmarks
12. `./scripts/run.sh` – launch the main program

---

**Comments and Clarifications:**

* All scripts are located in the `scripts/` directory and should be granted execution rights.
* This toolkit allows you to automate most routine tasks related to development, testing, code formatting, and continuous integration.
* For detailed information on script parameters and advanced usage, refer to the documentation for each script or the project wiki, if available.
* Recommended to execute scripts via the shell for consistent environment behavior, especially in CI/CD pipelines.

---
