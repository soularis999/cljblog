(defproject cljblog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring-logger  "1.0.0"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.12.5"]
            [cider/cider-nrepl  "0.30.0"]]
  :ring {:handler cljblog.core/app}
  :main cljblog.web
  :profiles 
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
