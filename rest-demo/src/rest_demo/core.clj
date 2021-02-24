(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer [GET] :as comp]
            [compojure.route :as route]
            [ring.middleware.defaults :as ring]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(defn simple-body-page [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "Hello World"})

; request-example
(defn request-example [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->>
             (pp/pprint req)
             (str "Request Object: " req))})

(defn hello-name [req] ;(3)
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (->
             (pp/pprint req)
             (str "Hello " (:name (:params req))))})

(defn jsontest [req] ;(3)
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {"hi" "there"
                             "list" [1 2 3]})})

(comp/defroutes app-routes
  (GET "/" [] simple-body-page)
  (GET "/request" [] request-example)
  (GET "/hello" [] hello-name)
  (GET "/jsontest" [] jsontest)
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (ring/wrap-defaults #'app-routes ring/site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

;; remaining questions
;; - path-based parameters
;; - an example dealing with a post body