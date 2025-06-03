#!/bin/bash

# Запуск REPL с предзагруженным Tipster логическим движком
echo "Starting Clojure REPL with Tipster logic engine loaded..."
echo "Available functions:"
echo "  (tipster/demo-tipster)              - Run demonstration"
echo "  (tipster/reset-tipster!)            - Reset knowledge base"
echo "  (tipster/deffact (human alice))     - Add facts"
echo "  (tipster/query (human ?X))          - Query knowledge base"
echo "  @tipster/knowledge-base             - View knowledge base"
echo ""
echo "Testing functions:"
echo "  (require 'tipster.core-test)        - Load tests"
echo "  (run-tests 'tipster.core-test)      - Run all tests"
echo "  (test-unify-compound-terms)         - Run specific test"
echo ""

clj -M -e "(require '[tipster.core :as tipster]) (in-ns 'user)" 
