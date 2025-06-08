(ns animals
  (:require [tipster.core :as tipster]))

(defn description
  "Возвращает описание примера"
  []
  {:title "🐱 Пример 4: Классификация животных"
   :subtitle "Изучаем систему классификации"
   :what-you-learn ["Построение систем классификации"
                    "Иерархические правила"
                    "Автоматическая категоризация"
                    "Системы экспертных знаний"]
   :next "./scripts/run-example.sh math"})

(defn run-example
  "Запуск примера классификации животных"
  []
  (println "\n🔹 Пример 4: Классификация животных")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  ;; Факты о животных
  (tipster/deffact (животное собака))
  (tipster/deffact (животное кошка))
  (tipster/deffact (животное попугай))
  (tipster/deffact (животное рыба))
  
  ;; Характеристики
  (tipster/deffact (имеет-шерсть собака))
  (tipster/deffact (имеет-шерсть кошка))
  (tipster/deffact (имеет-перья попугай))
  (tipster/deffact (живет-в-воде рыба))
  (tipster/deffact (может-летать попугай))
  
  ;; Правила классификации
  (tipster/defrule (млекопитающее ?X) 
                   [(животное ?X) (имеет-шерсть ?X)])
  
  (tipster/defrule (птица ?X) 
                   [(животное ?X) (имеет-перья ?X)])
  
  (tipster/defrule (летающее ?X) 
                   [(птица ?X) (может-летать ?X)])
  
  (tipster/defrule (домашнее ?X) 
                   [(млекопитающее ?X)])
  
  (println "\n📝 Система классификации животных создана!")
  
  (println "\n❓ Запрос: Какие животные млекопитающие?")
  (println "   query: (млекопитающее ?X)")
  (doseq [result (tipster/query (млекопитающее ?X))]
    (println "   ✓ " result))
  
  (println "\n❓ Запрос: Какие животные птицы?")
  (println "   query: (птица ?X)")
  (doseq [result (tipster/query (птица ?X))]
    (println "   ✓ " result))
  
  (println "\n❓ Запрос: Кто может летать?")
  (println "   query: (летающее ?X)")
  (doseq [result (tipster/query (летающее ?X))]
    (println "   ✓ " result))) 
