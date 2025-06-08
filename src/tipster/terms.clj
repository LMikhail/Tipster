(ns tipster.terms)

;; === СИСТЕМА ТЕРМОВ И ПРЕДСТАВЛЕНИЯ ДАННЫХ ===

;; --- Протокол для всех термов ---

(defprotocol ITerm
  "Протокол для всех термов в Tipster"
  (term-type [this])
  (term-value [this])
  (is-variable? [this])
  (is-compound? [this]))

;; --- Типы данных ---

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

(defn tipster-term->clojure-term 
  "Преобразование термов Tipster в Clojure-данные"
  [term bindings deref-fn]
  (let [deref-term (deref-fn term bindings)
        converted-term (auto-convert-term deref-term)]
    (cond
      (is-variable? converted-term) 
      (symbol (str "?" (:name converted-term)))
      
      (is-compound? converted-term)
      (let [functor (:functor converted-term)
            args (map #(tipster-term->clojure-term % bindings deref-fn) (:args converted-term))]
        (if (= functor 'vector)
          (vec args)
          (cons functor args)))
      
      (satisfies? ITerm converted-term)
      (term-value converted-term)
      
      :else 
      converted-term)))
