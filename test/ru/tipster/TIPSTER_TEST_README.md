# Тестирование Tipster логического движка

Этот проект использует стандартную структуру тестирования Clojure с использованием `clojure.test` для проверки корректности работы унификационного движка и логического солвера.

## Структура тестов

```
test/
└── tipster/
    └── core_test.clj    - Основные тесты логического движка
```

## Запуск тестов

### Базовый запуск всех тестов
```bash
clj -M:test
```

### Интерактивное тестирование через REPL
```bash
clj
```
Затем в REPL:
```clojure
(require 'tipster.core-test)
(run-tests 'tipster.core-test)

;; Или запуск конкретного теста
(test-unify-compound-terms)
```

## Категории тестов

### Тесты базовых компонентов

#### Тесты создания термов
- `test-term-creation` - Проверка создания переменных, атомов и составных термов
- `test-bindings-operations` - Операции со связываниями переменных
- `test-deref-term` - Дереференсация термов с учетом связываний

```clojure
(deftest test-term-creation
  ;; Проверяет корректность создания всех типов термов
  ;; и их свойства через протокол ITerm
  )
```

### Тесты унификации

#### Базовая унификация
- `test-unify-identical-terms` - Унификация идентичных термов
- `test-unify-different-atoms` - Неудачная унификация различных атомов
- `test-unify-variable-with-atom` - Связывание переменной с атомом
- `test-unify-variable-with-variable` - Унификация переменных между собой

#### Сложная унификация
- `test-unify-compound-terms` - Унификация составных термов с переменными
- `test-unify-different-functors` - Неудачная унификация различных функторов
- `test-unify-different-arity` - Неудачная унификация термов разной арности
- `test-occurs-check` - Проверка вхождения (предотвращение бесконечных структур)

```clojure
(deftest test-unify-compound-terms
  (testing "Унификация составных термов"
    (let [var1 (tipster/make-variable "X")
          var2 (tipster/make-variable "Y")
          compound1 (tipster/make-compound 'f var1 'b)
          compound2 (tipster/make-compound 'f 'a var2)
          result (tipster/unify compound1 compound2)]
      
      (is (not (nil? result)))
      (is (= (tipster/make-atom 'a) (tipster/lookup-binding var1 result)))
      (is (= (tipster/make-atom 'b) (tipster/lookup-binding var2 result))))))
```

### Тесты базы знаний

#### Управление знаниями
- `test-knowledge-base-operations` - Добавление фактов и правил
- `test-clear-knowledge-base` - Очистка базы знаний

```clojure
(deftest test-knowledge-base-operations
  ;; Проверяет корректность добавления и хранения
  ;; фактов и правил в базе знаний
  )
```

### Тесты логического солвера

#### Базовое решение
- `test-solve-simple-fact` - Решение простых фактов
- `test-solve-fact-with-variable` - Решение с переменными
- `test-solve-rule` - Применение правил для вывода
- `test-solve-multiple-solutions` - Множественные решения

```clojure
(deftest test-solve-rule
  (testing "Решение через правило"
    (reset-tipster-for-test!)
    
    (let [fact (tipster/make-compound 'human 'alice)
          rule-head (tipster/make-compound 'mortal (tipster/make-variable "X"))
          rule-body [(tipster/make-compound 'human (tipster/make-variable "X"))]
          var (tipster/make-variable "Y")
          query (tipster/make-compound 'mortal var)]
      
      (tipster/add-fact! fact)
      (tipster/add-rule! rule-head rule-body)
      
      (let [solutions (tipster/solve-goal query)
            solution (first solutions)]
        (is (= 1 (count solutions)))
        (is (= (tipster/make-atom 'alice) (tipster/lookup-binding var solution)))))))
```

### Тесты интеграции с Clojure

#### Преобразование данных
- `test-clojure-to-tipster-conversion` - Clojure → Tipster
- `test-tipster-to-clojure-conversion` - Tipster → Clojure

```clojure
(deftest test-clojure-to-tipster-conversion
  ;; Проверяет корректность преобразования различных
  ;; Clojure-структур в термы Tipster
  )
```

### Тесты макросов

#### Удобный синтаксис
- `test-deffact-macro` - Макрос определения фактов
- `test-query-macro` - Макрос запросов

```clojure
(deftest test-deffact-macro
  (testing "Макрос deffact"
    (reset-tipster-for-test!)
    
    (tipster/deffact (human alice))
    
    ;; Проверяем, что факт корректно добавлен в базу знаний
    ))
```

### Интеграционные тесты

#### Комплексные сценарии
- `test-complex-family-relations` - Сложные семейные отношения
- `test-performance-with-many-facts` - Производительность с большими данными

```clojure
(deftest test-complex-family-relations
  (testing "Сложные семейные отношения"
    ;; Создает сложную систему семейных отношений
    ;; и проверяет корректность логического вывода
    ))
```

## Соглашения по тестированию

