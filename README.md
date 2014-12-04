Cursors
==============

Cursors can be seen as a kind of *pointers* to a particular part of an
atom, which behaving exactly like a normal atom. This means that you
use the same functions you would on an atom (`reset!`, `swap!`,
`deref`, `add-watch`, etc) but affect only the part you are interested
in.

This enables you to create reusable functions and components by
abstracting away complex paths and getter/setter functions.

```clj
;; what was...
(swap! my-atom update-in [:some :path :that :might :be :quite :deep] my-fn)

;; ...can now become

(swap! my-cursor my-fn)

;; Notice that the path is no longer hardcoded; it could be a simple
;; atom, or a cursor pointing to the 10th level of a complex nested
;; hashmap.

;; How about associating a value into the nested structure? No
;; problem! Just `reset!` the cursor:

(reset! my-cursor "my-new-value")

;; Now just deref it:

@my-cursor

=> "my-new-value"

```

Usage
-----

Add `[reagent/reagent-cursor "0.1.2"]` to `:dependencies` in `project.clj`.

In your Reagent application `(:require [reagent.cursor :as rc])`.

There is two main functions available to create cursors: `cursor` and `cur`.

## cursor

`cursor` has two arities.

When given a single argument (a path), it returns a function that can
create a cursor when given an atom. *Useful to create mutliple cursors
with the same path.*


```clj
(def my-custom-cursor-fn (rc/cursor [:some :arbitrary :path]))

(map my-custom-cursor-fn [atom1 atom2 atom3])

;; this will return a collection of [cursor1 cursor2 cursor3]
```

When given two arguments, `cursor` will return a cursor.

```clj
(def c1 (rc/cursor [:some :arbitrary :path] atom1))

```


## cur


`cur` is the little brother of `cursor`. It will *only* accept 2
arguments (the atom and a path), but is guaranteed to return a
cursor.

Note that the atom argument is placed on the left, allowing
you to use a threading macro.

```clj

(-> my-atom
	(rc/cur [:some :path]) ;; <---- create the cursor
	(add-watch :my-watch #(println "updated!"))
	(historian/record! :my-state)
    (ls/local-storage :my-state))
	
```

License
-------

Copyright (c) 2014 Sean Corfield

Distributed under the Eclipse Public License, the same as Clojure.
