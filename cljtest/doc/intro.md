# Introduction to cljtest

TODO: write [great documentation](http://jacobian.org/writing/what-to-write/)




## install
- need to install java first
- don't worry about installing clojure leiningen takes care of it

## Project basics
- project.clj is like csproj. It contains meta, dependencies, and build info
- `lein install` to install packages
  - looks like this doesn't search any package repositories, but has to reference a file
  - the standard for dependency management seems to be editing the project file, not a cli command
- `lein new` to scaffold a project from template
- `lein repl` for repl
- `lein test` to test
- `lein run` to run project
  - seems to be much slower than repl evaluation 
- `lein compile` is like build
- `lein jar` is like pack

Package repository: https://clojars.org/ (can also use anything from maven)

Q: How would I start multiple projects? Probably launch.json



## Tools
Calva recommended for vscode

PROBLEM: Need to figure out unit test integration

## Links
Install: https://www.clojure.org/guides/getting_started
Tools: https://www.clojure.org/community/tools
- Calva for vsCode
- lein seems to be an expansion on clj (adds testing, packaging, and more)
- boot is the build tool

Community resources: https://www.clojure.org/community/resources

Tutorials:
- http://www.unexpected-vortices.com/clojure/brief-beginners-guide/index.html
- http://clojure-doc.org/articles/tutorials/introduction.html
- https://yogthos.net/ClojureDistilled.html
- http://clojure-doc.org/articles/content.html
  - has tutorials on all kinds of topics

[Core libraries](http://clojure-doc.org/articles/ecosystem/libraries_directory.html)
- things like testing, code coverage, serialization, http, diff tools, etc



## notes

To run things in the repl, you have to bee in the project root. In my case it's `cljtest` and not the workspaces root. Contains the project.clj

When running functions in the repl, it's important to reload the code, or it won't reflect changes. Example
```clojure
(use 'cljtest.core :reload-all)(-main)
```

!!! partition-by is very iteresting. It traverses the list, adding things to the same group as long as a function returns the same result, and starting a new group when the function returns a different value. It isn't group by because it only groups adjacent values

`map` can take multiple sequences and will effectively zip them

`contains?` used on a vector checks if it has a given index

`some` when given a set will return the first instance of any item in the set
e.g. `(some #{6 3} [2 1 8 6])` would return on the first 3 or 6

[lazy-seq doc](https://clojuredocs.org/clojure.core/lazy-seq) has great examples of functions with different numbers of args (multi-arity)
- can be accomplished with anonymous functions like
```clojure
(fn name 
([] (name default-arg))
([arg1] ...)
)
```

### the piping catastrophy
I was trying to figure out how piping (called threading in clojure) works. Really, I just wanted to make sure it was the right concept.
I tried this by `(->> 5 #(/ % 2) floor)` but it repeatedly would not work no matter how I tweaked it. The output claimed that floor was trying to floor an object

Turns out `#()` is a value and will not evaluate. same with `(fn [] ())`. It wasn't passing 5 to be divided, it was passing a the function to floor. 
To evaluate the anonymous function you need extra parens `(#(/ % 2))`.

So the working version is
```clojure
(->> 5 (#(/ % 2)) floor)
```
Alternately, this example coul use thread-first, that passes the previous value as the first arg 
```clojure
(-> 5 (/ 2) floor)
```

This issue is not really demonstrated in any of the examples because they all operate on lists and use `map`, which expects a function 

## Recusion
Can think of it as forward only. There is no way to get a value from the child operation and then add it to an accumulation

## The flatten incident
`conj` behaves differently for lists and vectors, causing opaque ordering issues when reducing

I was probably also missing the point. I think they wanted me to use `tree-seq` which is used for flattening trees

## Clojure Libs and Namespaces
```yml
link: https://8thlight.com/blog/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html
```
Namespace imports are a bit confusing 
Still sorting it out but 
- when invoked directly you seem to need `'namespace`. I don't know what the single quote is about
  - appears that `'` is the way of escaping a block of clojure. Telling it not to evaluate now but pass as a code block/symbol
  - can quote whole structures like `'[ns1 ns2 [ns3 :as alias]]` or `'(ns sub1 sub2)`
- when use in ns, it is a list of names `(:require [ns1 ns2])`
- can import multiple children like `'(ns sub1 sub2)`
- aliasing works in pretty much any context with a namespace

- require makes the namespace available, but you still have to prefix every child value
- use makes the namespace available without prefix (like `using` does in c#)
  - !!! replaced by `(require [ns :require :all])`
- inclusions have set semantics where you can `:only []` or `:exclude []` to get a subset of the namespace
- `import` is for java libs
- ns
  - echos all the import methods as keyed parameters
  - don't need to quote symbols anymore

## Clojure Distilled
```yml
link: https://yogthos.net/ClojureDistilled.html
```
`#(+ 1 x)` functions with the `#` are basically lambdas
- shorthand for `(fn [arg] (println arg))`

`(def name value)` is the general symbol binder
`(defn funcName [args](body))` is a shortcut on binding an anonymous function/lambda

!!! clojure is a single-pass compile just like F# 
- can use forward references if you use `(declare forwardRef)` before where you need the forward ref

!!! `%` is used to reference the unnamed parameter

like f#, clojure allows destructured binding 
- looks like it mostly comes down to vector positioning or maps
  - `[first second third]`
  - `[{name1 :key1 name2 :key2}]`
  - `[{:keys [user pass]}]` this is a shortened version for deconstructing maps
- can also collapse remaining arguments into a vector with `&`

No separation of modules and namespaces. just the one concept (the separation for F# is really a compatability issue anyway)

Mutability
- `(declare ^{:dynamic true} *foo*)`
- bind values differently ("binding" instead of let) `(binding [*foo* "I exist!"] (body))` 
- discouraged, but available for outer cases like dealing with IO
- dynamic and weakly typed

Polymorphism / Pattern Matching
- Multimethods
  - `defmulti` with cases handled by `defmethod`
  - not like f#, closer to overloading
- Protocols
  - like interfaces, implemented by concrete types
  - `defprotocol` and `deftype`
  - `extend-protocol` can add a protocol to a type, including java classes

Global state
- manages transactions on mutable data in-language
- `atom` and `ref`
- atom
  - access value with `deref`
  - change with `reset!` or `swap!`
  - the exclamation is conventions to show mutable state management 
- ref
  - transaction scope with `dosync`
  - `ref-set`, `alter`

!!! uses `nil` instead of `None`

Macros -> Metaprogramming
- looks very powerful, kinda confusing, but much less confusing than reflection in .net
- would handle a lot of aspect-like stuff
  - security wrappers, aggregating event handlers, etc
- I'd have to experiment more, but I think this would work for most things I'd use Source Generators for too


Java interop
- `Class.` for construction
- `.method` for methods on instance
- `(.. inst method1 method2)` for chained instance method calls

!!! turns out someone did make an implicitly typed clojure https://typedclojure.org/
- not actually a reference in this article
- appears to be a library for clojure, not a fork of the language
- optional typing, like typescript
- have to manually run the type checker (at least it isn't integrated into the normal compile)

READ: recommends lots of further reading


## Things I do and don't like so far
Like
- the text-file repl is awesome
- haven't worked with macros, but they look like what meta-programming should be
- spec is great
- in-language transactions around mutable data
  - atoms, agents
- question marks for boolean test names
- `recur` function to make it clear where recursion is taking place / enforce tail-recursion
- multi-arity definitions all under one declaration

Dislike
- namespaces were hard to figure out. especially needing to quote symbols for importing in the console
- the simplicity sometimes makes things hard. It enforces rules consistently even when I naturally interpret it differently
  - the biggest example was the unexpected need for parens around anonymous functions in a threading marcro
  - the namespaces are another example
- I'd like more design-time feedback, I spend a lot of time decyphering runtime errors that end up being syntactic derps causing incorrect types
- paren errors when refactoring (e.g something gets moved into a spot where it's trying to evaluate as a function)
- i spend quite a bit of time trying to figure out types on core functions. Especially of lambdas like on group-by, sort
- I also spend a lot of time checking what functions put collections first or last in their args

Neutral 
- laziness is hard to get used to. It's not bad, just different
- 

## Introduction to Clojure
```yml
date: 2021-01-15
link: http://clojure-doc.org/articles/tutorials/introduction.html
```

!!! Language cheatsheet https://clojure.org/api/cheatsheet

!!! `doc` function gives you info on any function
- can also see the source with `(source)`

`:name` is called a keyword. It is a type of literal

`'(val1 val2 val3)` is a list, but most often use vectors instead `[]`

Limit use of macros. Largely when you can't accomplish a task otherwise

`let` allows you to define as many bound names as you want ("locals"), and later locals can reference the earlier ones
- you can also overwrite names you've previously used

!! core math operations will take any numer of arguments, not just two. However they do not take lists

dots in namespaces correspond to directories in the filesystem
- don't use dots to sub-namespace unless you physically separate the files
- because of java, you have to use underscores in directory names instead of dashes

collections literals also have constructor functions

[A good quick reference for collection access](http://clojure-doc.org/articles/tutorials/introduction.html#functions-for-working-with-data-structures)

Strings are considered a sequence

This is the point where my note on a static type-keyed system used to be
https://github.com/farlee2121/CompanyImprovements/issues/59#issuecomment-761892060


!!! `cond` is kinda like pattern matching, kinda like a bunch of elifs

nil and false are falsey, everything else is truthy
- use other checks for things like empty string or empty lists

Equality
- structural
- `=` is strongly typed, `==` will do some cross-type casting

convention: methods for side-effects start with `do`
- `doseq` is like a foreach loop
- `dotimes` repeats an operation

convention: in a let binding assign the name `_` when you only want a side-effect and don't care about the value

`remove` is filter with reversed boolean logic

`apply` expands operations on a fixed set of args to a collection
- like max or min
- reduce can be used for cases like max and min, but I think apply can also work like a map
- I can't seem to figure out the proper arity for custom apply functions
  - Oh! It is like the general apply concept in that it unpacks the collection into individual arguments. If you want to handle collections of arbitrary size then your function needs to take arbitrary arguments

`for` is more about list comprehensions than iterating (though it does that)

`iterate` is more a kind of monadic recursion, getting subsequent values from a function (next value based on previous value)

recursion
- `loop` and `recur`
- You can also use recur in any function without explicitly having loop

Not discussed, but lists `agents` amoung ref types. Might be queue-like?
- https://clojure.org/reference/agents
- yes, much like the mailbox of F#, useful for serializing access to mutable state


## 4clojure kata
```yml
date: 2021-01-17
link: https://www.4clojure.com/
```

set literal `#{1 2 3 4}`

maps can be accessed using the key like a function or argument
```clojure
(:b {:a 10 :b 20})
({:a 10 :b 20} :b)
```

