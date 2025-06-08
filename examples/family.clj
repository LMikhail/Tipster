(ns family
  (:require [tipster.core :as tipster]))

(defn description
  "Возвращает описание примера"
  []
  {:title "👨‍👩‍👧‍👦 Пример 2: Семейные отношения"
   :subtitle "Изучаем связи между данными"
   :what-you-learn ["Создание связанных фактов"
                    "Использование составных предикатов"
                    "Запросы с несколькими переменными"
                    "Моделирование реальных отношений"]
   :next "./scripts/run-example.sh rules"})

(defn run-example
  "Запуск примера семейных отношений"
  []
  (println "\n🔹 Пример 2: Семейные отношения")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  ;; Факты о семье
  (tipster/deffact (родитель алиса боб))
  (tipster/deffact (родитель боб чарли))
  (tipster/deffact (родитель боб дэвид))
  (tipster/deffact (родитель чарли ева))
  
  ;; Факты о поле
  (tipster/deffact (мужчина боб))
  (tipster/deffact (мужчина чарли))
  (tipster/deffact (мужчина дэвид))
  (tipster/deffact (женщина алиса))
  (tipster/deffact (женщина ева))
  
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
