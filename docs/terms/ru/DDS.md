# Детальная спецификация структур данных (DDS) - Tipster Core

## 1. Назначение документа

Данный документ определяет **все структуры данных, протоколы и типы**, используемые в системе Tipster. Он служит технической референсной спецификацией для разработчиков, описывая точные детали реализации на уровне Clojure кода.

Документ связан с [Функциональной спецификацией](./functional_specification.md), которая определяет **поведение** системы, в то время как DDS определяет **структуру данных**.

---

## 2. Базовые протоколы (tipster.types)

### 2.1 Протокол двойной семантики

```clojure
(defprotocol IDualSemantics
  "Протокол для реализации двойной семантики термов (Φ_L и Φ_C)"
  (logical-eval [this context] 
    "Логическая интерпретация: терм как образец для унификации
     Возвращает: образец для сопоставления с фактами/правилами")
  (computational-eval [this context] 
    "Вычислительная интерпретация: терм как выражение для вычисления
     Возвращает: результат вычисления выражения"))
```

### 2.2 Протокол базовых термов

```clojure
(defprotocol ITerm
  "Базовый протокол для всех видов термов"
  (term-type [this] 
    "Возвращает тип терма: :variable | :constant | :compound")
  (is-ground? [this] 
    "Проверяет, является ли терм основным (без переменных)
     Возвращает: boolean")
  (variables [this] 
    "Возвращает множество всех переменных в терме
     Возвращает: #{Variable}")
  (subst [this substitution] 
    "Применяет подстановку к терму
     Параметры: substitution - {Variable -> Term}
     Возвращает: новый терм с примененной подстановкой"))
```

### 2.3 Протокол контекста выполнения

```clojure
(defprotocol IExecutionContext
  "Протокол для управления контекстом выполнения"
  (get-binding [this var] 
    "Получает значение переменной
     Возвращает: Term | nil")
  (add-binding [this var value] 
    "Добавляет привязку переменной (иммутабельно)
     Возвращает: новый контекст")
  (get-knowledge-base [this] 
    "Получает текущую базу знаний
     Возвращает: KnowledgeBase"))

(defrecord ExecutionContext [bindings knowledge-base]
  IExecutionContext
  (get-binding [this var] 
    (get (:bindings this) var))
  (add-binding [this var value] 
    (assoc this :bindings (assoc (:bindings this) var value)))
  (get-knowledge-base [this] 
    (:knowledge-base this)))
```

### 2.4 Базовые типы данных

```clojure
;; Подстановка - отображение переменных в термы
(def Substitution 
  "Тип для представления подстановок"
  '{Variable Term})

;; Результат унификации
(defrecord UnificationResult [success substitution]
  ;; success: boolean
  ;; substitution: Substitution | nil
  )

;; Решение запроса - набор привязок переменных
(def Solution
  "Тип для представления одного решения"
  '{Variable Term})
```

---

## 3. Алгоритмы декомпозиции термов (tipster.decomposition)

### 3.1 Базовые функции декомпозиции

```clojure
(defprotocol IDecomposable
  "Протокол для декомпозиции термов в унификационную форму"
  (decompose [this] 
    "Декомпозирует терм в структуру, оптимизированную для унификации
     Возвращает: {:type :constant/:variable/:compound 
                  :metadata {...}
                  :unified-form ...}")
  (to-pkvt [this parent-id] 
    "Преобразует декомпозированный терм в PKVT-записи
     Возвращает: [{:parent parent-id :key ... :value ... :type ...} ...]"))

(defn decompose-term
  "Основная функция декомпозиции термов"
  [term]
  (cond
    (variable? term) (decompose-variable term)
    (constant? term) (decompose-constant term)
    (seq? term) (decompose-relation term)
    (vector? term) (decompose-vector term)
    (map? term) (decompose-map term)
    (set? term) (decompose-set term)
    :else (throw (ex-info "Неизвестный тип терма" {:term term}))))

(defn decompose-variable [var-symbol]
  {:type :variable
   :name (name var-symbol)
   :id (gensym (name var-symbol))
   :unified-form var-symbol
   :metadata {:binding-eligible true}})

(defn decompose-constant [value]
  {:type :constant
   :value value
   :unified-form value
   :metadata {:value-type (type value)}})

(defn decompose-relation [relation-list]
  (let [functor (first relation-list)
        args (rest relation-list)]
    {:type :relation
     :functor functor
     :arity (count args)
     :args (mapv decompose-term args)
     :unified-form relation-list
     :metadata {:structural-signature [functor (count args)]
                :complexity (calculate-unification-complexity args)}}))

(defn decompose-vector [vector-term]
  {:type :vector
   :size (count vector-term)
   :elements (map-indexed 
              (fn [idx elem] 
                (assoc (decompose-term elem) :index idx))
              vector-term)
   :unified-form vector-term
   :metadata {:ordered true
              :indexable true}})

(defn decompose-map [map-term]
  {:type :map
   :keys (set (keys map-term))
   :pairs (mapv (fn [[k v]] 
                  {:key k 
                   :value (decompose-term v)})
                map-term)
   :unified-form map-term
   :metadata {:key-value-pairs true
              :unordered-keys true}})
```

