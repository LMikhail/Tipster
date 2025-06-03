#!/bin/bash

# Скрипт для непрерывной интеграции (CI)
echo "Running CI pipeline for Tipster Logic Engine..."
echo "==============================================="

set -e  # Останавливаться при первой ошибке

echo ""
echo "1. Checking code formatting..."
./scripts/format.sh
if [ $? -ne 0 ]; then
    echo "❌ Code formatting check failed!"
    exit 1
fi
echo "✅ Code formatting OK"

echo ""
echo "2. Running linter..."
./scripts/lint.sh
if [ $? -ne 0 ]; then
    echo "❌ Linting failed!"
    exit 1
fi
echo "✅ Linting OK"

echo ""
echo "3. Running tests with coverage..."
./scripts/coverage.sh
if [ $? -ne 0 ]; then
    echo "❌ Tests failed!"
    exit 1
fi
echo "✅ Tests passed"

echo ""
echo "4. Checking for outdated dependencies..."
clj -M:outdated
echo "✅ Dependencies check complete"

echo ""
echo "5. Security vulnerability scan..."
clj -M:nvd
if [ $? -ne 0 ]; then
    echo "⚠️  Security scan completed with warnings"
else
    echo "✅ Security scan OK"
fi

echo ""
echo "6. Running performance benchmarks..."
echo "This may take a few minutes..."
./scripts/bench.sh
echo "✅ Benchmarks complete"

echo ""
echo "🎉 CI pipeline completed successfully!"
echo "Coverage report: target/coverage/index.html"
echo "Test results: target/junit.xml"
echo "Benchmark results logged above" 
