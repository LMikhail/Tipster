(ns math
  (:require [tipster.core :as tipster]
            [tipster.i18n :as i18n]))

(defn description
  "Example description / Возвращает описание примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (case lang
    :ru {:title "🔢 Пример 5: Математические отношения"
         :subtitle "Изучаем числовую логику"
         :what-you-learn ["Работа с числовыми данными"
                          "Математические отношения"
                          "Правила классификации чисел"
                          "Логические вычисления"]
         :next "./scripts/run-example.sh interactive"}
    :en {:title "🔢 Example 5: Mathematical Relations"
         :subtitle "Learning numerical logic"
         :what-you-learn ["Working with numerical data"
                          "Mathematical relationships"
                          "Number classification rules"
                          "Logical computations"]
         :next "./scripts/run-example.sh interactive"}))

(defn run-example
  "Run mathematical example / Запуск математического примера"
  [& {:keys [lang] :or {lang (i18n/detect-language)}}]
  (println "\n🔹 Пример 5: Математические отношения")
  (println (apply str (repeat 50 "=")))
  
  (tipster/reset-tipster!)
  
  ;; Числовые факты
  (tipster/deffact (число 1))
  (tipster/deffact (число 2))
  (tipster/deffact (число 3))
  (tipster/deffact (число 4))
  (tipster/deffact (число 5))
  
  ;; Математические отношения
  (tipster/deffact (больше 2 1))
  (tipster/deffact (больше 3 2))
  (tipster/deffact (больше 4 3))
  (tipster/deffact (больше 5 4))
  (tipster/deffact (больше 3 1))
  (tipster/deffact (больше 4 2))
  (tipster/deffact (больше 5 3))
  
  ;; Правила
  (tipster/defrule (четное 2) [])
  (tipster/defrule (четное 4) [])
  (tipster/defrule (нечетное 1) [])
  (tipster/defrule (нечетное 3) [])
  (tipster/defrule (нечетное 5) [])
  
  (tipster/defrule (положительное ?X) 
                   [(число ?X) (больше ?X 0)])
  
  (println "\n📝 Математическая система создана!")
  
  (println "\n❓ Запрос: Какие числа четные?")
  (println "   query: (четное ?X)")
  (doseq [result (tipster/query (четное ?X))]
    (println "   ✓ " result))
  
  (println "\n❓ Запрос: Что больше чего?")
  (println "   query: (больше ?X ?Y)")
  (doseq [result (take 5 (tipster/query (больше ?X ?Y)))]
    (println "   ✓ " result))
  
  (println "   ... (показаны первые 5)")) 
