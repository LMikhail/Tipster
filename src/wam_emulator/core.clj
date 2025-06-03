(ns wam-emulator.core)

;; --- Регистры ---
(def P (atom 0))         ; Program counter (индекс в коде)
(def CP (atom 0))        ; Continuation pointer (адрес возврата)
(def E (atom 0))         ; Environment pointer (указатель на стек)
(def B (atom -1))        ; Backtrack pointer (указатель на стек, -1 = нет точек выбора)
(def H (atom 0))         ; Heap pointer (индекс последнего занятого элемента)
(def HB (atom 0))        ; Heap backtrack pointer
(def S (atom 0))         ; Structure pointer (используется для унификации)
(def TR (atom 0))        ; Trail pointer

;; Аргументные регистры (X-регистры)
;; Будем использовать вектор для X-регистров, например, на 8 регистров
(def X (atom (vec (repeat 8 nil))))

;; --- Области памяти ---
(def heap (atom []))     ; Куча
(def stack (atom []))    ; Стек (для окружений и точек выбора)
(def trail (atom []))    ; Трейл (для отмены связываний)

;; Код программы (последовательность WAM-инструкций в EDN)
(def program-code (atom []))

;; --- Представление термов в куче/регистрах ---
;; Теги используются для определения типа данных, хранящихся в ячейке.
(defn ref-tag [addr]      {:tag :ref :addr addr})   ; Ссылка на другую ячейку (дереференсация)
(defn const-tag [val]     {:tag :const :value val})
(defn struct-tag [functor_name arity] {:tag :struct :functor functor_name :arity arity}) ; Начало структуры в куче. functor_name - символ, arity - число
(defn list-tag []         {:tag :list})            ; Начало списка в куче (аналогично структуре с функтором './2')


;; Вспомогательные функции для работы с регистрами X
(defn set-X! [i val]
  (swap! X assoc i val))

(defn get-X [i]
  (nth @X i))

;; Вспомогательные функции для работы с кучей
(defn heap-push! [val]
  (let [pushed-addr (count @heap)] ; Адрес, где будет значение
    (swap! heap conj val) ; Добавляем в конец
    (reset! H (count @heap)) ; H теперь следующий свободный слот
    pushed-addr)) ; Возвращаем адрес, куда было записано

(defn heap-get [addr]
  (nth @heap addr))

(defn heap-set! [addr val]
  (swap! heap assoc addr val))

;; Дереференсация термов
(defn wam-deref [val]
  (cond
    (not (map? val)) val ; Атомарное значение (число, строка, символ напрямую)

    (= (:tag val) :const) val
    (= (:tag val) :struct) val
    (= (:tag val) :list) val

    (= (:tag val) :ref)
    (wam-deref (heap-get (:addr val)))

    (= (:tag val) :var)
    (let [var-heap-cell-addr (:addr val)
          cell-in-heap (heap-get var-heap-cell-addr)]
      (if (and (map? cell-in-heap) (= (:tag cell-in-heap) :ref))
        (wam-deref cell-in-heap) ; Переменная связана, идем по ссылке в куче
        val)) ; Несвязанная переменная

    :else val))

;; Связывание переменной с использованием трейла
(defn bind-var! [var-heap-cell-addr target-heap-addr]
  ;; var-heap-cell-addr - адрес ячейки переменной в куче
  ;; target-heap-addr - адрес значения (или другой переменной), с которым связываем
  (heap-set! var-heap-cell-addr (ref-tag target-heap-addr))
  (swap! trail conj var-heap-cell-addr))

;; Backtracking при неудаче
(defn FAIL! []
  (println "FAIL! Initiating backtracking...")
  (if (neg? @B)
    (throw (ex-info "WAM FAIL! No choice points left." {:current_P @P}))
    (let [choice-point-addr @B
          choice-point (nth @stack choice-point-addr)]
      (reset! X (:registers choice-point))
      (reset! E (:E choice-point))
      (reset! CP (:CP choice-point))
      (reset! H (:H_on_stack choice-point))
      
      (let [tr-at-choice-point (:TR choice-point)]
        (loop [i (dec (count @trail))]
          (when (>= i tr-at-choice-point)
            (let [var_addr_to_unbind (nth @trail i)]
              ;; Восстанавливаем переменную в состояние "указывает на саму себя"
              (heap-set! var_addr_to_unbind {:tag :var :addr var_addr_to_unbind}))
            (recur (dec i))))
        (swap! trail subvec 0 tr-at-choice-point))
      (reset! TR (:TR choice-point)) ; TR должен быть восстановлен после очистки трейла

      (reset! B (:B choice-point))
      (reset! P (:next_clause_P choice-point))
      (println "Backtracking to P:" @P "Choice point:" choice-point-addr))))

