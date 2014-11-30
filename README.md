reagent-cursor
==============

Optional cursors library for Reagent.

Usage
-----

Add `[reagent/reagent-cursor "0.1.1"]` to `:dependencies` in `project.clj`.

In your Reagent application, `(:require [reagent.cursor :as rc])` and then construct cursors from Reagent atoms like this `(rc/cursor [:path :to :data] ratom)`.

See docstrings in `reagent.cursor` for more details.

License
-------

Copyright (c) 2014 Sean Corfield

Distributed under the Eclipse Public License, the same as Clojure.
