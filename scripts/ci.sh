#!/bin/bash

# Скрипт для непрерывной интеграции (CI)
echo "Running CI pipeline for WAM Emulator..."
echo "======================================"

set -e  # Останавливаться при первой ошибке

echo ""
echo "1. Checking code formatting..."
./format.sh
if [ $? -ne 0 ]; then
    echo "❌ Code formatting check failed!"
    exit 1
fi
echo "✅ Code formatting OK"

echo ""
echo "2. Running linter..."
./lint.sh
if [ $? -ne 0 ]; then
    echo "❌ Linting failed!"
    exit 1
fi
echo "✅ Linting OK"

echo ""
echo "3. Running tests with coverage..."
./coverage.sh
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
echo "🎉 CI pipeline completed successfully!"
echo "Coverage report: target/coverage/index.html"
echo "Test results: target/junit.xml" 
