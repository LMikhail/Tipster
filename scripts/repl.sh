#!/bin/bash

# Интерактивный REPL для экспериментов с Tipster
echo "🧪 Tipster - Интерактивный REPL"
echo "==============================="
echo ""
echo "Добро пожаловать в интерактивную среду Tipster!"
echo ""
echo "💡 Полезные команды:"
echo "   (require '[tipster.core :as t])"
echo "   (t/reset-tipster!)                    ; Очистить базу знаний"
echo "   (t/deffact (мой-факт данные))         ; Добавить факт"
echo "   (t/query (мой-факт ?X))               ; Сделать запрос"
echo "   (t/defrule (правило ?X) [(условие ?X)]) ; Добавить правило"
echo ""
echo "📚 Примеры для копирования:"
echo "   (t/deffact (человек алиса))"
echo "   (t/deffact (любит алиса программирование))"
echo "   (t/query (человек ?Кто))"
echo "   (t/query (любит ?Кто ?Что))"
echo ""
echo "🚪 Для выхода: Ctrl+C или (exit)"
echo "================================"

# Запуск REPL с автоматической загрузкой Tipster
clj -e "
(require '[tipster.core :as tipster])
(println \"\n✅ Tipster загружен! Используйте tipster/... для доступа к функциям\")
(println \"✅ Или сократите: (require '[tipster.core :as t])\")
(require '[clojure.repl :refer :all])
(in-ns 'user)
" 