;; Функция для сброса состояния WAM (первое, корректное определение)
(defn reset-wam! []
  (reset! P 0)
  (reset! CP 0)
  (reset! E 0)
  (reset! B -1)
  (reset! H 0) ; H = 0, так как heap будет пуст
  (reset! HB 0)
  (reset! S 0)
  (reset! TR 0)
  (reset! X (vec (repeat 8 nil)))
  (reset! heap [])
  (reset! stack [])
  (reset! trail [])
  (reset! program-code []))

;; Главный цикл интерпретатора
(defn execute-instruction [instruction]
  (let [[instruction-name & args] instruction]
    (println "Executing:" instruction)
    (case instruction-name
      :put_constant
      (let [[const-val reg-idx] args]
        (set-X! reg-idx (const-tag const-val))
        (swap! P inc))

      :get_constant
      (let [[const-to-match reg-idx] args
            val-in-reg (get-X reg-idx)
            deref-val (wam-deref val-in-reg)]
        (cond
          (and (map? deref-val) (= (:tag deref-val) :var))
          (let [var-heap-cell-addr (:addr deref-val)
                const-addr-in-heap (heap-push! (const-tag const-to-match))]
            (bind-var! var-heap-cell-addr const-addr-in-heap)
            (swap! P inc))

          (and (map? deref-val) (= (:tag deref-val) :const))
          (if (= (:value deref-val) const-to-match)
            (swap! P inc)
            (FAIL!))

          :else (FAIL!)))

      :proceed
      (if (pos? @CP)
        (reset! P @CP)
        (do 
          (println "WAM execution finished via proceed with CP=0.")
          ;; Чтобы цикл в run-wam завершился, установим P за пределы программы
          (reset! P (count @program-code)))))

      :try_me_else
      (let [[next-clause-addr] args
            choice-point {:registers @X
                          :E @E
                          :CP @CP
                          :B @B
                          :TR @TR
                          :H_on_stack @H
                          :next_clause_P next-clause-addr
                          :arity (count @X)}]
        (swap! stack conj choice-point)
        (reset! B (dec (count @stack)))
        (reset! HB @H)
        (swap! P inc))

      :get_variable
      (let [[_ reg-idx] args ; Имя переменной (например, 'X) используется для читаемости байткода
            ;; Создаем новую ячейку переменной в куче, которая указывает сама на себя
            var-heap-cell-addr (count @heap) ; Адрес, где будет новая ячейка
            _ (heap-push! {:tag :var :addr var-heap-cell-addr})] ; Помещаем ячейку в кучу
        ;; В регистр X помещаем терм, указывающий на эту ячейку в куче
        (set-X! reg-idx {:tag :var :addr var-heap-cell-addr})
        (swap! P inc))

      :call
      (let [[predicate-name _] args] ; n-vars пока не используем
        (reset! CP (inc @P))
        (case predicate-name
          'q-1 (reset! P 8)
          (FAIL!)))

      (do (println "Unknown instruction:" instruction-name)
          (FAIL!))))

(defn run-wam []
  (println "Starting WAM execution...")
  (reset! P 0)
  (loop []
    (let [current-P @P
          code @program-code]
      (if (< current-P (count code))
        (let [instruction (nth code current-P)]
          (execute-instruction instruction)
          (recur))
        (println "WAM execution loop finished (P exceeded program code).")))))


(defn -main [& args]
  (println "WAM Emulator starting...")
  (reset-wam!) ; Используем стандартизированный сброс
  (println "Initial X registers:" @X)
  (println "Heap size:" (count @heap))
  (println "H pointer:" @H)

  ;; Пример "скомпилированной" программы:
  ;; p(a).
  ;; p(X) :- q(X).
  ;; q(b).
  ;; query: p(Z).

  ;; Это очень упрощенный пример, как мог бы выглядеть "байткод"
  (reset! program-code
          [
           ;; p/1 (clause 1: p(a).)
           [:try_me_else 4] ; Адрес следующей клаузы для p/1 (или fail)
           [:get_constant 'a 0] ; X[0] = 'a
           [:proceed]

           ;; p/1 (clause 2: p(X) :- q(X).)
           [:retry_me_else 0] ;  (в реальности тут был бы trust_me_else_fail или адрес другой клаузы)
           [:get_variable 'X 0] ; Связать X[0] с новой переменной X (Y[0] в стеке)
           [:call 'q-1 0]       ; Вызвать q/1. 0 - нет перманентных переменных для сохранения в окружении сверх аргументов.
           [:proceed]          ; (deallocate будет тут если был allocate)

           ;; q/1 (clause 1: q(b).)
           ;; Адрес этой процедуры будет в таблице символов для 'q/1
           [:get_constant 'b 0]
           [:proceed]
           ])

  (println "Program loaded. Code size:" (count @program-code)))
