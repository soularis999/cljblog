(ns cljblog.web
  (:require [compojure.handler :as ch]
            [ring.adapter.jetty :as jetty]
            [cljblog.core :as blog])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer. (or (System/getenv "CLJBLOG_PORT") 3000)) ]
    (jetty/run-jetty (ch/site #'blog/app)
                     {:port port
                      :join? false})))
