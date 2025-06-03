(ns tipster.core
  (:require [clojure.set :as set]))

;; === АРХИТЕКТУРА TIPSTER: ФУНКЦИОНАЛЬНО-ЛОГИЧЕСКИЙ ДВИЖОК ===

;; --- Система термов и представления данных ---

(defprotocol ITerm
  "Протокол для всех термов в Tipster"
  (term-type [this])
  (term-value [this])
  (is-variable? [this])
  (is-compound? [this]))

(defrecord Variable [name id]
  ITerm
  (term-type [_] :variable)
  (term-value [this] this)
  (is-variable? [_] true)
  (is-compound? [_] false))

(defrecord Atom [value]
  ITerm
  (term-type [_] :atom)
  (term-value [this] (:value this))
  (is-variable? [_] false)
  (is-compound? [_] false))

(defrecord Compound [functor args]
  ITerm
  (term-type [_] :compound)
  (term-value [this] this)
  (is-variable? [_] false)
  (is-compound? [_] true))

;; --- Конструкторы термов ---

(defn make-variable 
  "Создание переменной"
  ([name] (->Variable name (gensym name)))
  ([name id] (->Variable name id)))

(defn make-atom 
  "Создание атома"
  [value] 
  (->Atom value))

(defn make-compound 
  "Создание составного терма"
  [functor & args] 
  (->Compound functor (vec args)))

;; --- Преобразование типов ---

(declare clojure-term->tipster-term)

(defn auto-convert-term
  "Автоматическое преобразование в терм если нужно"
  [term]
  (if (satisfies? ITerm term)
    term
    (clojure-term->tipster-term term)))

