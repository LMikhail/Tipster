#!/bin/bash

# Просмотр статуса проекта Tipster
echo "📊 Tipster Logic Engine Project Status"
echo "======================================"

echo ""
echo "📁 Project Structure:"
echo "├── src/tipster/core.clj      - Main logic engine"
echo "├── src/tipster/bench.clj     - Performance benchmarks"
echo "├── test/tipster/core_test.clj - Test suite"
echo "└── scripts/                  - Build & utility scripts"

echo ""
echo "🔧 Available Scripts:"
echo "├── ./scripts/demo.sh         - Run demonstration"
echo "├── ./scripts/test.sh         - Run tests"
echo "├── ./scripts/test-watch.sh   - Run tests in watch mode"
echo "├── ./scripts/repl.sh         - Start interactive REPL"
echo "├── ./scripts/bench.sh        - Run performance benchmarks"
echo "├── ./scripts/debug.sh        - Debug mode with detailed output"
echo "├── ./scripts/coverage.sh     - Run tests with coverage"
echo "├── ./scripts/format.sh       - Format code"
echo "├── ./scripts/lint.sh         - Lint code"
echo "└── ./scripts/ci.sh           - Full CI pipeline"

echo ""
echo "📈 Quick Test Status:"
clj -M:kaocha --reporter kaocha.report/summary | tail -1

echo ""
echo "💡 Quick Start:"
echo "1. Run demo:     ./scripts/demo.sh"
echo "2. Run tests:    ./scripts/test.sh"
echo "3. Start REPL:   ./scripts/repl.sh"
echo "4. Full CI:      ./scripts/ci.sh"

echo ""
echo "🏗️  Architecture: Functional-Logic Programming Engine"
echo "🔗 Integration: Native Clojure with logic programming"
echo "📚 Features: Unification, Facts, Rules, Queries" 
