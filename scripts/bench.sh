#!/bin/bash

# Запуск бенчмарков производительности
echo "Running WAM Emulator performance benchmarks..."
echo "This may take several minutes..."
echo "============================================="

# Запуск бенчмарков
clj -X:bench

echo ""
echo "Benchmarks complete!"
echo "Results show execution time statistics for various WAM operations." 
