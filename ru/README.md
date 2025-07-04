# Tipster — функционально-логический язык программирования с двойной семантикой

[![Clojure](https://img.shields.io/badge/Clojure-1.12+-blue.svg)](https://clojure.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](../LICENSE)
[![Version](https://img.shields.io/badge/Version-0.0.3--dev-orange.svg)](../docs/ru/general/roadmap.md)

---

**Tipster** — инновационный язык программирования, объединяющий функциональное и логическое программирование через **двойную семантику**. Каждое выражение может быть одновременно вычислением (как в Clojure) и логическим фактом (как в Прологе), обеспечивая гибкость в создании интеллектуальных систем, баз знаний и сложной бизнес-логики.

## 🎯 Что делает Tipster уникальным?

**Инновация двойной семантики:**
```clojure
;; Одно выражение, двойная интерпретация:
(сотрудник "Иван" 30 "ИТ" 75000)

;; Как функция (вычислительная семантика):
(сотрудник "Иван" 30 "ИТ" 75000)  ; → создает запись о сотруднике

;; Как логический паттерн (логическая семантика):
(сотрудник ?имя ?возраст "ИТ" ?зарплата)  ; → находит всех ИТ-сотрудников

;; Объединенная мощь:
(->> (сотрудник ?имя ?возраст "ИТ" ?зарплата)
     (filter #(> (:возраст %) 25))
     (map #(рассчитать-премию (:зарплата %))))
```

**Никаких искусственных границ** между данными, функциями, правилами и запросами — всё живёт в едином пространстве знаний.

---

## 🚨 Текущий статус: v0.0.3 - Ранняя разработка

**Это стадия ранней разработки**, реализующая базовые возможности логического вывода. Текущая версия обеспечивает начальную основу для логического программирования, MVP v0.1.0 и полное видение двойной семантики придут в будущих релизах.

**✅ Что работает сейчас (v0.0.3):**
- Базовый алгоритм унификации с проверкой циклов
- Связывание переменных и логический вывод
- Факты, правила и запросы с сопоставлением паттернов
- 6 интерактивных примеров с подробной документацией
- Полная двуязычная поддержка (русский/английский)
- REPL-дружественная среда разработки

**🚀 Дорожная карта полного видения:** [../docs/ru/general/roadmap.md](../docs/ru/general/roadmap.md) | [../docs/en/general/roadmap.md](../docs/en/general/roadmap.md)

---

## 🌍 Двуязычный проект

Полная поддержка **русского** и **английского**:

- **Русский**: Вы его читаете! 🇷🇺  
- **English**: [../README.md](../README.md) 🇺🇸

Используйте `--lang ru` для русского интерфейса во всех инструментах.

---

## 🚀 Быстрый старт (30 секунд)

**Почувствуйте магию логического программирования:**

```bash
git clone <this-repo>
cd tipster
./start.sh
```

Это запустит интерактивные примеры, демонстрирующие логический вывод в действии!

**Пошаговое изучение:**
- Русский: [QUICKSTART.md](QUICKSTART.md)
- Английский: [../examples/README.md](../examples/README.md)

---

## 💡 Зачем нужен Tipster?

Рождён из реальных потребностей предприятий — интеграции разнородных бизнес-систем и автоматизации сложных процессов. Tipster устраняет "несоответствие импеданса" между различными парадигмами программирования и моделями данных.

**Основная философия:**
- **Единое пространство знаний**: факты, правила, функции и данные сосуществуют естественно
- **Двойная семантика**: каждое выражение работает как вычисление и как логический паттерн
- **Нулевое трение**: родной синтаксис Clojure, никаких DSL или ORM
- **Бесшовная интеграция**: полная совместимость с экосистемой JVM

---

## 🎨 Ключевые особенности

### 🔄 Двойная семантика
Каждое выражение-предикат служит двойной цели без специального синтаксиса:
```clojure
;; Определяем один раз, используем везде:
(defn клиент [имя id заказы])

;; Вычислительное использование:
(клиент "Алиса" 123 [...])

;; Логические запросы:
(клиент ?имя ?id ?заказы)
```

### 🧠 Единое пространство знаний (PKVT)
Все знания хранятся в кортежах Родитель-Ключ-Значение-Тип:
```clojure
{:parent "факт-1" :key "сотрудник" :value ["Иван" 30 "ИТ"] :type :fact}
{:parent "правило-1" :key "старший" :value "..." :type :rule}
{:parent "функция-1" :key "премия" :value "..." :type :function}
```

### 🔍 Универсальный поиск
Один комбинаторный движок поиска для:
- Логического вывода
- Запросов к данным  
- Вызовов функций
- Сопоставления паттернов

### 🌐 Естественные агрегации
Агрегации возникают естественно из логического вывода:
```clojure
;; Логический вывод находит решения:
(->> (сотрудник ?имя ?возраст ?отдел ?зарплата)
     (filter #(> (:возраст %) 30))
     (group-by :отдел)  ; Естественная группировка
     (map #(reduce + (map :зарплата %))))  ; Стандартные функции
```

---

## 📝 Текущий пример кода (v0.0.3)

```clojure
;; Факты о семейных отношениях
(deffact (человек алиса))
(deffact (человек боб))
(deffact (родитель алиса боб))

;; Логические правила
(defrule (дедушка ?X ?Z) 
         [(родитель ?X ?Y) (родитель ?Y ?Z)])

;; Запросы - автоматический логический вывод!
(query (человек ?кто))        ; → Найти всех людей
(query (дедушка ?x ?y))       ; → Найти отношения дедушка-внук
```

> **⚠️ Примечание об эволюции:** Макросы `deffact`/`defrule`/`query` — временные конструкции v0.0.x для ранней разработки. Начиная с MVP v0.1.0, они будут эволюционировать к двойной семантике, с полной реализацией в v0.2.0+, где любое выражение естественно служит как вычислительным, так и логическим целям.

---

## 🎓 Изучение и примеры

**Интерактивное обучение:**
```bash
./scripts/run-example.sh all     # Полный тур
./scripts/run-example.sh basic   # Факты и запросы
./scripts/run-example.sh family  # Отношения
./scripts/run-example.sh rules   # Логический вывод
```

**Экспериментирование:**
```bash
./scripts/repl.sh               # Интерактивный REPL
./scripts/debug.sh              # Режим отладки
```

**Ресурсы для изучения:**
- [QUICKSTART.md](QUICKSTART.md) - Быстрый старт (русский)
- [../examples/README.md](../examples/README.md) - Подробные примеры

---

## 🏗️ Эволюция архитектуры

### Текущее (v0.0.3): Ранняя разработка
- ✅ Базовый алгоритм унификации
- ✅ Простое связывание переменных  
- ✅ Минимальная база знаний
- ✅ Базовое сопоставление паттернов

### Ближайшее будущее (v0.1.0): MVP логической основы
- 🚀 Полный алгоритм унификации
- 🚀 Надёжная система связывания переменных
- 🚀 Комплексная база знаний
- 🚀 Продвинутое сопоставление паттернов

### Будущее (v0.2.0+): Двойная семантика
- 🚀 Универсальные комбинаторные движки поиска
- 🚀 Модульные стратегии поиска (backtrack, эвристический, breadth-first)
- 🚀 Истинная двойная семантика без специальных макросов

### Полное видение (v0.3.0+): Единая платформа знаний
- 🌟 Пространство знаний PKVT (1B+ объектов)
- 🌟 Прозрачная интеграция внешних данных
- 🌟 Распределённые комбинаторные машины
- 🌟 Enterprise-готовая платформа

---

## 🆚 Сравнение

|                      | Prolog        | SQL/NoSQL      | Tipster                    |
|----------------------|---------------|----------------|----------------------------|
| **Парадигма**        | Только логика | Только данные  | Логика + функциональность  |
| **Синтаксис**        | Prolog        | SQL/JSON       | Чистый Clojure/EDN         |
| **Модель данных**    | Факты/правила | Таблицы/документы | Единое пространство PKVT |
| **Запросы**          | Логические    | Декларативные  | Двойная семантика          |
| **Интеграция**       | Ограниченная  | ORM/API        | Родная JVM                 |
| **Агрегации**        | Ручные        | Встроенные     | Естественные из вывода     |
| **Порог вхождения**  | Крутой        | Средний        | Пологий (если знаете Clojure) |

**Tipster = Лучшее из всех миров, объединённое.**

---

## 📚 Документация

> **📋 Централизованная навигация:** Полная документация организована по разделам **general**, **concepts** и **terms** с удобной навигацией через [Портал документации](../docs/ru/README.md).

### Русский
- [📚 **Портал документации**](../docs/ru/README.md) - Вся документация с удобной навигацией
- [📖 Общее описание](../docs/ru/general/general_description.md) - Обзор проекта и видение
- [🏗️ Архитектура](../docs/ru/general/architecture.md) - Техническое погружение
- [🗺️ Дорожная карта](../docs/ru/general/roadmap.md) - План развития
- [🔧 Технические спецификации](../docs/ru/terms/) - Ключевые концепции, алгоритмы и спецификации
- [💡 Примеры](QUICKSTART.md) - Практическое изучение

### English  
- [📚 **Complete Documentation Portal**](../docs/en/README.md) - All documentation with easy navigation
- [📖 General Description](../docs/en/general/general_description.md) - Project overview and vision
- [🏗️ Architecture](../docs/en/general/architecture.md) - Technical deep dive
- [🗺️ Roadmap](../docs/en/general/roadmap.md) - Development plan
- [💡 Examples](../examples/README.md) - Hands-on learning

---

## 🤝 Участие в проекте

**Open Source и сообщество:**
- 📝 Приветствуются вопросы и обсуждения
- 🔧 Pull request'ы для функций и исправлений
- 🌟 Помогите формировать будущее языков программирования
- 📖 Вклад в документацию и примеры

**Области, где нужна помощь:**
- Оптимизация ядра движка
- Дополнительные примеры и туториалы
- Адаптеры интеграции
- Бенчмаркинг производительности

---

## 📄 Лицензия

Apache License 2.0 — свободно используйте, изменяйте и распространяйте с указанием авторства.

---

## 📞 Контакты и сообщество

- **Вопросы и обсуждения**: [GitHub Issues](https://github.com/LMikhail/Tipster/issues)
- **Предприятия и партнёрства**: Связь через профиль GitHub
- **Сообщество**: Присоединяйтесь к революции в языках программирования!

---

> **Tipster** — Где логика встречается с вычислениями, а знания становятся кодом. 🚀

---


