(ns tipster.all-tests
  (:require [clojure.test :refer :all]
            [tipster.i18n :as i18n]
            [tipster.terms-test :as terms-test]
            [tipster.bindings-test :as bindings-test]
            [tipster.unification-test :as unification-test]
            [tipster.knowledge-test :as knowledge-test]
            [tipster.solver-test :as solver-test]
            [tipster.integration-test :as integration-test]))

;; === ЗАПУСК ВСЕХ ТЕСТОВ ПО МОДУЛЯМ ===

(defn run-all-tipster-tests [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Run all Tipster tests by modules / Запуск всех тестов Tipster по модулям"
  (println (i18n/t :running-tests lang) "Tipster...")
  (println "=" (apply str (repeat 50 "=")))
  
  (println "\n" (i18n/t :terms-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (terms-test/run-terms-tests)
  
  (println "\n" (i18n/t :bindings-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (bindings-test/run-bindings-tests)
  
  (println "\n" (i18n/t :unification-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (unification-test/run-unification-tests)
  
  (println "\n" (i18n/t :knowledge-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (knowledge-test/run-knowledge-tests)
  
  (println "\n" (i18n/t :solver-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (solver-test/run-solver-tests)
  
  (println "\n" (i18n/t :integration-module lang))
  (println "-" (apply str (repeat 30 "-")))
  (integration-test/run-integration-tests)
  
  (println "\n🎉" (i18n/t :completed lang) "!")
  (println "=" (apply str (repeat 50 "="))))

(defn run-module-tests [module-name & {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Run tests for specific module / Запуск тестов конкретного модуля"
  (case module-name
    "terms" (terms-test/run-terms-tests)
    "bindings" (bindings-test/run-bindings-tests)
    "unification" (unification-test/run-unification-tests)
    "knowledge" (knowledge-test/run-knowledge-tests)
    "solver" (solver-test/run-solver-tests)
    "integration" (integration-test/run-integration-tests)
    (do 
      (println "❌" (i18n/t :error lang) ":" module-name)
      (println "📚 Available modules / Доступные модули: terms, bindings, unification, knowledge, solver, integration"))))

;; === ТЕСТОВЫЕ НАБОРЫ ===

(defn run-core-tests [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Run core tests (terms, bindings, unification) / Запуск базовых тестов"
  (println "🔧" (i18n/t :core-tests-title lang))
  (terms-test/run-terms-tests)
  (bindings-test/run-bindings-tests)
  (unification-test/run-unification-tests))

(defn run-logic-tests [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Run logic tests (knowledge, solver) / Запуск логических тестов"
  (println "🧠" (i18n/t :logic-tests-title lang))
  (knowledge-test/run-knowledge-tests)
  (solver-test/run-solver-tests))

(defn run-quick-tests [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Run quick tests (main functions) / Запуск быстрых тестов"
  (println "⚡" (i18n/t :quick-tests-title lang))
  (run-tests 'tipster.terms-test
             'tipster.bindings-test
             'tipster.unification-test))

;; === СТАТИСТИКА ТЕСТОВ ===

(defn test-summary [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  "Show test summary / Показать сводку по тестам"
  (println (i18n/t :test-summary-title lang))
  (println (i18n/t :available-commands lang))
  (println "  " (i18n/t :terms-module lang))
  (println "  " (i18n/t :bindings-module lang))
  (println "  " (i18n/t :unification-module lang))
  (println "  " (i18n/t :knowledge-module lang))
  (println "  " (i18n/t :solver-module lang))
  (println "  " (i18n/t :integration-module lang))
  (println "\n💡 Commands / Команды:")
  (println "  (run-all-tipster-tests)        - all tests / все тесты")
  (println "  (run-module-tests \"name\")      - module tests / тесты модуля")
  (println "  (run-core-tests)               - core tests / базовые тесты")
  (println "  (run-logic-tests)              - logic tests / логические тесты")
  (println "  (run-quick-tests)              - quick tests / быстрые тесты")
  (println "\n🌍 Language / Язык:")
  (println "  Add :lang :ru for Russian / Добавьте :lang :ru для русского")
  (println "  Example: (run-all-tipster-tests :lang :ru)"))

;; Show summary on load / Показываем сводку при загрузке
(test-summary)
