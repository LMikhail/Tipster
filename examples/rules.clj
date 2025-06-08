(ns rules
  (:require [tipster.core :as tipster]
            [tipster.i18n :as i18n]))

(defn description
  "Example description / Возвращает описание примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (case lang
    :ru {:title "🧠 Пример 3: Правила и логический вывод"
         :subtitle "Изучаем мощь логического программирования"
         :what-you-learn ["Создание правил с defrule"
                          "Логический вывод новых фактов"
                          "Цепочки правил и производные правила"
                          "Условия в правилах"]
         :next "./scripts/run-example.sh animals"}
    :en {:title "🧠 Example 3: Rules and Logical Inference"
         :subtitle "Learning the power of logic programming"
         :what-you-learn ["Creating rules with defrule"
                          "Logical inference of new facts"
                          "Rule chains and derived rules"
                          "Conditions in rules"]
         :next "./scripts/run-example.sh animals"}))

(defn run-example
  "Run rules and logical inference example / Запуск примера правил и логического вывода"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (let [desc (description :lang lang)]
    (println "\n" (:title desc))
    (println (apply str (repeat 50 "=")))
    
    (tipster/reset-tipster!)
    
    ;; Facts / Факты
    (tipster/deffact (родитель алиса боб))
    (tipster/deffact (родитель боб чарли))
    (tipster/deffact (родитель чарли дэвид))
    (tipster/deffact (мужчина боб))
    (tipster/deffact (мужчина чарли))
    (tipster/deffact (женщина алиса))
    
    ;; Logical inference rules / Правила логического вывода
    (tipster/defrule (дедушка ?X ?Z) 
                     [(родитель ?X ?Y) (родитель ?Y ?Z) (мужчина ?X)])
    
    (tipster/defrule (бабушка ?X ?Z) 
                     [(родитель ?X ?Y) (родитель ?Y ?Z) (женщина ?X)])
    
    (tipster/defrule (внук ?Z ?X) 
                     [(дедушка ?X ?Z)])
    
    (case lang
      :ru (do
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
      :en (do
            (println "\n📝 Added facts + rules:")
            (println "   Rule: (дедушка ?X ?Z) :- (родитель ?X ?Y), (родитель ?Y ?Z), (мужчина ?X)")
            (println "         ; (grandfather ?X ?Z) :- (parent ?X ?Y), (parent ?Y ?Z), (male ?X)")
            (println "   Rule: (бабушка ?X ?Z) :- (родитель ?X ?Y), (родитель ?Y ?Z), (женщина ?X)")
            (println "         ; (grandmother ?X ?Z) :- (parent ?X ?Y), (parent ?Y ?Z), (female ?X)")
            (println "   Rule: (внук ?Z ?X) :- (дедушка ?X ?Z)")
            (println "         ; (grandson ?Z ?X) :- (grandfather ?X ?Z)")
            
            (println "\n❓ Query: Who is grandfather? (rule)")
            (println "   query: (дедушка ?X ?Y)   ; (grandfather ?X ?Y)")
            (doseq [result (tipster/query (дедушка ?X ?Y))]
              (println "   ✓ " result))
            
            (println "\n❓ Query: Who is grandmother? (rule)")
            (println "   query: (бабушка ?X ?Y)   ; (grandmother ?X ?Y)")
            (doseq [result (tipster/query (бабушка ?X ?Y))]
              (println "   ✓ " result))
            
            (println "\n❓ Query: Who is grandson? (derived rule)")
            (println "   query: (внук ?X ?Y)   ; (grandson ?X ?Y)")
            (doseq [result (tipster/query (внук ?X ?Y))]
              (println "   ✓ " result)))))) 
