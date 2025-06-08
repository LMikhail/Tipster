#!/bin/bash

# Универсальный скрипт для запуска примеров Tipster
# Использование: ./scripts/run-example.sh <example-name>
# Примеры: basic, family, rules, animals, math, interactive, all

EXAMPLE_NAME="$1"

# Функция для показа описания примера
show_description() {
    local example_name="$1"
    
    # Запускаем Clojure для получения описания примера
    local desc=$(clj -M:examples -e "
    (require '[${example_name} :as ex])
    (let [desc (ex/description)]
      (println (:title desc))
      (println (:subtitle desc))
      (println \"==============================\"))")
    
    echo "$desc"
}

# Функция для показа что изучили
show_what_you_learned() {
    local example_name="$1"
    
    # Получаем информацию о том что изучили
    local learned=$(clj -M:examples -e "
    (require '[${example_name} :as ex])
    (let [desc (ex/description)]
      (println)
      (println \"💡 Что вы изучили:\")
      (doseq [item (:what-you-learn desc)]
        (println \"   •\" item))
      (println)
      (when (:next desc)
        (println \"📖 Следующий пример:\" (:next desc))))")
    
    echo "$desc"
}

# Функция для запуска примера
run_example() {
    local example_name="$1"
    
    show_description "$example_name"
    
    # Запускаем пример
    clj -M:examples -e "
    (require '[${example_name} :as ex])
    (ex/run-example)"
    
    show_what_you_learned "$example_name"
}

# Функция для запуска всех примеров
run_all_examples() {
    echo "🚀 TIPSTER - Примеры использования"
    echo $(printf '%*s' 60 '' | tr ' ' '=')
    echo "Логический движок для Clojure с поддержкой фактов, правил и запросов"
    echo $(printf '%*s' 60 '' | tr ' ' '=')
    
    local examples=("basic" "family" "rules" "animals" "math" "interactive")
    
    for example in "${examples[@]}"; do
        clj -M:examples -e "
        (require '[${example} :as ex])
        (ex/run-example)"
    done
    
    echo ""
    echo "🎉 Все примеры выполнены!"
    echo ""
    echo "💡 Попробуйте запустить отдельные примеры:"
    echo "   ./scripts/run-example.sh basic"
    echo "   ./scripts/run-example.sh family"
    echo "   ./scripts/run-example.sh rules"
    echo "   ./scripts/run-example.sh animals"
    echo "   ./scripts/run-example.sh math"
    echo "   ./scripts/run-example.sh interactive"
    echo ""
    echo "📖 Или изучите примеры в файле: examples/README.md"
}

# Основная логика
case "$EXAMPLE_NAME" in
    "basic"|"family"|"rules"|"animals"|"math"|"interactive")
        run_example "$EXAMPLE_NAME"
        ;;
    "all"|"")
        run_all_examples
        ;;
    "list")
        echo "📚 Доступные примеры:"
        echo "   basic       - Основы: факты и запросы"
        echo "   family      - Семейные отношения"
        echo "   rules       - Правила и логический вывод"
        echo "   animals     - Классификация животных"
        echo "   math        - Математические отношения"
        echo "   interactive - Интерактивный режим"
        echo "   all         - Все примеры подряд"
        ;;
    *)
        echo "❌ Неизвестный пример: $EXAMPLE_NAME"
        echo ""
        echo "📚 Доступные примеры:"
        echo "   basic, family, rules, animals, math, interactive, all"
        echo ""
        echo "💡 Использование: ./scripts/run-example.sh <example-name>"
        echo "💡 Список примеров: ./scripts/run-example.sh list"
        exit 1
        ;;
esac 
