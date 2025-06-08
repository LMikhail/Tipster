#!/bin/bash

# Universal script for running Tipster examples
# Универсальный скрипт для запуска примеров Tipster
# Usage: ./scripts/run-example.sh <example-name> [--lang ru]
# Examples: basic, family, rules, animals, math, interactive, all

EXAMPLE_NAME="$1"
LANG_ARG=""

# Parse language argument
if [[ "$2" == "--lang" && "$3" == "ru" ]]; then
    LANG_ARG=":lang :ru"
fi

# Функция для показа описания примера
show_description() {
    local example_name="$1"
    
    # Run Clojure to get example description
    local desc=$(clj -M:examples -e "
    (require '[${example_name} :as ex])
    (let [desc (ex/description ${LANG_ARG})]
      (println (:title desc))
      (println (:subtitle desc))
      (println \"==============================\"))")
    
    echo "$desc"
}

# Функция для показа что изучили
show_what_you_learned() {
    local example_name="$1"
    
    # Get information about what was learned
    local learned=$(clj -M:examples -e "
    (require '[${example_name} :as ex])
    (let [desc (ex/description ${LANG_ARG})]
      (println)
      (if (= \":ru\" \"${LANG_ARG##* }\")
        (println \"💡 Что вы изучили:\")
        (println \"💡 What you learned:\"))
      (doseq [item (:what-you-learn desc)]
        (println \"   •\" item))
      (println)
      (when (:next desc)
        (if (= \":ru\" \"${LANG_ARG##* }\")
          (println \"📖 Следующий пример:\" (:next desc))
          (println \"📖 Next example:\" (:next desc)))))")
    
    echo "$desc"
}

# Функция для запуска примера
run_example() {
    local example_name="$1"
    
    show_description "$example_name"
    
    # Run example
    clj -M:examples -e "
    (require '[${example_name} :as ex])
    (ex/run-example ${LANG_ARG})"
    
    show_what_you_learned "$example_name"
}

# Function to run all examples
run_all_examples() {
    if [[ "$LANG_ARG" == *":ru"* ]]; then
        echo "🚀 TIPSTER - Примеры использования"
        echo $(printf '%*s' 60 '' | tr ' ' '=')
        echo "Логический движок для Clojure с поддержкой фактов, правил и запросов"
        echo $(printf '%*s' 60 '' | tr ' ' '=')
    else
        echo "🚀 TIPSTER - Usage Examples"
        echo $(printf '%*s' 60 '' | tr ' ' '=')
        echo "Logic engine for Clojure with support for facts, rules and queries"
        echo $(printf '%*s' 60 '' | tr ' ' '=')
    fi
    
    local examples=("basic" "family" "rules" "animals" "math" "interactive")
    
    for example in "${examples[@]}"; do
        clj -M:examples -e "
        (require '[${example} :as ex])
        (ex/run-example ${LANG_ARG})"
    done
    
    echo ""
    if [[ "$LANG_ARG" == *":ru"* ]]; then
        echo "🎉 Все примеры выполнены!"
        echo ""
        echo "💡 Попробуйте запустить отдельные примеры:"
        echo "   ./scripts/run-example.sh basic --lang ru"
        echo "   ./scripts/run-example.sh family --lang ru"
        echo "   ./scripts/run-example.sh rules --lang ru"
        echo "   ./scripts/run-example.sh animals --lang ru"
        echo "   ./scripts/run-example.sh math --lang ru"
        echo "   ./scripts/run-example.sh interactive --lang ru"
        echo ""
        echo "📖 Или изучите примеры в файле: examples/ru/README.md"
    else
        echo "🎉 All examples completed!"
        echo ""
        echo "💡 Try running individual examples:"
        echo "   ./scripts/run-example.sh basic"
        echo "   ./scripts/run-example.sh family"
        echo "   ./scripts/run-example.sh rules"
        echo "   ./scripts/run-example.sh animals"
        echo "   ./scripts/run-example.sh math"
        echo "   ./scripts/run-example.sh interactive"
        echo ""
        echo "📖 Or study examples in file: examples/README.md"
    fi
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
        if [[ "$LANG_ARG" == *":ru"* ]]; then
            echo "📚 Доступные примеры:"
            echo "   basic       - Основы: факты и запросы"
            echo "   family      - Семейные отношения"
            echo "   rules       - Правила и логический вывод"
            echo "   animals     - Классификация животных"
            echo "   math        - Математические отношения"
            echo "   interactive - Интерактивный режим"
            echo "   all         - Все примеры подряд"
        else
            echo "📚 Available examples:"
            echo "   basic       - Basics: facts and queries"
            echo "   family      - Family relationships"
            echo "   rules       - Rules and logical inference"
            echo "   animals     - Animal classification"
            echo "   math        - Mathematical relations"
            echo "   interactive - Interactive mode"
            echo "   all         - All examples in sequence"
        fi
        ;;
    *)
        if [[ "$LANG_ARG" == *":ru"* ]]; then
            echo "❌ Неизвестный пример: $EXAMPLE_NAME"
            echo ""
            echo "📚 Доступные примеры:"
            echo "   basic, family, rules, animals, math, interactive, all"
            echo ""
            echo "💡 Использование: ./scripts/run-example.sh <example-name> [--lang ru]"
            echo "💡 Список примеров: ./scripts/run-example.sh list --lang ru"
        else
            echo "❌ Unknown example: $EXAMPLE_NAME"
            echo ""
            echo "📚 Available examples:"
            echo "   basic, family, rules, animals, math, interactive, all"
            echo ""
            echo "💡 Usage: ./scripts/run-example.sh <example-name> [--lang ru]"
            echo "💡 List examples: ./scripts/run-example.sh list"
        fi
        exit 1
        ;;
esac 
