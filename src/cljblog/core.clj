(ns cljblog.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as ch]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as resp]
            [ring.middleware.session :as session]
            [ring.logger :as logger]
            [cljblog.db :as db]
            [cljblog.pages :as p]
            [cljblog.admin :as admin]
            ))

(defn- build-context [session]
  {:admin (:admin session)}
  )

(defroutes app-routes
  (GET "/" [:as {session :session}] (p/index (build-context session) (db/list-articles)))
  (GET "/article/:id" [id :as {session :session}] (p/article (build-context session) (db/get-article id)))
  (route/not-found "Not Found"))

(defroutes admin-login-routes
 (GET "/admin/login" [:as {session :session}]
       (if (:admin session)
         (resp/redirect "/")
         (p/login-page (build-context session))
         )
       )

  (GET "/admin/logout" []
       (-> (resp/redirect "/")
           (assoc-in [:session :admin] false)))

  (POST "/admin/login" [username password]
        (if (admin/check-login username password)
         (-> (resp/redirect "/")
             (assoc-in [:session :admin] true))
         (p/login-page "Invalid username or password!")
         ))
  )

(defroutes admin-perm-routes
  (GET "/article/new" [:as {session :session}] (p/edit-article (build-context session) nil))
  (POST "/article" [title body]
        (do (db/create-article title body)
            (resp/redirect "/")))

  (GET "/article/:id/edit" [id :as {session :session}] (p/edit-article (build-context session) (db/get-article id)))
  (POST "/article/:id" [id title body]
        (do (db/update-article id title body)
            (resp/redirect (str "/article/" id ))))

  (DELETE "/article/:id" [id]
        (do (db/delete-article id)
            (resp/redirect "/")))

  )

(defn wrap-admin-routes [handler]
  (fn [request]
    (println (request :session))
    (if (-> request :session :admin)
      (handler request)
      (resp/redirect "/admin/login")
      )
    )
  )

(def app (-> (routes
        admin-login-routes
        (wrap-routes admin-perm-routes wrap-admin-routes)
        app-routes)
      (wrap-defaults site-defaults)
      (wrap-resource "public")
      wrap-base-url
      session/wrap-session
      logger/wrap-with-logger
    )
  )

