#!/bin/bash

# 🚀 TIPSTER - Быстрый старт для новичков
# ======================================

clear

echo "🚀 Добро пожаловать в TIPSTER!"
echo "=============================="
echo ""
echo "TIPSTER - это логический движок для Clojure."
echo "Он позволяет вам писать программы в декларативном стиле:"
echo ""
echo "  ✨ Определяете факты о мире"
echo "  ⚡ Создаете правила логического вывода"  
echo "  🔍 Задаете вопросы системе"
echo ""
echo "Система сама найдет ответы, используя логику!"
echo ""

read -p "Нажмите Enter чтобы увидеть примеры..."

echo ""
echo "🎯 Запускаем примеры..."
echo ""

# Проверяем наличие Java и Clojure
if ! command -v java &> /dev/null; then
    echo "❌ Ошибка: Java не найдена."
    echo "   Установите Java 8+ и попробуйте снова."
    exit 1
fi

if ! command -v clj &> /dev/null; then
    echo "❌ Ошибка: Clojure CLI не найден."
    echo "   Установите Clojure CLI tools и попробуйте снова."
    echo "   Инструкции: https://clojure.org/guides/install_clojure"
    exit 1
fi

# Запускаем примеры
./scripts/run-example.sh all

echo ""
echo "🎉 Примеры завершены!"
echo ""
echo "🔥 Что дальше?"
echo "   1. Изучите подробности:     cat examples/README.md"
echo "   2. Поэкспериментируйте:     ./scripts/repl.sh"
echo "   3. Прочитайте quickstart:   cat QUICKSTART.md"
echo ""
echo "💡 Отдельные примеры:"
echo "   • Основы:           ./scripts/run-example.sh basic"
echo "   • Семья:            ./scripts/run-example.sh family"  
echo "   • Правила:          ./scripts/run-example.sh rules"
echo "   • Животные:         ./scripts/run-example.sh animals"
echo "   • Математика:       ./scripts/run-example.sh math"
echo "   • Интерактивный:    ./scripts/run-example.sh interactive"
echo ""
echo "📚 Удачного изучения логического программирования!" 
