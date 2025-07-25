# Обоснование проектных решений в Tipster Core

## Сводная таблица решений

| Концепция | Ключевое преимущество (За) | Основной компромисс (Против) | Оценка веса решения |
| :--- | :--- | :--- | :--- |
| [**Суперсет Clojure**](#1-tipster-как-бесшовный-суперсет-clojure) | Огромная экономия сил за счет переиспользования экосистемы. | Высокий порог входа для не-Clojure разработчиков. | `++` (Стратегически верное) |
| [**`Symbol` вместо `Atom`**](#2-терминология-symbol-вместо-atom) | Устранение критической путаницы с `clojure.core/Atom`. | Незначительная непривычность для Prolog-ветеранов. | `+` (Прагматично и необходимо) |
| [**Двойная семантика**](#3-двойная-семантика-термов) | Унификация логики и вычислений в одной конструкции. | Высокая когнитивная нагрузка на разработчика. | `++` (Рискованно, но это ядро инновации) |
| [**`def` vs `defn`**](#4-разделение-def-факты-и-defn-правилафункции) | Четкое семантическое разделение данных и логики. | Нарушение "чистоты" Prolog, где факт - частный случай правила. | `+` (Улучшение читаемости) |
| [**Разделитель `\|`**](#5-обоснование-выбора-разделителя-) | Лучший компромисс: валидность и визуальная ясность. | Нестандартное соглашение об именовании. | `+` (Прагматично и ясно) |
| [**Модификатор `\|l`**](#6-модификатор-l-для-определения-правил) | Элегантность и возможность перегрузки имен. | Риск случайной ошибки (забыть `\|l`). | `~` (Элегантный компромисс) |
| [**Модификаторы вызова**](#7-явные-модификаторы-вызова-l-f-seq) | Максимальный контроль и ясность намерений в коде. | Небольшой синтаксический шум. | `++` (Ключ к гибкости) |
| [**Расширяемость**](#8-расширяемость-через-модификаторы) | Легкость добавления новых стратегий вычисления. | Потенциальное усложнение языка в будущем. | `+` (Полезный задел на будущее) |
| [**Модель данных PKVTC**](#9-выбор-модели-хранения-данных) | Радикальное улучшение производительности унификации. | Увеличение объема данных на 25% и снижение БД-совместимости. | `++` (Критично для масштабирования) |

---

## Подробный разбор решений

Ниже приводится подробное описание каждого решения с аргументами "за" и "против", которые были кратко изложены в таблице выше.

### 1. Tipster как бесшовный суперсет Clojure

**Концепция:** Вместо создания нового языка с нуля, Tipster расширяет Clojure, добавляя в него конструкции логического программирования.

#### За (Pros)

*   **Переиспользование экосистемы:** Позволяет использовать весь богатый набор библиотек, инструментов и существующей кодовой базы Clojure.
*   **Низкий порог входа для Clojure-разработчиков:** Программистам, знакомым с Clojure, не нужно учить совершенно новый синтаксис.
*   **Полная совместимость:** Обеспечивает естественное и бесшовное взаимодействие между декларативным логическим кодом и стандартным функциональным/императивным кодом Clojure.

#### Против (Cons)

*   **Высокий порог входа для "не-Clojure" разработчиков:** Программистам из мира Prolog или других языков придется сначала освоить основы Clojure.
*   **Риск семантических конфликтов:** Необходимо очень тщательно спроектировать компилятор, чтобы новые конструкции не конфликтовали с существующей семантикой Clojure и не приводили к неожиданному поведению.

### 2. Терминология: `Symbol` вместо `Atom`

**Концепция:** Для обозначения уникальных символьных констант (аналог `atom` в Prolog) используется термин и тип `Symbol` из Clojure, а термин `Atom` сознательно избегается.

#### За (Pros)

*   **Устранение путаницы:** Однозначно решает известную проблему омонимии между `atom` в Prolog (константа) и `Atom` в Clojure (ссылочный тип для управления состоянием).
*   **Идиоматичность:** Использование `Symbol` для идентификаторов полностью соответствует духу и практике Clojure.

#### Против (Cons)

*   **Непривычность для Prolog-программистов:** Ветеранам логического программирования может потребоваться время, чтобы привыкнуть к новой терминологии.

### 3. Двойная семантика термов

**Концепция:** Любой терм `(f t₁ ... tₙ)` может быть интерпретирован и как логический паттерн для поиска ($Φ_L$), и как вычислительное выражение для исполнения ($Φ_C$).

#### За (Pros)

*   **Выразительная сила:** Это ключевая инновация, которая унифицирует миры логики и вычислений. Один и тот же код может использоваться и для поиска, и для прямого выполнения.
*   **Лаконичность:** Устраняет необходимость в "коде-клее" для вызова функций из логических правил и наоборот.

#### Против (Cons)

*   **Высокая когнитивная нагрузка:** Концепция может быть сложной для понимания. Разработчик должен четко осознавать, какая интерпретация активна в данный момент, чтобы избежать трудноуловимых ошибок.
*   **Сложность компилятора:** Реализация компилятора, корректно обрабатывающего обе семантики и переключения между ними, является нетривиальной задачей.

### 4. Разделение `def` (факты) и `defn` (правила/функции)

**Концепция:** Факты (безусловные утверждения) определяются через `def`, а правила и функции (вычислительные абстракции) — через `defn`.

#### За (Pros)

*   **Четкое разделение:** Синтаксис подчеркивает разницу между статичными данными (`def`) и вычислительной логикой (`defn`), что улучшает читаемость.
*   **Соответствие идиомам Clojure:** Использование `def` для определения "данных" и `defn` для определения "поведения" канонично для Clojure.

#### Против (Cons)

*   **Отход от чистоты Prolog:** В "чистом" логическом программировании факты и правила являются лишь частными случаями одного и того же понятия — клаузы предиката. Такое разделение может показаться искусственным.

### 5. Обоснование выбора разделителя `|`

**Концепция:** Для модификации семантики был выбран специальный символ-разделитель `|` (пайп), который присоединяется к имени сущности.

**Обоснование:** Был проведен анализ различных символов (`-`, `::`, `!`, `?`, `$` и др.) по критериям синтаксической валидности, визуальной ясности и идиоматических конфликтов.
*   `::` и `^` оказались синтаксически невалидными.
*   `$` был синтаксически наилучшим вариантом, но был отвергнут из-за плохой визуальной читаемости в тексте.
*   `!`, `?`, `%` имеют сильные конфликтующие значения в Clojure.
*   `-` широко используется в именах функций и переменных, что привело бы к неоднозначности.
*   `|` был выбран как наилучший компромисс: он валиден, визуально ясен и имеет минимальные конфликты.

### 6. Модификатор `|l` для определения правил

**Концепция:** Правило отличается от функции не новым макросом (`defrule`), а модификатором `|l`, присоединенным к `defn`: `(defn|l ...)`.

#### За (Pros)

*   **Элегантность и минимализм:** Позволяет переиспользовать знакомый макрос `defn`.
*   **Перегрузка имен:** Дает возможность определить правило и оптимизированную функцию с одним и тем же именем.

#### Против (Cons)

*   **Риск случайных ошибок:** Разработчик может забыть указать `|l` и вместо правила случайно определить обычную функцию.
*   **Нестандартный синтаксис:** Схема именования `символ|модификатор` не является стандартным соглашением в Clojure.

### 7. Явные модификаторы вызова (`|l`, `|f`, `|seq`)

**Концепция:** Семантика вызова (логическая, функциональная) определяется явным модификатором на стороне вызывающего кода: `(grandparent|l ...)`.

#### За (Pros)

*   **Максимальная ясность и контроль:** Вызывающий код явно декларирует свое намерение.
*   **Прагматичность `|seq`:** Модификатор `|seq` — это прагматичное решение, материализующее ленивую последовательность решений в обычную коллекцию.
*   **Гибкость:** Позволяет гибко смешивать парадигмы.

#### Против (Cons)

*   **Синтаксический шум:** Обилие модификаторов может снижать читаемость.
*   **Повышенная ответственность разработчика:** Необходимо помнить о правилах применения модификаторов.

### 8. Расширяемость через модификаторы

**Концепция:** Архитектура позволяет легко добавлять новые модификаторы (`|fl`, `|lf`) для реализации смешанных стратегий вычисления.

#### За (Pros)
*   **Задел на будущее:** Систему можно легко расширять новыми стратегиями выполнения.
*   **Мощный механизм метапрограммирования:** Открывает возможности для создания кастомных семантик вызова.

#### Против (Cons)
*   **Риск избыточной сложности:** Большое количество модификаторов может сделать язык сложным.

### 9. Выбор модели хранения данных

**Концепция:** Расширение базовой модели PKVT (`Parent-Key-Value-Type`) до PKVTC с добавлением поля `:children` для прямых ссылок на дочерние элементы структуры.

#### За (Pros)
*   **Радикальное улучшение производительности:** Сложность унификации снижается с O(m×n×log s) до O(m×k), что критично для систем с миллиардами фактов.
*   **Экономия на индексах:** Уменьшение количества необходимых индексов более чем компенсирует рост основных данных — общая экономия 26% объема.
*   **Масштабируемость:** Линейная сложность вместо квадратичной обеспечивает предсказуемую производительность при росте данных.
*   **Прямая навигация:** O(1) доступ к дочерним элементам вместо O(n) поиска по связям.

#### Против (Cons)
*   **Увеличение объема основных данных:** На 25% больше полей в каждой записи.
*   **Снижение совместимости с реляционными БД:** Поле-массив `:children` поддерживается не во всех СУБД.
*   **Усложнение модели:** Добавление пятого поля нарушает элегантность четырехкомпонентной PKVT.

#### Детальное обоснование

Данное решение основано на количественном анализе пяти альтернативных моделей хранения по критериям производительности, компактности, простоты реализации и БД-совместимости. 

**Ключевые результаты анализа:**
- PKVTC показывает лучший общий балл: 8.6/10 против 6.6/10 у базовой PKVT
- Для систем с интенсивными логическими вычислениями выигрыш в производительности перевешивает недостатки
- Альтернативный вариант "PKVT + Materialized Paths" (7.5/10) рекомендуется для проектов с критичными требованиями к БД-совместимости

> **Подробный анализ:** Детальное сравнение всех моделей с количественными оценками представлено в [Анализе моделей хранения данных](./storage-model-analysis.md).

 