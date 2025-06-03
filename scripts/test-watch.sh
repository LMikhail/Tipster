#!/bin/bash

# Запуск тестов WAM эмулятора в режиме наблюдения с Kaocha
echo "Running WAM Emulator tests in watch mode with Kaocha..."
echo "Will automatically re-run tests when files change."
echo "Press Ctrl+C to stop."
echo "======================================================"

# Запуск тестов с автоматическим перезапуском при изменении файлов
clj -M:test-watch 