(defn clojure-term->tipster-term 
  "Преобразование Clojure-данных в термы Tipster"
  [data]
  (cond
    (satisfies? ITerm data) data ; Уже терм Tipster
    
    (symbol? data) 
    (if (.startsWith (name data) "?")
      (make-variable (subs (name data) 1)) ; Убираем ? из имени переменной
      (make-atom data))
    
    (list? data)
    (if (empty? data)
      (make-atom data)
      (apply make-compound (first data) (map clojure-term->tipster-term (rest data))))
    
    (vector? data)
    (apply make-compound 'vector (map clojure-term->tipster-term data))
    
    :else 
    (make-atom data)))

;; --- Среда связывания переменных ---

(defn empty-bindings 
  "Пустая среда связывания"
  [] 
  {})

(defn lookup-binding 
  "Поиск связывания переменной"
  [var bindings]
  (get bindings (:id var)))

(defn bind-variable 
  "Связывание переменной с термом"
  [var term bindings]
  (assoc bindings (:id var) term))

(defn occurs-check 
  "Проверка вхождения переменной в терм (предотвращение бесконечных структур)"
  [var term bindings]
  (letfn [(occurs? [var term]
            (let [term (auto-convert-term term)]
              (cond
                (is-variable? term) 
                (if (= (:id var) (:id term))
                  true
                  (when-let [binding (lookup-binding term bindings)]
                    (occurs? var binding)))
                
                (is-compound? term)
                (some #(occurs? var %) (:args term))
                
                :else false)))]
    (occurs? var term)))

;; --- Дереференсация термов ---

(defn deref-term 
  "Дереференсация терма с учетом связываний"
  [term bindings]
  (cond
    (satisfies? ITerm term)
    (if (is-variable? term)
      (if-let [binding (lookup-binding term bindings)]
        (recur binding bindings)
        term)
      term)
    
    ;; Обрабатываем нативные типы Clojure
    :else term))

;; --- Алгоритм унификации Робинсона ---

(defn unify 
  "Унификация двух термов"
  ([term1 term2] (unify term1 term2 (empty-bindings)))
  ([term1 term2 bindings]
   (let [t1 (auto-convert-term (deref-term term1 bindings))
         t2 (auto-convert-term (deref-term term2 bindings))]
     (cond
       ;; Идентичные термы
       (= t1 t2) bindings
       
       ;; Унификация переменной с термом
       (is-variable? t1)
       (if (occurs-check t1 t2 bindings)
         nil ; Неудачная унификация из-за occurs check
         (bind-variable t1 t2 bindings))
       
       (is-variable? t2)
       (if (occurs-check t2 t1 bindings)
         nil
         (bind-variable t2 t1 bindings))
       
       ;; Унификация составных термов
       (and (is-compound? t1) (is-compound? t2))
       (if (and (= (:functor t1) (:functor t2))
                (= (count (:args t1)) (count (:args t2))))
         (reduce (fn [acc [arg1 arg2]]
                   (if acc
                     (unify arg1 arg2 acc)
                     nil))
                 bindings
                 (map vector (:args t1) (:args t2)))
         nil)
       
       ;; Унификация атомов
       (and (= (term-type t1) :atom) (= (term-type t2) :atom))
       (if (= (term-value t1) (term-value t2))
         bindings
         nil)
       
       ;; Неудачная унификация
       :else nil))))

;; --- База знаний ---

(defonce knowledge-base (atom {:facts #{} :rules #{}}))

(defn add-fact! 
  "Добавление факта в базу знаний"
  [fact]
  (swap! knowledge-base update :facts conj fact))

(defn add-rule! 
  "Добавление правила в базу знаний"
  [head body]
  (swap! knowledge-base update :rules conj {:head head :body body}))

(defn clear-knowledge-base! 
  "Очистка базы знаний"
  []
  (reset! knowledge-base {:facts #{} :rules #{}}))

;; --- Поиск подходящих фактов и правил ---

(defn matching-facts 
  "Поиск фактов, унифицируемых с целью"
  [goal bindings]
  (keep (fn [fact]
          (when-let [unified-bindings (unify goal fact bindings)]
            {:bindings unified-bindings :fact fact}))
        (:facts @knowledge-base)))

(defn rename-variables 
  "Переименование переменных в правиле для избежания конфликтов"
  [term]
  (let [term (auto-convert-term term)
        var-map (atom {})]
    (letfn [(rename [t]
              (let [t (auto-convert-term t)]
                (cond
                  (is-variable? t)
                  (if-let [renamed (@var-map (:id t))]
                    renamed
                    (let [new-var (make-variable (:name t))]
                      (swap! var-map assoc (:id t) new-var)
                      new-var))
                  
                  (is-compound? t)
                  (make-compound (:functor t) (map rename (:args t)))
                  
                  (coll? t) ; Обработка коллекций
                  (map rename t)
                  
                  :else t)))]
      (rename term))))

(defn matching-rules 
  "Поиск правил, унифицируемых с целью"
  [goal bindings]
  (keep (fn [rule]
          (let [rule-head (rename-variables (:head rule))
                rule-body (vec (rename-variables (:body rule)))]
            (when-let [unified-bindings (unify goal rule-head bindings)]
              {:bindings unified-bindings 
               :head rule-head 
               :body rule-body})))
        (:rules @knowledge-base)))

;; --- Логический солвер ---

(defn solve-goals 
  "Решение множества целей"
  ([goals] (solve-goals goals (empty-bindings)))
  ([goals bindings]
   (cond
     (empty? goals) 
     [bindings] ; Все цели решены
     
     :else
     (let [goal (first goals)
           remaining-goals (rest goals)]
       (lazy-cat
         ;; Поиск фактов
         (mapcat (fn [{:keys [bindings fact]}]
                   (solve-goals remaining-goals bindings))
                 (matching-facts goal bindings))
         
         ;; Поиск правил
         (mapcat (fn [{:keys [bindings body]}]
                   (solve-goals (concat body remaining-goals) bindings))
                 (matching-rules goal bindings)))))))

(defn solve-goal 
  "Решение одной цели"
  ([goal] (solve-goal goal (empty-bindings)))
  ([goal bindings]
   (solve-goals [goal] bindings)))

;; --- Интеграция с Clojure ---

(defn tipster-term->clojure-term 
  "Преобразование термов Tipster в Clojure-данные"
  [term bindings]
  (let [deref-term (deref-term term bindings)
        converted-term (auto-convert-term deref-term)]
    (cond
      (is-variable? converted-term) 
      (symbol (str "?" (:name converted-term)))
      
      (is-compound? converted-term)
      (let [functor (:functor converted-term)
            args (map #(tipster-term->clojure-term % bindings) (:args converted-term))]
        (if (= functor 'vector)
          (vec args)
          (cons functor args)))
      
      (satisfies? ITerm converted-term)
      (term-value converted-term)
      
      :else 
      converted-term)))

;; --- Макросы для удобной работы ---

(defmacro deffact 
  "Макрос для определения фактов"
  [fact-expr]
  `(add-fact! (clojure-term->tipster-term '~fact-expr)))

(defmacro defrule 
  "Макрос для определения правил"
  [head-expr body-exprs]
  `(add-rule! 
     (clojure-term->tipster-term '~head-expr)
     (map clojure-term->tipster-term '~body-exprs)))

(defmacro query 
  "Макрос для запросов"
  [goal-expr]
  `(let [goal# (clojure-term->tipster-term '~goal-expr)
         solutions# (solve-goal goal#)]
     (map #(tipster-term->clojure-term goal# %) solutions#)))

;; --- Функции для работы с предикатами Clojure ---

(defn integrate-clojure-predicate 
  "Интеграция предиката Clojure в систему Tipster"
  [predicate-fn]
  (fn [& args]
    (let [clojure-result (apply predicate-fn args)]
      (if clojure-result
        [(empty-bindings)] ; Успешное решение
        []))))             ; Неудачное решение

;; --- Встроенные предикаты ---

(defn builtin-predicates 
  "Встроенные предикаты для интеграции с Clojure"
  []
  {'= (fn [x y bindings]
        (if-let [unified (unify x y bindings)]
          [unified]
          []))
   'integer? (integrate-clojure-predicate integer?)
   'string? (integrate-clojure-predicate string?)
   'number? (integrate-clojure-predicate number?)
   '> (integrate-clojure-predicate >)
   '< (integrate-clojure-predicate <)
   '>= (integrate-clojure-predicate >=)
   '<= (integrate-clojure-predicate <=)})

;; --- Примеры использования ---

(defn demo-tipster []
  "Демонстрация возможностей Tipster"
  (println "=== Демонстрация Tipster ===")
  
  ;; Очистка базы знаний
  (clear-knowledge-base!)
  
  ;; Добавление фактов
  (deffact (человек алиса))
  (deffact (человек боб))
  (deffact (человек чарли))
  (deffact (родитель алиса боб))
  (deffact (родитель боб чарли))
  (deffact (возраст алиса 45))
  (deffact (возраст боб 25))
  (deffact (возраст чарли 5))
  
  ;; Добавление правил
  (defrule (дедушка ?X ?Z) [(родитель ?X ?Y) (родитель ?Y ?Z)])
  (defrule (взрослый ?X) [(человек ?X) (возраст ?X 45)])
  (defrule (взрослый ?X) [(человек ?X) (возраст ?X 25)])
  
  ;; Запросы
  (println "\nЗапрос: кто является человеком?")
  (doseq [result (query (человек ?X))]
    (println "  " result))
  
  (println "\nЗапрос: кто чей родитель?")
  (doseq [result (query (родитель ?X ?Y))]
    (println "  " result))
  
  (println "\nЗапрос: кто дедушка?")
  (doseq [result (query (дедушка ?X ?Y))]
    (println "  " result))
  
  (println "\nЗапрос: кто взрослый?")
  (doseq [result (query (взрослый ?X))]
    (println "  " result)))

;; --- Функции для тестирования ---

(defn reset-tipster! 
  "Сброс состояния Tipster"
  []
  (clear-knowledge-base!))

(defn run-tipster 
  "Запуск демонстрации Tipster"
  []
  (demo-tipster))

(defn -main [& args]
  (println "Tipster - функционально-логический движок")
  (println "Интеграция Clojure и логического программирования")
  (run-tipster))
