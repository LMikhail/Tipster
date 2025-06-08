(ns basic
  (:require [tipster.core :as tipster]))

(defn description
  "Возвращает описание примера"
  []
  {:title "🔹 Пример 1: Основы"
   :subtitle "Изучаем факты и простые запросы"
   :what-you-learn ["Как добавлять факты с deffact"
                    "Как делать запросы с query"
                    "Использование переменных (?X, ?Y)"
                    "Основной синтаксис Tipster"]
   :next "./scripts/run-example.sh family"})

(defn run-example
  "Запуск базового примера"
  []
  (println "\n🔹 Пример 1: Основы - факты и простые запросы")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  ;; Добавляем факты о людях
  (tipster/deffact (человек алиса))
  (tipster/deffact (человек боб))
  (tipster/deffact (человек чарли))
  
  ;; Добавляем факты о профессиях
  (tipster/deffact (профессия алиса программист))
  (tipster/deffact (профессия боб учитель))
  (tipster/deffact (профессия чарли врач))
  
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