### 3.2 Оптимизация для унификации

```clojure
(defn create-unification-index
  "Создает индекс для быстрого поиска унифицируемых термов"
  [decomposed-terms]
  (reduce 
   (fn [index term]
     (let [sig (:structural-signature (:metadata term))]
       (update-in index [sig] (fnil conj #{}) term)))
   {}
   decomposed-terms))

(defn fast-unify-check
  "Быстрая проверка совместимости термов перед полной унификацией"
  [term1 term2]
  (and (= (:type term1) (:type term2))
       (case (:type term1)
         :relation (= (:functor term1) (:functor term2))
         :vector (= (:size term1) (:size term2))
         :map (clojure.set/subset? 
               (clojure.set/intersection (:keys term1) (:keys term2))
               (clojure.set/union (:keys term1) (:keys term2)))
         true)))

(defn calculate-unification-complexity
  "Вычисляет ожидаемую сложность унификации"
  [args]
  (reduce + (map 
             #(case (:type %)
                :constant 1
                :variable 1
                :compound (* 2 (count (:args %)))
                :vector (count (:elements %))
                :map (* 2 (count (:pairs %))))
             args)))
```

### 3.3 PKVT-интеграция

```clojure
(defn term-to-pkvt-records
  "Преобразует декомпозированный терм в PKVT-записи"
  [decomposed-term parent-id]
  (let [base-records [{:parent parent-id 
                       :key :type 
                       :value (:type decomposed-term) 
                       :type :keyword}]]
    (case (:type decomposed-term)
      :constant 
      (conj base-records 
            {:parent parent-id :key :value :value (:value decomposed-term) 
             :type (keyword (type (:value decomposed-term)))})
      
      :variable
      (conj base-records
            {:parent parent-id :key :name :value (:name decomposed-term) :type :string}
            {:parent parent-id :key :id :value (:id decomposed-term) :type :symbol})
      
      :relation
      (concat base-records
              [{:parent parent-id :key :functor :value (:functor decomposed-term) :type :symbol}
               {:parent parent-id :key :arity :value (:arity decomposed-term) :type :number}]
              (map-indexed 
               (fn [idx arg]
                 (let [arg-id (str parent-id "-arg-" idx)]
                   {:parent parent-id :key (keyword (str "arg-" idx)) 
                    :value arg-id :type :reference}))
               (:args decomposed-term)))
      
      :vector
      (concat base-records
              [{:parent parent-id :key :size :value (:size decomposed-term) :type :number}]
              (map-indexed
               (fn [idx elem]
                 (let [elem-id (str parent-id "-elem-" idx)]
                   {:parent parent-id :key (keyword (str "elem-" idx))
                    :value elem-id :type :reference}))
               (:elements decomposed-term)))
      
      :map
      (concat base-records
              (mapv (fn [{:keys [key value]}]
                      (let [value-id (str parent-id "-" (name key))]
                        {:parent parent-id :key key :value value-id :type :reference}))
                    (:pairs decomposed-term))))))

(defn pkvt-records-to-term
  "Восстанавливает терм из PKVT-записей"
  [records parent-id]
  (let [grouped (group-by :key records)
        term-type (:value (first (grouped :type)))]
    (case term-type
      :constant (:value (first (grouped :value)))
      :variable (symbol (:value (first (grouped :name))))
      :relation (let [functor (:value (first (grouped :functor)))
                      arity (:value (first (grouped :arity)))
                      args (for [i (range arity)]
                             (let [arg-ref (:value (first (grouped (keyword (str "arg-" i)))))]
                               (pkvt-records-to-term 
                                (filter #(= (:parent %) arg-ref) records)
                                arg-ref)))]
                  (cons functor args))
      ;; ... аналогично для :vector и :map
      )))
```

