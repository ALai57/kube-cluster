(ns mock-sentry.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [mount.core :as mount]
            [cheshire.core :as json]
            [org.httpkit.server :as server]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))


(def port (or (System/getenv "MOCK_SENTRY_PORT")
              5000))

(defn default-output-fn
  "Default (fn [data]) -> string output fn.
  Use`(partial default-output-fn <opts-map>)` to modify default opts."
  ([     data] (default-output-fn nil data))
  ([opts data] ; For partials
   (let [{:keys [no-stacktrace? stacktrace-fonts]} opts
         {:keys [level ?err #_vargs msg_ ?ns-str ?file hostname_
                 timestamp_ ?line]} data]
     (str
      "[" (force timestamp_) " UTC+00:00] "
      (force msg_)
      (when-not no-stacktrace?
        (when-let [err ?err]
          (str "\n" err opts)))))))

(timbre/merge-config!
 {:appenders {:spit (assoc (appenders/spit-appender {:fname "log.txt"})
                           :output-fn default-output-fn)}})

(defn parse-body [body]
  (-> body
      slurp
      (json/parse-string keyword)))


(defroutes handler
  (GET "/" [] {:status 200 :body "Hello"})
  (GET "/api/v1/ping" [] {:status 200 :body "Pong"})
  (POST "/api/v1/log" {:keys [remote-addr body] :as request}
        (let [{:keys [service-name level message]} (parse-body body)]
          (timbre/infof "[%s @ %s] [%s] %s"
                        service-name
                        remote-addr
                        level
                        message)
          {:status 200 :body "Logs received"})))

(mount/defstate server
  :start
  (do
    (println "Starting server on port " port)
    (server/run-server handler {:port port}))

  :stop
  (do
    (println "Stopping server...")
    (server)))

(defn -main [& args]
  (mount/start))


(comment
  (require '[ring.mock.request :as mock])

  (handler
   (mock/request :post "/api/v1/log"
                 (json/generate-string {:level "FATAL"
                                        :service-name "test-service"
                                        :message "Nothing to see here"})))
  )
