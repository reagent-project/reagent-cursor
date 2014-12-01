# Changelog

## 0.2.0

- Cursor arguments swapped.
```clj
;; before
(cursor [:path :to :data] atom)

;; now
(cursor atom [:path :to :data])
