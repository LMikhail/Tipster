#!/bin/bash

# Запуск тестов Tipster логического движка с Kaocha
echo "Running Tipster Logic Engine tests with Kaocha..."
echo "==============================================="

# Запуск всех тестов с Kaocha
clj -M:kaocha
