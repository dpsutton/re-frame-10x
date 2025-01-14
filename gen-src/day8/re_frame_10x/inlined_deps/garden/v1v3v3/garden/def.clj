(ns ^{:mranderson/inlined true} day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.def
  (:require [day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.types]
            [day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.util :as util]
            [day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.core])
  (:import day8.re_frame_10x.inlined_deps.garden.v1v3v3.garden.types.CSSFunction
           day8.re_frame_10x.inlined_deps.garden.v1v3v3.garden.types.CSSAtRule))

(defmacro defstyles
  "Convenience macro equivalent to `(def name (list styles*))`."
  [name & styles]
  `(def ~name (list ~@styles)))

(defmacro defstylesheet
  "Convenience macro equivalent to `(def name (css opts? styles*))`."
  [name & styles]
  `(def ~name (day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.core/css ~@styles)))

(defmacro defrule
  "Define a function for creating rules. If only the `name` argument is
  provided the rule generating function will default to using it as the
  primary selector.

  Ex.
      (defrule a)
      ;; => #'user/a

      (a {:text-decoration \"none\"})
      ;; => [:a {:text-decoration \"none\"}]

  Ex.
      (defrule sub-headings :h4 :h5 :h6)
      ;; => #'user/sub-headings

      (sub-headings {:font-weight \"normal\"})
      ;; => [:h4 :h5 :h6 {:font-weight \"normal\"}]"
  [sym & selectors]
  (let [rule (if (seq selectors)
               `(vec '~selectors)
               [(keyword sym)])
        [_ sym spec] (macroexpand `(defn ~sym [~'& ~'children]
                                     (into ~rule ~'children)))]
    `(def ~sym ~spec)))

(defmacro ^{:arglists '([name] [name docstring? & fn-tail])}
  defcssfn
  "Define a function for creating custom CSS functions. The generated
  function will automatically create an instance of
  `day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.types.CSSFunction` of which the `:args` field will be set
  to whatever the return value of the original function is. The
  `:function` field will be set to `(str name)`.

  If only the `name` argument is provided the returned function will
  accept any number of arguments.

  Ex.
      (defcssfn url)
      ;; => #'user/url

      (url \"http://fonts.googleapis.com/css?family=Lato\")
      ;; => #day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.types.CSSFunction{:function \"url\", :args \"http://fonts.googleapis.com/css?family=Lato\"}

      (css (url \"http://fonts.googleapis.com/css?family=Lato\"))
      ;; => url(http://fonts.googleapis.com/css?family=Lato) 

  Ex.
      (defcssfn attr
        ([name] name)
        ([name type-or-unit]
           [[name type-or-unit]])
        ([name type-or-unit fallback]
           [name [type-or-unit fallback]]))
      ;; => #'user/attr

      (attr :vertical :length)
      ;; => #day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.types.CSSFunction{:function \"url\", :args [:vertical :length]}

      (css (attr :vertical :length))
      ;; => \"attr(vertical length)\"

      (attr :end-of-quote :string :inherit) 
      ;; => #day8.re-frame-10x.inlined-deps.garden.v1v3v3.garden.types.CSSFunction{:function \"url\", :args [:end-of-quote [:string :inherit]]}

      (css (attr :end-of-quote :string :inherit))
      ;; => \"attr(end-of-quote string, inherit)\""
  ([sym]
     (let [[_ sym fn-tail] (macroexpand
                            `(defn ~sym [& ~'args]
                               (CSSFunction. ~(str sym) ~'args)))]
       `(def ~sym ~fn-tail)))
  ([sym & fn-tail]
     (let [[_ sym [_ & fn-spec]] (macroexpand `(defn ~sym ~@fn-tail))
           cssfn-name (str sym)]
       `(def ~sym
          (fn [& args#]
            (CSSFunction. ~cssfn-name (apply (fn ~@fn-spec) args#)))))))

(defmacro defkeyframes
  "Define a CSS @keyframes animation.

  Ex. 
      (defkeyframes my-animation
        [:from
         {:background \"red\"}]

        [:to
         {:background \"yellow\"}])

      (css {:vendors [\"webkit\"]}
        my-animation ;; Include the animation in the stylesheet.
        [:div
         ^:prefix ;; Use vendor prefixing (optional).
         {:animation [[my-animation \"5s\"]]}])"
  [sym & frames]
  (let [value {:identifier `(str '~sym)
               :frames `(list ~@frames)}
        obj `(CSSAtRule. :keyframes ~value)]
    `(def ~sym ~obj)))