---

## 4. Структуры термов (tipster.terms)

### 3.1 Переменная

```clojure
(defrecord Variable [name id]
  ITerm
  (term-type [_] :variable)
  (is-ground? [_] false)
  (variables [this] #{this})
  (subst [this substitution] 
    (get substitution this this))
  
  IDualSemantics
  (logical-eval [this context] 
    ;; В логическом контексте переменная - это образец для связывания
    this)
  (computational-eval [this context] 
    ;; В вычислительном контексте - получаем значение из контекста
    (or (get-binding context this) this))
  
  Object
  (toString [this] (str "?" (:name this)))
  (equals [this other] 
    (and (instance? Variable other) (= (:id this) (:id other))))
  (hashCode [this] (hash (:id this))))

;; Конструктор переменной
(defn make-variable 
  ([name] (->Variable (str name) (gensym name)))
  ([name id] (->Variable (str name) id)))
```

### 3.2 Константа

```clojure
(defrecord Constant [value]
  ITerm
  (term-type [_] :constant)
  (is-ground? [_] true)
  (variables [_] #{})
  (subst [this _] this)
  
  IDualSemantics
  (logical-eval [this _] 
    ;; В логическом контексте константа сопоставляется сама с собой
    this)
  (computational-eval [this _] 
    ;; В вычислительном контексте константа возвращает свое значение
    (:value this))
  
  Object
  (toString [this] (str (:value this)))
  (equals [this other] 
    (and (instance? Constant other) (= (:value this) (:value other))))
  (hashCode [this] (hash (:value this))))

;; Конструктор константы
(defn make-constant [value] 
  (->Constant value))
```

### 3.3 Составной терм

```clojure
(defrecord Compound [functor args]
  ITerm
  (term-type [_] :compound)
  (is-ground? [this] 
    (every? is-ground? (:args this)))
  (variables [this] 
    (reduce into #{} (map variables (:args this))))
  (subst [this substitution] 
    (->Compound (:functor this) 
                (mapv #(subst % substitution) (:args this))))
  
  IDualSemantics
  (logical-eval [this context] 
    ;; В логическом контексте - образец для унификации
    this)
  (computational-eval [this context] 
    ;; В вычислительном контексте - вызов функции
    (let [fn-symbol (:functor this)
          evaluated-args (map #(computational-eval % context) (:args this))]
      (if-let [fn-var (resolve fn-symbol)]
        (apply @fn-var evaluated-args)
        (throw (ex-info "Неизвестная функция" {:functor fn-symbol})))))
  
  Object
  (toString [this] 
    (str "(" (:functor this) " " (clojure.string/join " " (:args this)) ")"))
  (equals [this other] 
    (and (instance? Compound other) 
         (= (:functor this) (:functor other))
         (= (:args this) (:args other))))
  (hashCode [this] 
    (hash [(:functor this) (:args this)])))

;; Конструктор составного терма
(defn make-compound [functor & args] 
  (->Compound functor (vec args)))
```

---

## 4. Структуры унификации (tipster.unification)

### 4.1 Результат унификации

```clojure
(defrecord UnificationResult [success substitution]
  Object
  (toString [this] 
    (if (:success this)
      (str "SUCCESS: " (:substitution this))
      "FAILURE")))

(defn success [substitution] 
  (->UnificationResult true substitution))

(defn failure [] 
  (->UnificationResult false nil))
```

### 4.2 Система подстановок

```clojure
;; Подстановка представляется как обычная Clojure map
(def empty-substitution {})

(defn compose-substitutions 
  "Композиция двух подстановок: (σ₁ ∘ σ₂)"
  [subst1 subst2]
  (merge (into {} (map (fn [[var term]] 
                         [var (apply-substitution term subst1)]) 
                       subst2))
         subst1))

(defn apply-substitution 
  "Применяет подстановку к терму"
  [term substitution]
  (subst term substitution))

(defn occurs-check 
  "Проверяет, встречается ли переменная в терме (предотвращает циклы)"
  [var term]
  (contains? (variables term) var))
```

---

## 5. Структуры решателя (tipster.solver)

### 5.1 Точка выбора

