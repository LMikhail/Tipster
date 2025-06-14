# Детальная техническая спецификация модуля terms (DDS)

### Архитектурный обзор

#### Определение протокола
```clojure
(defprotocol ITerm
  (term-type [this])      ; Возвращает :variable, :atom или :compound
  (term-value [this])     ; Возвращает фактическое значение
  (is-variable? [this])   ; Возвращает true для переменных
  (is-compound? [this]))  ; Возвращает true для составных термов
```

#### Структуры данных
1. **Variable**
   ```clojure
   (defrecord Variable [name id]
     ITerm
     (term-type [_] :variable)
     (term-value [this] this)
     (is-variable? [_] true)
     (is-compound? [_] false))
   ```
   - `name`: Строковый идентификатор переменной
   - `id`: Уникальный идентификатор, генерируемый `gensym`

2. **Atom**
   ```clojure
   (defrecord Atom [value]
     ITerm
     (term-type [_] :atom)
     (term-value [this] (:value this))
     (is-variable? [_] false)
     (is-compound? [_] false))
   ```
   - `value`: Любое значение Clojure

3. **Compound**
   ```clojure
   (defrecord Compound [functor args]
     ITerm
     (term-type [_] :compound)
     (term-value [this] this)
     (is-variable? [_] false)
     (is-compound? [_] true))
   ```
   - `functor`: Символ, представляющий функцию терма
   - `args`: Вектор аргументов (других термов)

### Основные функции

#### Создание термов
```clojure
(defn make-variable [name] (->Variable name (gensym name)))
(defn make-atom [value] (->Atom value))
(defn make-compound [functor & args] (->Compound functor (vec args)))
```

#### Преобразование типов
```clojure
(defn auto-convert-term [term]
  (if (satisfies? ITerm term)
    term
    (clojure-term->tipster-term term)))

(defn clojure-term->tipster-term [data]
  (cond
    (satisfies? ITerm data) data
    (symbol? data) (if (.startsWith (name data) "?")
                     (make-variable (subs (name data) 1))
                     (make-atom data))
    (list? data) (if (empty? data)
                   (make-atom data)
                   (apply make-compound (first data) 
                          (map clojure-term->tipster-term (rest data))))
    (vector? data) (apply make-compound 'vector 
                         (map clojure-term->tipster-term data))
    :else (make-atom data)))

(defn tipster-term->clojure-term [term bindings deref-fn]
  (let [deref-term (deref-fn term bindings)
        converted-term (auto-convert-term deref-term)]
    (cond
      (is-variable? converted-term) 
      (symbol (str "?" (:name converted-term)))
      (is-compound? converted-term)
      (let [functor (:functor converted-term)
            args (map #(tipster-term->clojure-term % bindings deref-fn) 
                     (:args converted-term))]
        (if (= functor 'vector)
          (vec args)
          (cons functor args)))
      (satisfies? ITerm converted-term)
      (term-value converted-term)
      :else converted-term)))
```

### Детали реализации

1. **Обработка переменных**
   - Переменные создаются с уникальными ID через `gensym`
   - Имена переменных сохраняются для отладки и отображения
   - Переменные могут быть разыменованы с помощью предоставленной `deref-fn`

2. **Преобразование типов**
   - Автоматическое преобразование из структур данных Clojure
   - Специальная обработка переменных (префикс '?')
   - Поддержка вложенных структур
   - Сохранение векторов через составные термы

3. **Соображения производительности**
   - Использование `defrecord` для эффективных структур данных
   - Составные термы на основе векторов для доступа O(1)
   - Минимальное создание объектов при преобразовании 
