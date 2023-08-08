(ns cljblog.pages
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ))

(defn- template [& body]
  (html5 [:head
         ; Basic Page
         [:meta {:charset "utf-8"}]
         [:title "BLOG!"]
         [:meta {:name "description" :content ""}]
         [:meta {:name "author" :content ""}]
         ; Mobile
         [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
         ; Fonts
         [:link {:href "//fonts.googleapis.com/css?family=Raleway:400,300,600" :rel "stylesheet" :type "text/css"}]
         [:link {:href (include-css "/css/normalize.css") :rel "stylesheet"}]
         [:link {:href (include-css "/css/skeleton.css") :rel "stylesheet"}]
         [:link {:href (include-css "/css/custom.css") :rel "stylesheet"}]
         ; JS
         [:script {:src "//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"}]
        ]
         [:body
          [:div.container
           [:nav.navbar
            [:div.container
              [:ul.navbar-list
                [:li.navbar-item [:a.navbar-link {:href "/"} "Blog!"]]
                [:li.navbar-item [:a.navbar-link {:href "/article/new"} "New Article"]]
                [:li.navbar-item [:a.navbar-link {:href "/admin/login"} "LogIn"]]
                [:li.navbar-item [:a.navbar-link {:href "/admin/logout"} "Logout"]]
              ]
             ]
           ]
          ]
          [:div.container
            body
          ]
        ]
      )
  )

(defn- article_t [article]
  [:li [:a {:href (str "/article/" (:id article))}
        (:title article) ] ]
  )

(defn- article_detail_t [article]
  (list
    [:a {:href (str "/article/" (:id article) "/edit")} "Edit!"]
    [:hr]
    [:h2 (:title article)]
    [:p (:body article)]
    )
  )

(defn- articles_t [articles]
  [:ul (map article_t 
            (sort-by #(:created %) articles))])

(defn index [articles]
 (template (articles_t articles))
 )

(defn article [article]
  (template (article_detail_t article))
  )

(defn edit-article [article]
  (template
    (form/form-to
      [:post (if article 
               (str "/article/" (:id article))
               "/article")]

      (form/label "title" "Title")
      (form/text-field "title" (:title article))

      (form/label "body" "Body")
      (form/text-area "body" (:body article))

      (anti-forgery-field)

      (form/submit-button "Save!")
      
      )
    )
  )

(defn login-page []
  (template
    (form/form-to
      [:post "/admin/login"]

      [:div.row
       [:div.six_columns
        (form/label "username" "User Name")
        (form/text-field "username")
        ]

        [:div.six_columns
          (form/label "password" "Password")
          (form/password-field "password")
        ]
      ]

      (anti-forgery-field)

      (form/submit-button {:class "button-primary"} "Login!")
      )))
