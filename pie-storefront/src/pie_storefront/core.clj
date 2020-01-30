(ns pie-storefront.core
  (:gen-class)
  (:require [mount.core :as mount]
            [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [taoensso.timbre :as timbre]
            [cheshire.core :as json]))

(def port (or (System/getenv "PIE_STOREFRONT_PORT")
              5001))

(def pies->price
  {:apple 5.00
   :peach 7.00
   :pumpkin 5.50})

(defn parse-body [body]
  (-> body
      slurp
      (json/parse-string keyword)))

(defn req->itemized-list [body]
  (into #{}
        (for [[pie n] body]
          [(name pie) n (->> pie
                             pies->price
                             (* n))])))

(defroutes handler
  (GET "/api/v1/ping" [] {:status 200 :body "Hello, pie!"})
  (context "/api/v1/orders" []
           (PUT "/" request
                (let [itemized-list (->> request
                                         :body
                                         parse-body
                                         req->itemized-list)]
                  {:status 200 :body {:items itemized-list
                                      :total (->> itemized-list
                                                  (map last)
                                                  (reduce +))
                                      :id "1234"}}))
           (GET "/:id" [id]
                (println "requested order id " id))))

(mount/defstate server
  :start
  (do
    (println "Starting server")
    (server/run-server handler {:port port}))

  :stop
  (do
    (println "Stopping server")
    (server)))

(defn -main [& args]
  (mount/start))
