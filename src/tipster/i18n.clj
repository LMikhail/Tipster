(ns tipster.i18n
  (:require [clojure.string :as str]
            [clojure.set]))

;; Language detection and management
(def ^:dynamic *default-language* :en)
(def ^:dynamic *current-language* nil)

(defn detect-language []
  "Detect language from environment or return default"
  (or *current-language*
      (when-let [lang (System/getenv "TIPSTER_LANG")]
        (keyword (str/lower-case lang)))
      ;; Always default to English unless explicitly set
      *default-language*))

(defn set-language! [lang]
  "Set current language"
  (alter-var-root #'*current-language* (constantly lang)))

;; Message dictionaries
(def messages
  {:en {
        ;; General
        :welcome "Welcome to Tipster Logic Programming System"
        :goodbye "Thank you for using Tipster"
        :error "Error"
        :success "Success"
        :loading "Loading..."
        :running "Running"
        :completed "Completed"
        
        ;; Examples
        :example-title "Tipster Example: %s"
        :example-description "Description"
        :example-running "Running example..."
        :example-result "Result"
        :available-examples "Available examples"
        :basic-example "Basic Logic Programming"
        :family-example "Family Relationships"
        :rules-example "Rules and Inference"
        :animals-example "Animal Classification"
        :math-example "Mathematical Relations" 
        :interactive-example "Interactive Session"
        
        ;; Tests
        :test-summary-title "🧪 Tipster Modular Testing System"
        :available-commands "📊 Available commands"
        :running-tests "Running tests"
        :test-module "Test module"
        :tests-passed "Tests passed"
        :tests-failed "Tests failed"
        :test-assertions "Assertions"
        :all-tests-title "All Tipster Tests"
        :core-tests-title "Core Tests (Terms, Bindings, Unification)"
        :logic-tests-title "Logic Tests (Knowledge, Solver)"
        :quick-tests-title "Quick Verification Tests"
        :module-tests-title "Module Tests: %s"
        
        ;; Test modules
        :terms-module "📋 Terms - term creation and typing"
        :bindings-module "🔗 Bindings - variable bindings"
        :unification-module "🤝 Unification - unification algorithm"
        :knowledge-module "🧠 Knowledge - knowledge base"
        :solver-module "⚡ Solver - logical solver"
        :integration-module "🔄 Integration - integration tests and macros"
        
        ;; Results
        :solution-found "Solution found"
        :no-solution "No solution found"
        :multiple-solutions "%d solutions found"
        :query-result "Query result"
        :fact-added "Fact added to knowledge base"
        :rule-added "Rule added to knowledge base"
        }
   
   :ru {
        ;; Общие
        :welcome "Добро пожаловать в систему логического программирования Tipster"
        :goodbye "Спасибо за использование Tipster"
        :error "Ошибка"
        :success "Успех"
        :loading "Загрузка..."
        :running "Выполнение"
        :completed "Завершено"
        
        ;; Примеры
        :example-title "Пример Tipster: %s"
        :example-description "Описание"
        :example-running "Запуск примера..."
        :example-result "Результат"
        :available-examples "Доступные примеры"
        :basic-example "Основы логического программирования"
        :family-example "Семейные отношения"
        :rules-example "Правила и вывод"
        :animals-example "Классификация животных"
        :math-example "Математические отношения"
        :interactive-example "Интерактивная сессия"
        
        ;; Тесты
        :test-summary-title "🧪 Модульная система тестов Tipster"
        :available-commands "📊 Доступные команды"
        :running-tests "Запуск тестов"
        :test-module "Модуль тестов"
        :tests-passed "Тестов пройдено"
        :tests-failed "Тестов провалено"
        :test-assertions "Утверждений"
        :all-tests-title "Все тесты Tipster"
        :core-tests-title "Базовые тесты (Термы, Связывания, Унификация)"
        :logic-tests-title "Логические тесты (База знаний, Солвер)"
        :quick-tests-title "Быстрые проверочные тесты"
        :module-tests-title "Тесты модуля: %s"
        
        ;; Модули тестов
        :terms-module "📋 Термы - создание и типизация термов"
        :bindings-module "🔗 Связывания - связывание переменных"
        :unification-module "🤝 Унификация - алгоритм унификации"
        :knowledge-module "🧠 База знаний - база знаний"
        :solver-module "⚡ Солвер - логический солвер"
        :integration-module "🔄 Интеграция - интеграционные тесты и макросы"
        
        ;; Результаты
        :solution-found "Найдено решение"
        :no-solution "Решение не найдено"
        :multiple-solutions "Найдено решений: %d"
        :query-result "Результат запроса"
        :fact-added "Факт добавлен в базу знаний"
        :rule-added "Правило добавлено в базу знаний"
        }})

(defn t 
  "Translate message key to current language with optional formatting"
  ([key] (t key (detect-language)))
  ([key lang] 
   (get-in messages [lang key] (str "Missing translation: " key)))
  ([key lang & args]
   (let [template (t key lang)]
     (if (seq args)
       (apply format template args)
       template))))

(defn with-language 
  "Execute function with specific language"
  [lang f]
  (binding [*current-language* lang]
    (f)))

(defn multilingual-output
  "Output text in multiple languages with language prefix"
  [key & {:keys [langs format-args] :or {langs [:en :ru] format-args []}}]
  (doseq [lang langs]
    (let [prefix (case lang :en "🇺🇸" :ru "🇷🇺" "")
          message (if (seq format-args)
                   (apply t key lang format-args)
                   (t key lang))]
      (println (str prefix " " message)))))

;; Convenience macros
(defmacro defn-i18n 
  "Define function with internationalization support"
  [name args & body]
  `(defn ~name ~args
     (let [~'lang (detect-language)]
       ~@body)))

;; Language switching utilities
(defn parse-lang-option [args]
  "Parse --lang option from command line arguments"
  (let [lang-idx (.indexOf args "--lang")]
    (if (and (>= lang-idx 0) (< (inc lang-idx) (count args)))
      (keyword (nth args (inc lang-idx)))
      nil)))

(defn setup-language-from-args [args]
  "Setup language from command line arguments"
  (when-let [lang (parse-lang-option args)]
    (set-language! lang)))

;; Development helpers
(defn available-languages []
  "List available languages"
  (keys messages))

(defn missing-translations 
  "Find missing translations for a language"
  [target-lang]
  (let [en-keys (set (keys (:en messages)))
        target-keys (set (keys (target-lang messages)))]
    (clojure.set/difference en-keys target-keys)))

(defn translation-coverage 
  "Calculate translation coverage percentage"
  [target-lang]
  (let [en-count (count (:en messages))
        target-count (count (target-lang messages))]
    (if (> en-count 0)
      (* 100.0 (/ target-count en-count))
      0.0)))
