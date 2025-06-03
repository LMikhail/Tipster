#!/bin/bash

# Запуск тестов Tipster логического движка в режиме наблюдения с Kaocha
echo "Running Tipster Logic Engine tests in watch mode with Kaocha..."
echo "Will automatically re-run tests when files change."
echo "Press Ctrl+C to stop."
echo "==============================================================="

# Запуск тестов с автоматическим перезапуском при изменении файлов
clj -M:test-watch 
