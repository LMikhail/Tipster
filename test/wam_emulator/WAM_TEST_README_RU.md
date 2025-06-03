# Тестирование WAM Эмулятора

Этот проект использует стандартную структуру тестирования Clojure с использованием `clojure.test`.

## Структура тестов

```
test/
└── wam_emulator/
    └── core_test.clj    - Основные тесты WAM эмулятора
```

## Запуск тестов

### Базовый запуск всех тестов
```bash
./test.sh
```
Или через deps:
```bash
clj -M:test
```

### Режим наблюдения (автоматический перезапуск при изменениях)
```bash
./test-watch.sh
```
Или через deps:
```bash
clj -M:test-watch
```

### Интерактивное тестирование через REPL
```bash
./repl.sh
```
Затем в REPL:
```clojure
(require 'wam-emulator.core-test)
(run-tests 'wam-emulator.core-test)
```

## Категории тестов

### Unit тесты
- `test-wam-state-reset` - Проверка сброса состояния WAM
- `test-put-constant-instruction` - Тест инструкции put_constant
- `test-get-constant-success` - Успешная унификация констант
- `test-get-constant-failure` - Неудачная унификация (должна вызывать исключение)
- `test-get-variable-instruction` - Создание новых переменных
- `test-proceed-instruction` - Завершение выполнения
- `test-try-me-else-instruction` - Создание точек выбора
- `test-call-instruction-*` - Вызов предикатов

### Интеграционные тесты
- `test-simple-program-execution` - Полное выполнение простой программы
- `test-failed-program-execution` - Программа с неудачной унификацией
- `test-backtracking-setup` - Настройка механизма бэктрекинга

### Тесты внутренних функций
- `test-heap-operations` - Операции с кучей
- `test-wam-deref` - Дереференсация термов
- `test-tag-constructors` - Конструкторы тегированных значений
- `test-bind-var-with-trail` - Связывание переменных с записью в трейл

## Добавление новых тестов

1. **Для новых инструкций WAM:**
   ```clojure
   (deftest test-new-instruction
     (testing "Description of what this instruction should do"
       (reset-wam-for-test!)
       ;; Setup test state
       ;; Execute instruction
       ;; Assert expected results
       ))
   ```

2. **Для сложных сценариев:**
   ```clojure
   (deftest test-complex-scenario
     (testing "Integration test for complex WAM scenario"
       (reset-wam-for-test!)
       ;; Setup program
       (reset! wam/program-code [...])
       ;; Run and verify
       (wam/run-wam)
       ;; Assert final state
       ))
   ```

## Соглашения по тестированию

### Именование
- Тесты функций: `test-function-name`
- Тесты инструкций: `test-instruction-name-instruction`
- Интеграционные тесты: `test-scenario-description`

### Структура теста
1. **Сброс состояния:** Всегда начинать с `(reset-wam-for-test!)`
2. **Настройка:** Подготовить необходимое состояние
3. **Выполнение:** Выполнить тестируемую операцию
4. **Проверка:** Убедиться в корректности результата

### Проверка исключений
Для тестов, которые должны вызывать ошибки:
```clojure
(is (thrown? clojure.lang.ExceptionInfo
             (wam/execute-instruction [:invalid-instruction]))
    "Should throw exception for invalid instruction")
```

### Проверка состояния WAM
```clojure
;; Проверка регистров
(is (= expected-value @wam/P) "Program counter should be updated")
(is (= expected-value (wam/get-X 0)) "X register should contain expected value")

;; Проверка памяти
(is (= expected-size (count @wam/heap)) "Heap should have expected size")
(is (= expected-content @wam/trail) "Trail should record expected bindings")
```

## Отладка тестов

### Детальный вывод состояния
```clojure
(deftest debug-test
  (testing "Debug specific behavior"
    (reset-wam-for-test!)
    ;; Setup
    (println "Before:" @wam/X @wam/heap)
    ;; Action
    (println "After:" @wam/X @wam/heap)
    ;; Assert
    ))
```

### Пошаговое выполнение в REPL
```clojure
(reset-wam-for-test!)
(reset! wam/program-code [[:put_constant 'a 0]])
(wam/execute-instruction (first @wam/program-code))
@wam/X  ; Проверить состояние
```

## Покрытие тестами

Текущее покрытие включает:
- ✅ Базовые инструкции WAM (put_constant, get_constant, etc.)
- ✅ Управление состоянием (регистры, куча, стек)
- ✅ Создание точек выбора для бэктрекинга
- ✅ Связывание переменных с трейлом
- ⚠️ Полный бэктрекинг (частично)
- ❌ Сложные структуры данных
- ❌ Оптимизации производительности

## Автоматизация

### CI/CD интеграция
Для непрерывной интеграции можно использовать:
```bash
# В CI скрипте
clj -M:test
```

### Профилирование тестов
Для измерения производительности:
```bash
time ./test.sh
```

## Расширение тестов

При добавлении новой функциональности в WAM эмулятор:
1. Добавьте unit тесты для новых функций
2. Добавьте интеграционные тесты для новых сценариев
3. Обновите документацию тестов
4. Убедитесь, что все существующие тесты проходят 
