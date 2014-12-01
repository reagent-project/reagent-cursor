reagent-cursor
==============

Cursors library for clojurescript atoms.

Usage
-----

Add `[reagent/reagent-cursor "0.2.0"]` to `:dependencies` in `project.clj`.

In your Reagent application, `(:require [reagent.cursor :as rc])` and then construct cursors from Reagent atoms like this `(rc/cursor ratom [:path :to :data])`.

See docstrings in `reagent.cursor` for more details.

License
-------

Copyright (c) 2014 Sean Corfield

Distributed under the Eclipse Public License, the same as Clojure.
