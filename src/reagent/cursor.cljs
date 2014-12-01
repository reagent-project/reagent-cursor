;; copyright (c) 2014 Sean Corfield

(ns reagent.cursor
  "Optional cursor support for Reagent.

  (:require [reagent.cursor :as rc])

  (rc/cursor some-ratom [:path :to :some :item])

  The cursor behaves in all ways like a regular Reagent atom except
  that it dereferences to the specified path inside the atom
  and updates to it, cause updates to the atom relative to the
  specified path.")

;; Implementation based on RAtom by delegation

(defprotocol IRoot
  (-root [c]))

(defprotocol IPath
  (-path [c]))

(deftype RCursor [ratom path]
  IAtom

  IRoot
  (-root [_] ratom)

  IPath
  (-path [_] path)
  
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
    (-> (swap! ratom update-in path f)
        (get-in path)))
  (-swap! [a f x]
    (-> (swap! ratom update-in path f x)
        (get-in path)))
  (-swap! [a f x y]
    (-> (swap! ratom update-in path f x y)
        (get-in path)))
  (-swap! [a f x y more]
    (-> (swap! ratom update-in path
               (fn [v] (apply f v x y more)))
        (get-in path)))

  IMeta
  (-meta [_]
    (-meta ratom))

  IPrintWithWriter
  (-pr-writer [a writer opts]
    ;; not sure about how this should be implemented?
    ;; should it print as an atom focused on the appropriate part of
    ;; the ratom - (pr-writer (get-in @ratom path)) - or should it be
    ;; a completely separate type? and do we need a reader for it?

    ;; Until further investigation, it should simply be REPL friendly.
    (-write writer "#<Cursor: ")
    (pr-writer (get-in @ratom path) writer opts) ;; the current value
    (-write writer " @")
    (pr-writer path writer opts)
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


(defn root
  "Return the original atom of a cursor."
  [c]
  (-root c))

(defn path
  "Return the cursor's path."
  [c]
  (-path c))

(declare cursor)
(defn parent
  "Return a cursor one level higher in the path, or the root atom
  itself. Should probably be used as a debugging tool only to keep
  cursor code architecture-agnostic." [c]
  (cond
   (instance? Atom c) c
   (not (next (path c))) (root c)
   :else (cursor (root c) (drop-last (path c)))))


(defn cursor
  "Provide a cursor into an atom.

  Behaves like a normal atom but focuses updates and derefs to
  the specified path within the wrapped atom. e.g.,
  (let [c (cursor [:nested :content] ra)]
  ... @c ;; equivalent to (get-in @ra [:nested :content])
  ... (reset! c 42) ;; equivalent to (swap! ra assoc-in [:nested :content] 42)
  ... (swap! c inc) ;; equivalence to (swap! ra update-in [:nested :content] inc)
  )"
  [ra path] (RCursor. ra path))
