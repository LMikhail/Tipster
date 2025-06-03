#!/bin/bash

# Линтинг кода с clj-kondo
echo "Running clj-kondo linter..."
echo "=========================="

# Линтинг всего проекта
clj -M:lint --lint src test

echo ""
echo "Linting complete!"
echo "For more detailed output, run: clj -M:lint --lint src test --config .clj-kondo/config.edn" 
