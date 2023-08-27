(ns cljblog.db
  (:require [cljblog.model :as m]))

;; The mutable state of the db
(def db (atom {}))

(defn list-articles [] (vals @db))

(defn get-article [id]
  (@db id)) 

(defn create-article [title body]
  (let [new_item (m/new_blog_inst title body)]
    (swap! db #(assoc % (:id new_item) new_item)))
  )

(defn- replace-old-article [old-article title body]
  (assoc old-article :title title :body body))

(defn- update-with-old-article [old-article title body]
  (let [new-article (replace-old-article old-article title body)]
    (swap! db #(assoc % (:id new-article) new-article))
  )
)

(defn update-article [id title body]
  (when-let [old (get-article id)]
    (update-with-old-article old title body)) 
)

(defn delete-article [id]
  (swap! db #(dissoc % id)))
