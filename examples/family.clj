(ns family
  (:require [tipster.core :as tipster]
            [tipster.i18n :as i18n]))

(defn description
  "Example description / Возвращает описание примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (case lang
    :ru {:title "👨‍👩‍👧‍👦 Пример 2: Семейные отношения"
         :subtitle "Изучаем связи между данными"
         :what-you-learn ["Создание связанных фактов"
                          "Использование составных предикатов"
                          "Запросы с несколькими переменными"
                          "Моделирование реальных отношений"]
         :next "./scripts/run-example.sh rules"}
    :en {:title "👨‍👩‍👧‍👦 Example 2: Family Relationships"
         :subtitle "Learning connections between data"
         :what-you-learn ["Creating connected facts"
                          "Using compound predicates"
                          "Queries with multiple variables"
                          "Modeling real-world relationships"]
         :next "./scripts/run-example.sh rules"}))

(defn run-example
  "Run family relationships example / Запуск примера семейных отношений"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (let [desc (description :lang lang)]
    (println "\n" (:title desc))
    (println (apply str (repeat 50 "=")))
    
    (tipster/reset-tipster!)
    
    ;; Family facts / Факты о семье
    (tipster/deffact (родитель алиса боб))
    (tipster/deffact (родитель боб чарли))
    (tipster/deffact (родитель боб дэвид))
    (tipster/deffact (родитель чарли ева))
    
    ;; Gender facts / Факты о поле
    (tipster/deffact (мужчина боб))
    (tipster/deffact (мужчина чарли))
    (tipster/deffact (мужчина дэвид))
    (tipster/deffact (женщина алиса))
    (tipster/deffact (женщина ева))
    
    (case lang
      :ru (do
            (println "\n📝 Добавили факты о семье:")
            (println "   (родитель алиса боб)")
            (println "   (родитель боб чарли)")
            (println "   (родитель боб дэвид)")
            (println "   (родитель чарли ева)")
            (println "   + факты о поле")
            
            (println "\n❓ Запрос: Кто чей родитель?")
            (println "   query: (родитель ?X ?Y)")
            (doseq [result (tipster/query (родитель ?X ?Y))]
              (println "   ✓ " result))
            
            (println "\n❓ Запрос: Кто мужчина?")
            (println "   query: (мужчина ?X)")
            (doseq [result (tipster/query (мужчина ?X))]
              (println "   ✓ " result)))
      :en (do
            (println "\n📝 Added family facts:")
            (println "   (родитель алиса боб)     ; (parent alice bob)")
            (println "   (родитель боб чарли)     ; (parent bob charlie)")
            (println "   (родитель боб дэвид)     ; (parent bob david)")
            (println "   (родитель чарли ева)     ; (parent charlie eva)")
            (println "   + gender facts")
            
            (println "\n❓ Query: Who is whose parent?")
            (println "   query: (родитель ?X ?Y)   ; (parent ?X ?Y)")
            (doseq [result (tipster/query (родитель ?X ?Y))]
              (println "   ✓ " result))
            
            (println "\n❓ Query: Who is male?")
            (println "   query: (мужчина ?X)   ; (male ?X)")
            (doseq [result (tipster/query (мужчина ?X))]
              (println "   ✓ " result)))))) 
