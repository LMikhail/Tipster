#!/bin/bash

# Форматирование кода с cljfmt
echo "Formatting code with cljfmt..."
echo "============================"

# Проверка форматирования
echo "Checking current formatting..."
clj -M:format-check src test

if [ $? -eq 0 ]; then
    echo "Code is already properly formatted!"
else
    echo ""
    echo "Formatting files..."
    clj -M:format fix src test
    echo "Code formatting complete!"
fi 
