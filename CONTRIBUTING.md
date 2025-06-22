# Tipster Contribution Guide

We are excited about your interest in the Tipster project and welcome all contributions! To ensure a smooth and effective development process for everyone, please familiarize yourself with this guide.

## Project Philosophy

Tipster is an ambitious project aiming to create a reliable and high-performance platform. We strive for the highest quality in code, documentation, and user experience. Cleanliness, clarity, and predictability are our top priorities.

## Code Quality Standards

All code added to the project must adhere to the following standards:

1.  **Code Style:** We follow the widely accepted [Clojure Community Style Guide](https://guide.clojure.style/).
2.  **Linter:** Before committing code, you must check it with `clj-kondo`. This helps identify potential issues early. The linter configuration is included in the project.
3.  **Idiomatic Code:** Strive to write simple, clear, and idiomatic Clojure code. Avoid overly complex constructs if a simpler solution exists.

## Testing Strategy

We adhere to the principle that "untested code does not exist."

1.  **Test Coverage:** Any new functionality or bug fix must be accompanied by tests. We aim for 100% coverage for all critical logic.
2.  **Test Types:**
    *   **Unit Tests:** To verify individual functions and components in isolation.
    *   **Integration Tests:** To check the interaction between different parts of the system.
3.  **Running Tests:** Use the standard command to run all tests in the project to ensure your changes have not broken anything.

```bash
# Command to run tests (specify depending on the build tool)
lein test
```

## Documentation Policy

Code is only useful when it can be understood and used.

1.  **Docstrings:** All public functions (`def`, `defn`, `defmacro`) must have comprehensive docstrings explaining *what* the function does, what arguments it takes, and *what* it returns.
2.  **User Documentation:** If your changes affect user-visible behavior (e.g., changing the syntax or semantics of `defn::l`), you **must** update the relevant sections in the `/docs` folder.
3.  **Comments:** Use comments in the code only to explain *why* a particular non-trivial decision was made. *What* the code does should be clear from the code itself and the docstrings.

## Performance Work

Performance is a key feature of Tipster.

1.  **Benchmarks:** Any changes to performance-critical areas of the code (the unification core, indexing system, compiler) must be accompanied by benchmarks (e.g., using `criterium`) that demonstrate the impact of the changes.
2.  **Justification for Optimizations:** Do not apply premature optimizations. If you are optimizing code, be prepared to explain why it is necessary and how it affects overall performance.

## Pull Request Process

1.  Fork the repository.
2.  Create a new branch for your changes.
3.  Make your changes, adhering to all the standards described above.
4.  Ensure that all tests pass successfully.
5.  Submit a `Pull Request` to the main repository with a detailed description of the changes you have made.

## Contact

For questions about contributions or commercial use, please see [CONTACT.md](CONTACT.md).

## Authors

See [AUTHORS.md](AUTHORS.md) for information about project authors and contributors.

Thank you for your contribution to the development of Tipster! 