### Именование тестов
- Тесты базовых функций: `test-function-name`
- Тесты сложных сценариев: `test-scenario-description`
- Интеграционные тесты: `test-integration-aspect`

### Структура теста
1. **Сброс состояния:** Всегда начинать с `(reset-tipster-for-test!)`
2. **Настройка:** Создать необходимые термы и добавить факты/правила
3. **Выполнение:** Выполнить тестируемую операцию (унификация, решение цели)
4. **Проверка:** Убедиться в корректности результата

### Проверка результатов унификации

```clojure
;; Проверка успешной унификации
(is (not (nil? (tipster/unify term1 term2))))

;; Проверка связывания переменных
(let [result (tipster/unify var term)]
  (is (= expected-value (tipster/lookup-binding var result))))

;; Проверка неудачной унификации
(is (nil? (tipster/unify incompatible-term1 incompatible-term2)))
```

### Проверка решений

```clojure
;; Проверка количества решений
(let [solutions (tipster/solve-goal query)]
  (is (= expected-count (count solutions))))

;; Проверка содержимого решений
(let [solutions (tipster/solve-goal query)
      bound-values (set (map #(tipster/lookup-binding var %) solutions))]
  (is (contains? bound-values expected-value)))
```

## Отладка тестов

### Детальный вывод состояния

```clojure
(deftest debug-test
  (testing "Debug specific behavior"
    (reset-tipster-for-test!)
    
    ;; Создание термов
    (println "Terms created:" term1 term2)
    
    ;; Унификация
    (let [result (tipster/unify term1 term2)]
      (println "Unification result:" result)
      
      ;; Проверки
      )))
```

### Пошаговое выполнение в REPL

```clojure
;; Создание тестовых данных
(def var-x (tipster/make-variable "X"))
(def atom-a (tipster/make-atom 'a))

;; Унификация
(tipster/unify var-x atom-a)

;; Проверка базы знаний
(tipster/add-fact! (tipster/make-compound 'human 'alice))
@tipster/knowledge-base

;; Решение запроса
(tipster/solve-goal (tipster/make-compound 'human (tipster/make-variable "X")))
```

## Покрытие тестами

### Текущее покрытие

- ✅ **Создание термов** - все типы (переменные, атомы, составные)
- ✅ **Унификация** - базовая и сложная, с проверкой occurs check
- ✅ **Дереференсация** - с цепочками связывания
- ✅ **База знаний** - добавление, удаление, поиск фактов и правил
- ✅ **Логический солвер** - решение фактов и правил
- ✅ **Интеграция с Clojure** - преобразование данных
- ✅ **Макросы** - удобный синтаксис для работы
- ✅ **Сложные сценарии** - семейные отношения, производительность

### Планируемые тесты

- ⚠️ **Constraint Logic Programming** - работа с ограничениями
- ⚠️ **Оптимизации** - индексирование, мемоизация
- ❌ **Параллельные вычисления** - многопоточность
- ❌ **Интеграция с базами данных** - внешние источники знаний

## Автоматизация

### CI/CD интеграция

```bash
# Команда для CI
clj -M:test

# С детальным выводом
clj -M:test :verbose
```

### Профилирование производительности

```clojure
;; Измерение времени выполнения
(time (run-tests 'tipster.core-test))

;; Профилирование конкретных операций
(time (tipster/solve-goal large-query))
```

## Расширение тестов

### Добавление новых тестов

1. **Для новых алгоритмов унификации:**
   ```clojure
   (deftest test-new-unification-feature
     (testing "Description of the new feature"
       (reset-tipster-for-test!)
       ;; Создание тестовых данных
       ;; Выполнение операции
       ;; Проверка результатов
       ))
   ```

2. **Для новых типов термов:**
   ```clojure
   (deftest test-new-term-type
     (testing "New term type behavior"
       ;; Проверка создания
       ;; Проверка унификации
       ;; Проверка интеграции с существующими компонентами
       ))
   ```

3. **Для производительности:**
   ```clojure
   (deftest test-performance-scenario
     (testing "Performance with specific data patterns"
       ;; Создание большого объема данных
       ;; Измерение времени выполнения
       ;; Проверка результатов
       ))
   ```

## Лучшие практики

### Изоляция тестов
- Всегда используйте `reset-tipster-for-test!` в начале каждого теста
- Не полагайтесь на порядок выполнения тестов
- Создавайте минимально необходимые данные для каждого теста

### Читаемость
- Используйте описательные имена для тестов и переменных
- Добавляйте комментарии для сложных тестовых сценариев
- Группируйте связанные проверки в один тест

### Производительность тестов
- Избегайте создания избыточно больших структур данных
- Используйте ленивые последовательности для больших результатов
- Профилируйте медленные тесты

Эта архитектура тестирования обеспечивает надежную проверку всех аспектов Tipster логического движка и гарантирует корректность работы системы при дальнейшем развитии.
