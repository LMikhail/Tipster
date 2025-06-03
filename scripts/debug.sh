#!/bin/bash

# Отладочный режим WAM эмулятора с детальным выводом
echo "WAM Emulator Debug Mode"
echo "======================="

echo ""
echo "Running simple test with detailed output:"
clj -M -e "
(require '[wam-emulator.core :as wam])
(wam/reset-wam!)
(reset! wam/program-code [[:put_constant 'a 0] [:get_constant 'a 0] [:proceed]])
(println \"Initial state:\")
(println \"  P:\" @wam/P)
(println \"  X:\" @wam/X) 
(println \"  Heap:\" @wam/heap)
(println \"  Program:\" @wam/program-code)
(println \"\nExecuting...\")
(wam/run-wam)
(println \"\nFinal state:\")
(println \"  P:\" @wam/P)
(println \"  X:\" @wam/X)
(println \"  Heap:\" @wam/heap)
" 
