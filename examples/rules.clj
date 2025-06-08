(ns rules
  (:require [tipster.core :as tipster]))

(defn description
  "Возвращает описание примера"
  []
  {:title "🧠 Пример 3: Правила и логический вывод"
   :subtitle "Изучаем мощь логического программирования"
   :what-you-learn ["Создание правил с defrule"
                    "Логический вывод новых фактов"
                    "Цепочки правил и производные правила"
                    "Условия в правилах"]
   :next "./scripts/run-example.sh animals"})

(defn run-example
  "Запуск примера правил и логического вывода"
  []
  (println "\n🔹 Пример 3: Правила и логический вывод")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  ;; Факты
  (tipster/deffact (родитель алиса боб))
  (tipster/deffact (родитель боб чарли))
  (tipster/deffact (родитель чарли дэвид))
  (tipster/deffact (мужчина боб))
  (tipster/deffact (мужчина чарли))
  (tipster/deffact (женщина алиса))
  
  ;; Правила логического вывода
  (tipster/defrule (дедушка ?X ?Z) 
                   [(родитель ?X ?Y) (родитель ?Y ?Z) (мужчина ?X)])
  
  (tipster/defrule (бабушка ?X ?Z) 
                   [(родитель ?X ?Y) (родитель ?Y ?Z) (женщина ?X)])
  
  (tipster/defrule (внук ?Z ?X) 
                   [(дедушка ?X ?Z)])
  
  (println "\n📝 Добавили факты + правила:")
  (println "   Правило: (дедушка ?X ?Z) :- (родитель ?X ?Y), (родитель ?Y ?Z), (мужчина ?X)")
  (println "   Правило: (бабушка ?X ?Z) :- (родитель ?X ?Y), (родитель ?Y ?Z), (женщина ?X)")
  (println "   Правило: (внук ?Z ?X) :- (дедушка ?X ?Z)")
  
  (println "\n❓ Запрос: Кто дедушка? (правило)")
  (println "   query: (дедушка ?X ?Y)")
  (doseq [result (tipster/query (дедушка ?X ?Y))]
    (println "   ✓ " result))
  
  (println "\n❓ Запрос: Кто бабушка? (правило)")
  (println "   query: (бабушка ?X ?Y)")
  (doseq [result (tipster/query (бабушка ?X ?Y))]
    (println "   ✓ " result))
  
  (println "\n❓ Запрос: Кто внук? (производное правило)")
  (println "   query: (внук ?X ?Y)")
  (doseq [result (tipster/query (внук ?X ?Y))]
    (println "   ✓ " result))) 
