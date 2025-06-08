(ns tipster.unification
  (:require [tipster.terms :as terms]
            [tipster.bindings :as bindings]))

;; === АЛГОРИТМ УНИФИКАЦИИ РОБИНСОНА ===

(defn unify 
  "Унификация двух термов"
  ([term1 term2] (unify term1 term2 (bindings/empty-bindings)))
  ([term1 term2 bindings]
   (let [t1 (terms/auto-convert-term (bindings/deref-term term1 bindings))
         t2 (terms/auto-convert-term (bindings/deref-term term2 bindings))]
     (cond
       ;; Идентичные термы
       (= t1 t2) bindings
       
       ;; Унификация переменной с термом
       (terms/is-variable? t1)
       (if (bindings/occurs-check t1 t2 bindings)
         nil ; Неудачная унификация из-за occurs check
         (bindings/bind-variable t1 t2 bindings))
       
       (terms/is-variable? t2)
       (if (bindings/occurs-check t2 t1 bindings)
         nil
         (bindings/bind-variable t2 t1 bindings))
       
       ;; Унификация составных термов
       (and (terms/is-compound? t1) (terms/is-compound? t2))
       (if (and (= (:functor t1) (:functor t2))
                (= (count (:args t1)) (count (:args t2))))
         (reduce (fn [acc [arg1 arg2]]
                   (if acc
                     (unify arg1 arg2 acc)
                     nil))
                 bindings
                 (map vector (:args t1) (:args t2)))
         nil)
       
       ;; Унификация атомов
       (and (= (terms/term-type t1) :atom) (= (terms/term-type t2) :atom))
       (if (= (terms/term-value t1) (terms/term-value t2))
         bindings
         nil)
       
       ;; Неудачная унификация
       :else nil))))
