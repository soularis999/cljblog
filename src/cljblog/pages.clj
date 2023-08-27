(ns cljblog.pages
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            ))

(defn- template [context & body]
  (html5 [:head
         ; Basic Page
         [:meta {:charset "utf-8"}]
         [:title "BLOG!"]
         [:meta {:name "description" :content ""}]
         [:meta {:name "author" :content ""}]
         ; Mobile
         [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
         ; Fonts
         (include-css "http://fonts.googleapis.com/css?family=Raleway:400,300,600")
         (include-css "/css/normalize.css")
         (include-css "/css/skeleton.css")
         (include-css "/css/custom.css")
         ; JS
         (include-js "http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js")
        ]
         [:body
          [:div.container
           [:nav.navbar
            [:div.container
             [:ul.navbar-list
              [:li.navbar-item [:a.navbar-link {:href "/"} "Blog!"]]
              (if (not (:admin context))
                [:li.navbar-item [:a.navbar-link {:href "/admin/login"} "LogIn"]]
                (list
                  [:li.navbar-item [:a.navbar-link {:href "/article/new"} "New Article"]]
                  [:li.navbar-item [:a.navbar-link {:href "/admin/logout"} "Logout"]])
                )
              ]
             ]
            ]
           ]
          [:div.container
           [:div.row [:div {:class "twelve columns"} "&nbsp"]]
            body
          ]
        ]
      )
  )

(def preview-len 270)

(defn- cut-body [article]
  (if (> (.length article) preview-len)
    (subs article 0 preview-len)
    article
    ))

(defn- article_t [article]
  [:div [:h5
         [:a {:href (str "/article/" (:id article))} (:title article) ]]
  [:p (-> article :body cut-body)]]
  )

(defn- article_detail_t [context article]
  (concat
    (when (:admin context)
      (list
        (form/form-to [:delete (str "/article/" (:id article))]
          [:div {:class "row"}
            [:div {:class "two columns"}
             [:a {:href (str "/article/" (:id article) "/edit") :class "button"} "Edit!"]]
            [:div {:class "ten columns"}
             (anti-forgery-field)
             (form/submit-button {:class "button-primary"} "Delete!")]
          ])
        )
    )
    (list
      [:h2 (:title article)]
      [:p (:body article)]
      )
  )
)

(defn- articles_t [articles]
  [:div.row (map article_t
            (sort-by #(:created %) articles))])

(defn index [context articles]
 (template context (articles_t articles))
 )

(defn article [context article]
  (template context (article_detail_t context article))
  )

(defn edit-article [context article]
  (template
    context
    (form/form-to
      [:post (if article 
               (str "/article/" (:id article))
               "/article")]

      [:div.row

       [:div.six_columns
        (form/label "title" "Title")
        (form/text-field "title" (:title article))
        ]

       [:div.six_columns
        (form/label "body" "Body")
        (form/text-area "body" (:body article))
        ]
       ]
      (anti-forgery-field)

      (form/submit-button {:class "button-primary"} "Save!")
      
      )
    )
  )

(defn login-page [context & msg]
  (template
    context
    (when msg
     [:div.alert.alert-denger msg])

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
