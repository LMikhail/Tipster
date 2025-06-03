#!/bin/bash

# Запуск тестов WAM эмулятора с Kaocha
echo "Running WAM Emulator tests with Kaocha..."
echo "=========================================="

# Запуск всех тестов с Kaocha
clj -M:kaocha
