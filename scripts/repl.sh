#!/bin/bash

# Запуск REPL с предзагруженным WAM эмулятором
echo "Starting Clojure REPL with WAM emulator loaded..."
echo "Available functions:"
echo "  (wam/reset-wam!)                    - Reset WAM state"
echo "  (wam/run-wam)                       - Run current program"
echo "  @wam/X                              - View X registers"
echo "  @wam/heap                           - View heap"
echo "  @wam/program-code                   - View current program"
echo ""
echo "Testing functions:"
echo "  (require 'wam-emulator.core-test)   - Load tests"
echo "  (run-tests 'wam-emulator.core-test) - Run all tests"
echo "  (test-specific-function)            - Run specific test"
echo ""

clj -M -e "(require '[wam-emulator.core :as wam]) (in-ns 'user)" 
