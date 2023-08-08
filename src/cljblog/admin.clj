(ns cljblog.admin)

(def admin-login (System/getProperty "CLJBLOG_ADMIN_LOGIN", "admin"))
(def admin-password (System/getProperty "CLJBLOG_ADMIN_PASSW", "admin"))

(defn check-login [login password]
  (and (= login admin-login) (= password admin-password)))
