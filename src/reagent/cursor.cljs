;; copyright (c) 2014 Sean Corfield

(ns reagent.cursor
  "Optional cursor support for Reagent.

  (:require [reagent.cursor :as rc])

  (rc/cursor [:path :to :some :item] some-ratom)

  The cursor behaves in all ways like a regular Reagent atom except
  that it dereferences to the specified path inside the atom
  and updates to it, cause updates to the atom relative to the
  specified path.")

;; Implementation based on RAtom by delegation

(deftype RCursor [path ratom]
  IAtom

  IEquiv
  (-equiv [o other] (identical? o other))

  IDeref
  (-deref [this]
    (get-in @ratom path))

  IReset
  (-reset! [a new-value]
    (swap! ratom assoc-in path new-value))

  ISwap
  (-swap! [a f]
    (swap! ratom update-in path f))
  (-swap! [a f x]
    (swap! ratom update-in path f x))
  (-swap! [a f x y]
    (swap! ratom update-in path f x y))
  (-swap! [a f x y more]
    (swap! ratom update-in path f x y more))

  IMeta
  (-meta [_]
    (-meta ratom))

  IPrintWithWriter
  (-pr-writer [a writer opts]
    ;; not sure about how this should be implemented?
    ;; should it print as an atom focused on the appropriate part of
    ;; the ratom - (pr-writer (get-in @ratom path)) - or should it be
    ;; a completely separate type? and do we need a reader for it?
    (-write writer "#<Cursor: ")
    (pr-writer path writer opts)
    (-write writer " ")
    (pr-writer ratom writer opts)
    (-write writer ">"))

  IWatchable
  (-notify-watches [this oldval newval]
    (-notify-watches ratom oldval newval))
  (-add-watch [this key f]
    (-add-watch ratom key f))
  (-remove-watch [this key]
    (-remove-watch ratom key))

  IHash
  (-hash [this] (goog/getUid this)))

;; RCursor

(defn cursor
  "Provide a cursor into a Reagent atom.

Behaves like a Reagent atom but focuses updates and derefs to
the specified path within the wrapped Reagent atom. e.g.,
  (let [c (cursor [:nested :content] ra)]
    ... @c ;; equivalent to (get-in @ra [:nested :content])
    ... (reset! c 42) ;; equivalent to (swap! ra assoc-in [:nested :content] 42)
    ... (swap! c inc) ;; equivalence to (swap! ra update-in [:nested :content] inc)
    )"
  ([path] (fn [ra] (cursor path ra)))
  ([path ra] (RCursor. path ra)))
