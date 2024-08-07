;; Copyright 2023-2024 Ingy dot Net
;; This code is licensed under MIT license (See License for details)

(ns yamlscript.transformers
  (:require
   [yamlscript.util :refer [die if-lets when-lets]]
   [yamlscript.ast :refer [Sym Lst Vec Key]]))

(def Q {:Sym 'quote})

;;-----------------------------------------------------------------------------
;; cond
;;-----------------------------------------------------------------------------

(defn transform_cond [lhs rhs]
  (when-lets [forms (:forms rhs)
              len (count forms)
              _ (>= len 2)
              last-key-pos (- len 2)
              last-key (nth forms last-key-pos)
              _ (= '=> (:Sym last-key))
              rhs (update-in rhs [:forms last-key-pos] (fn [_] (Key "else")))]
    [lhs rhs]))

;;-----------------------------------------------------------------------------
;; defn and fn
;;-----------------------------------------------------------------------------

(defn transform_defn [lhs rhs]
  (when-lets [lhs (remove nil? lhs)
              lhs (vec lhs)
              _ (= 2 (count lhs))
              kind (get-in lhs [0 :Sym])
              _ (#{'defn 'fn} kind)
              pairs (:pairs rhs)
              _ (every? :Lst (->> pairs (partition 2) (map first)))
              pairs (reduce
                      (fn [acc [lhs rhs]]
                        (let [lhs (Vec (:Lst lhs))]
                          (conj acc lhs rhs)))
                      []
                      (partition 2 pairs))
              rhs {:pairs pairs}]
    [lhs rhs]))

(def dot-op (Sym '_->))
(defn transform_def [lhs rhs]
  (let [[a b c] lhs]
    (if (not c)
      [lhs rhs]
      (let [lhs [a b]
            op (:Sym c)
            op (or ({'|| 'or
                     '+ '+_
                     '* '*_
                     '. '_->} op)
                 op)
            op (Sym op)
            rhs (if (and (= op dot-op)
                      (= dot-op (get-in rhs [:Lst 0])))
                  (let [l (:Lst rhs)
                        op (first l)
                        tail (vec (drop 1 l))
                        tail (if-lets [l1 (get-in tail [0 :Lst])
                                       l1 (concat [(Sym 'list)] l1)]
                               (update-in tail [0 :Lst] (fn [_] l1))
                               tail)]
                    (Lst (concat [op b] tail)))
                  (Lst [op b rhs]))]
        [lhs rhs]))))

(defn transform_loop [lhs rhs]
  (let [lhs (if (= lhs (Sym 'loop))
              [lhs (Vec [])]
              lhs)]
    [lhs rhs]))

(defn transform_if [lhs rhs]
  (when-lets
    [pairs (:pairs rhs)
     [k1 v1 k2 v2] pairs
     _ (= k1 (Sym 'then))
     rhs (if (> (count (:pairs v1)) 2)
           (update-in rhs [:pairs 0] (fn [_] (Sym 'do)))
           (update-in rhs [:pairs 0] (fn [_] (Sym '=>))))]
    (if (> (count pairs) 2)
      (if (= k2 (Sym 'else))
        (if (> (count (:pairs v2)) 2)
          [lhs (update-in rhs [:pairs 2] (fn [_] (Sym 'do)))]
          [lhs (update-in rhs [:pairs 2] (fn [_] (Sym '=>)))])
        (die "Form after 'then' must be 'else'"))
      [lhs rhs])))

(defn transform_catch [lhs rhs]
  (let [lhs (cond
              (= lhs (Sym 'catch))
              [lhs (Sym 'Exception) (Sym '_e)]
              ,
              (= (count lhs) 2)
              [(first lhs) (Sym 'Exception) (second lhs)]
              ,
              :else lhs)]
    [lhs rhs]))

;;-----------------------------------------------------------------------------
;; require
;;-----------------------------------------------------------------------------

(def AS (Key "as"))
(def REFER (Key "refer"))

(defn require-spc-lhs? [lhs]
  (when-lets [sym (get-in lhs [0])
              _ (:Sym sym)
              spc (nth lhs 1)
              _ (or (:Spc spc) (:Sym spc))
              _ (= 2 (count lhs))]
    [sym spc]))

(defn require-args [rhs]
  (let [args []
        rhs (if (vector? rhs) rhs [rhs])
        [args rhs] (if-lets [_ (= '=> (get-in rhs [0 :Sym]))
                             alias (nth rhs 1)
                             _ (:Sym alias)]
                     [(conj args AS alias) (drop 2 rhs)]
                     [args rhs])
        args (if (seq rhs)
               (cond
                 (every? :Sym rhs) (conj args REFER (Vec rhs))
                 (= rhs [{:Key :all}]) (conj args REFER (first rhs))
                 :else (die "Invalid 'require' arguments"))
               args)]
    args))

(defn require-pairs [pairs]
  (reduce
    (fn [acc [spc rhs]]
      (or (:Spc spc) (:Sym spc)
        (die "Invalid 'require' pairs"))
      (let [args (if (nil? rhs)
                   (Lst [Q spc])
                   (if (= :all (:Key rhs))
                     (Lst [Q (Vec [spc REFER rhs])])
                     (Lst [Q (Vec (concat [spc] (require-args rhs)))])))]
        (conj acc args)))
    []
    (partition 2 pairs)))

(defn transform_require [lhs rhs]
  (or
    (when-lets [_ (:Sym lhs)
                _ (:Spc rhs)]
      [lhs (Lst [Q rhs])])

    (when-lets [[sym spc] (require-spc-lhs? lhs)
                _ (nil? rhs)]
      [sym (Lst [Q spc])])

    (when-lets [[sym spc] (require-spc-lhs? lhs)
                args (require-args rhs)]
      [sym (Lst [Q (Vec (concat [spc] args))])])

    (when-lets [_ (:Sym lhs)
                pairs (:pairs rhs)
                args (require-pairs pairs)]
      [lhs args])

    (die "Invalid 'require' form")))

(comment
  )