```clojure
(defrecord ChoicePoint [alternatives context goal]
  ;; alternatives: ленивая последовательность альтернатив для бэктрекинга
  ;; context: текущий контекст выполнения
  ;; goal: цель, для которой создана точка выбора
  )

(defn make-choice-point [alternatives context goal]
  (->ChoicePoint alternatives context goal))
```

### 5.2 Состояние решателя

```clojure
(defrecord SolverState [goals context choice-points]
  ;; goals: стек текущих целей для решения
  ;; context: текущий контекст выполнения
  ;; choice-points: стек точек выбора для бэктрекинга
  )

(defn make-initial-state [goal knowledge-base]
  (->SolverState [goal] 
                 (->ExecutionContext {} knowledge-base) 
                 []))
```

### 5.3 Результат решения

```clojure
(defrecord SolutionStream [solutions state]
  ;; solutions: ленивая последовательность решений
  ;; state: финальное состояние решателя
  )
```

---

## 6. Структуры базы знаний (tipster.knowledge)

### 6.1 База знаний

```clojure
(defrecord KnowledgeBase [facts rules indexes]
  ;; facts: множество фактов #{Term}
  ;; rules: множество правил #{Rule}
  ;; indexes: индексы для быстрого поиска
  )

(defn empty-knowledge-base []
  (->KnowledgeBase #{} #{} {}))
```

### 6.2 Правило

```clojure
(defrecord Rule [head body]
  ;; head: терм-голова правила
  ;; body: последовательность термов-целей в теле правила
  Object
  (toString [this] 
    (str (:head this) " :- " (clojure.string/join ", " (:body this)))))

(defn make-rule [head & body]
  (->Rule head (vec body)))
```

### 6.3 Индексы

```clojure
(defrecord FunctorIndex [functor-map]
  ;; functor-map: {[functor arity] -> #{Term}}
  )

(defn build-functor-index [terms]
  (->FunctorIndex 
    (group-by (fn [term] 
                [(if (= :compound (term-type term)) 
                   (:functor term) 
                   :constant)
                 (if (= :compound (term-type term)) 
                   (count (:args term)) 
                   0)]) 
              terms)))
```

---

## 7. Структуры API (tipster.core)

### 7.1 Конфигурация запроса

```clojure
(defrecord QueryOptions [limit timeout strategy]
  ;; limit: максимальное количество решений (nil = все)
  ;; timeout: таймаут в миллисекундах (nil = без ограничений)  
  ;; strategy: стратегия поиска (:depth-first, :breadth-first)
  )

(def default-query-options 
  (->QueryOptions nil nil :depth-first))
```

### 7.2 Результат запроса

```clojure
(defrecord QueryResult [solutions metadata]
  ;; solutions: последовательность решений
  ;; metadata: информация о выполнении (время, количество шагов и т.д.)
  )
```

---

## 8. Утилитарные типы

### 8.1 Ошибки и исключения

```clojure
(defn tipster-error [type message data]
  (ex-info message (merge {:type type} data)))

;; Типы ошибок
(def error-types
  {:unification-failure "Ошибка унификации"
   :infinite-loop "Обнаружен бесконечный цикл"
   :unknown-predicate "Неизвестный предикат"
   :syntax-error "Синтаксическая ошибка"})
```

### 8.2 Метаданные и отладка

```clojure
(defrecord DebugInfo [step-count backtrack-count time-ms]
  ;; step-count: количество шагов решения
  ;; backtrack-count: количество откатов
  ;; time-ms: время выполнения в миллисекундах
  )
```

---

## 9. Валидация и спецификации

### 9.1 Clojure spec для основных типов

```clojure
(require '[clojure.spec.alpha :as s])

(s/def ::variable (partial instance? Variable))
(s/def ::constant (partial instance? Constant))
(s/def ::compound (partial instance? Compound))
(s/def ::term (s/or :var ::variable :const ::constant :comp ::compound))

(s/def ::substitution (s/map-of ::variable ::term))
(s/def ::solution ::substitution)

(s/def ::knowledge-base (partial instance? KnowledgeBase))
(s/def ::execution-context (partial instance? ExecutionContext))
```

### 9.2 Инварианты и ограничения

```clojure
;; Инварианты для валидации
(defn valid-term? [term]
  (satisfies? ITerm term))

(defn valid-substitution? [subst]
  (and (map? subst)
       (every? #(instance? Variable %) (keys subst))
       (every? valid-term? (vals subst))))

(defn acyclic-substitution? [subst]
  (not-any? (fn [[var term]] (occurs-check var term)) subst))
```
