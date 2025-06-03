#!/bin/bash

# Запуск тестов с анализом покрытия кода
echo "Running Tipster Logic Engine tests with code coverage..."
echo "Results will be saved to target/coverage/"
echo "======================================================="

# Запуск тестов с coverage
clj -M:coverage

echo ""
echo "Coverage report generated in target/coverage/"
echo "Open target/coverage/index.html in your browser to view the report." 
