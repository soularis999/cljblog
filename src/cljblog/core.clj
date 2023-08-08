(ns cljblog.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as resp]
            [ring.middleware.session :as session]
            [ring.logger :as logger]
            [cljblog.db :as db]
            [cljblog.pages :as p]
            [cljblog.admin :as admin]
            ))


(defroutes app-routes
  (GET "/" [] (p/index (db/list-articles)))
  (GET "/article/:id" [id] (p/article (db/get-article id)))
  (route/not-found "Not Found"))

(defroutes admin-login-routes
 (GET "/admin/login" [:as {session :session}]
       (if (:admin session)
         (resp/redirect "/")
         (p/login-page)
         )
       )

  (GET "/admin/logout" []
       (-> (resp/redirect "/")
           (assoc-in [:session :admin] false)))

  (POST "/admin/login" [username password]
        (if (admin/check-login username password)
         (-> (resp/redirect "/")
             (assoc-in [:session :admin] true))
         (p/login-page)
         ))
  )

(defroutes admin-perm-routes
  (GET "/article/new" [] (p/edit-article nil))
  (POST "/article" [title body]
        (do (db/create-article title body)
            (resp/redirect "/")))

  (GET "/article/:id/edit" [id] (p/edit-article (db/get-article id)))
  (POST "/article/:id" [id title body]
        (do (db/update-article id title body)
            (resp/redirect (str "/article/" id )))))

(defn wrap-admin-routes [handler]
  (fn [request]
    (println (request :session))
    (if (-> request :session :admin)
      (handler request)
      (resp/redirect "/admin/login")
      )
    )
  )

(defn- mock [] 
  (doseq [x (range 1 10)]
    (db/create-article (str "Article " x) (str "this is a " x " article"))
  )
  (-> (routes
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

(def app (mock))


