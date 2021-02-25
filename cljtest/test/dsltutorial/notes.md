---
date: 2021-02-24
---

## Testing

Source: https://clojuredocs.org/clojure.test

`deftest` lets you define test lists in a bottom-up fashion

```clj
(deftest sub1
    expr1
    expr2)
(deftest sub2 
    expr1 
    expr2)

(deftest joined
    (sub1)
    (sub2)
)
```

`testing` let's you define test lists inline like `testlist` in Expecto

Testing with gen is just using the [property testing lib](https://clojure.org/guides/test_check_beginner)

```clj
(require '[clojure.test.check :as tc]) ;; property runner
(require '[clojure.test.check.generators :as gen])
(require '[clojure.test.check.properties :as prop])
(require '[clojure.test.check.clojure-test :refer [defspec]]) ;; compatability with deftest
```

Fixtures attached with `use-fixtures`. They will apply to all tests in the namespace and run based on the flag for `:each` test or `:once` for all tests

## DSL

Multi-methods are like a case statement dispatched on type.
- can be extended by anyone at anytime

How would I accomplish the same as a multimethod in F#?
- probably a command pattern? 
- Maybe a map of {type, type -> string}?

`defmulti` decided what the multimethod dispatches on

!!! Inheritance is ad-hoc. Seperate from defining the type definitions
- Unions are used a similar way in F#


I had a heck of a time figuring out keyword namespacing. Here is a good guide
- https://blog.jeaye.com/2017/10/31/clojure-keywords/