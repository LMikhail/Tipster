(ns basic
  (:require [tipster.core :as tipster]
            [tipster.i18n :as i18n]))

(defn description
  "Example description / Возвращает описание примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (case lang
    :ru {:title "🔹 Пример 1: Основы"
         :subtitle "Изучаем факты и простые запросы"
         :what-you-learn ["Как добавлять факты с deffact"
                          "Как делать запросы с query"
                          "Использование переменных (?X, ?Y)"
                          "Основной синтаксис Tipster"]
         :next "./scripts/run-example.sh family"}
    :en {:title "🔹 Example 1: Basics"
         :subtitle "Learning facts and simple queries"
         :what-you-learn ["How to add facts with deffact"
                          "How to make queries with query"
                          "Using variables (?X, ?Y)"
                          "Basic Tipster syntax"]
         :next "./scripts/run-example.sh family"}))

(defn run-example
  "Run basic example / Запуск базового примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (let [desc (description :lang lang)]
    (println "\n" (:title desc))
    (println (apply str (repeat 50 "=")))
    
    (tipster/reset-tipster!)
    
    ;; Add facts about people / Добавляем факты о людях
    (tipster/deffact (человек алиса))
    (tipster/deffact (человек боб))
    (tipster/deffact (человек чарли))
    
    ;; Add facts about professions / Добавляем факты о профессиях
    (tipster/deffact (профессия алиса программист))
    (tipster/deffact (профессия боб учитель))
    (tipster/deffact (профессия чарли врач))
    
    (case lang
      :ru (do
            (println "\n📝 Добавили факты:")
            (println "   (человек алиса)")
            (println "   (человек боб)")  
            (println "   (человек чарли)")
            (println "   (профессия алиса программист)")
            (println "   (профессия боб учитель)")
            (println "   (профессия чарли врач)")
            
            (println "\n❓ Запрос: Кто является человеком?")
            (println "   query: (человек ?X)")
            (doseq [result (tipster/query (человек ?X))]
              (println "   ✓ " result))
            
            (println "\n❓ Запрос: Какие есть профессии?")
            (println "   query: (профессия ?Кто ?Что)")
            (doseq [result (tipster/query (профессия ?Кто ?Что))]
              (println "   ✓ " result)))
      :en (do
            (println "\n📝 Added facts:")
            (println "   (человек алиса)     ; (person alice)")
            (println "   (человек боб)       ; (person bob)")  
            (println "   (человек чарли)     ; (person charlie)")
            (println "   (профессия алиса программист)  ; (profession alice programmer)")
            (println "   (профессия боб учитель)        ; (profession bob teacher)")
            (println "   (профессия чарли врач)         ; (profession charlie doctor)")
            
            (println "\n❓ Query: Who is a person?")
            (println "   query: (человек ?X)   ; (person ?X)")
            (doseq [result (tipster/query (человек ?X))]
              (println "   ✓ " result))
            
            (println "\n❓ Query: What professions exist?")
            (println "   query: (профессия ?Кто ?Что)   ; (profession ?Who ?What)")
            (doseq [result (tipster/query (профессия ?Кто ?Что))]
              (println "   ✓ " result)))))) 
