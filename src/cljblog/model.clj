(ns cljblog.model)

(defrecord Blog [id title body created])

(defn new_blog_inst [title body]
  (Blog. (str (java.util.UUID/randomUUID)) title body (new java.util.Date)))

