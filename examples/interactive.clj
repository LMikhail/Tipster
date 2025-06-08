(ns interactive
  (:require [tipster.core :as tipster]))

(defn description
  "Возвращает описание примера"
  []
  {:title "🎓 Пример 6: Интерактивный режим"
   :subtitle "Изучаем сложные системы знаний"
   :what-you-learn ["Сложные базы знаний"
                    "Многоуровневые запросы"
                    "Практические применения"
                    "Готовность к созданию собственных систем"]
   :next "./scripts/repl.sh"})

(defn run-example
  "Запуск интерактивного примера"
  []
  (println "\n🔹 Пример 6: Интерактивный режим")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  (println "\n📝 Создаем базу знаний о университете...")
  
  ;; Студенты
  (tipster/deffact (студент иван))
  (tipster/deffact (студент мария))
  (tipster/deffact (студент петр))
  
  ;; Предметы
  (tipster/deffact (предмет математика))
  (tipster/deffact (предмет физика))
  (tipster/deffact (предмет программирование))
  
  ;; Изучение
  (tipster/deffact (изучает иван математика))
  (tipster/deffact (изучает иван программирование))
  (tipster/deffact (изучает мария физика))
  (tipster/deffact (изучает мария математика))
  (tipster/deffact (изучает петр программирование))
  
  ;; Оценки
  (tipster/deffact (оценка иван математика 5))
  (tipster/deffact (оценка мария физика 4))
  (tipster/deffact (оценка петр программирование 5))
  
  ;; Правила
  (tipster/defrule (отличник ?Студент) 
                   [(студент ?Студент) (оценка ?Студент ?Предмет 5)])
  
  (tipster/defrule (изучает-точные-науки ?Студент) 
                   [(изучает ?Студент математика)])
  
  (println "\n❓ Доступные запросы:")
  (println "   1. (студент ?X) - кто студент?")
  (println "   2. (изучает ?Кто ?Что) - кто что изучает?")
  (println "   3. (отличник ?X) - кто отличник?")
  (println "   4. (изучает-точные-науки ?X) - кто изучает точные науки?")
  
  (println "\n🔍 Примеры запросов:")
  
  (println "\n❓ Кто студент?")
  (doseq [result (tipster/query (студент ?X))]
    (println "   ✓ " result))
  
  (println "\n❓ Кто отличник?")
  (doseq [result (tipster/query (отличник ?X))]
    (println "   ✓ " result))) 
