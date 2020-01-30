(ns mock-sentry.core-test
  (:require [clojure.test :refer :all]
            [mock-sentry.core :refer :all]
            [ring.mock.request :as mock]
            [taoensso.timbre :as timbre]
            [cheshire.core :as json]))


(defmacro with-captured-logs-as [sym & body]
  `(let [~sym (atom [])]
     (timbre/with-merged-config
       {:appenders {:spit {:enabled? false}
                    :test-logging {:enabled? true
                                   :output-fn default-output-fn
                                   :fn #(swap! ~sym
                                               conj
                                               (force (:msg_ %)))}}}
       ~@body)))

(deftest server-test
  (testing ""
    (is (= {:status 200 :headers {} :body "Pong"}
           (handler (mock/request :get "/api/v1/ping")))))
  (testing "log"
    (with-captured-logs-as captured-logs
      (handler (mock/request :post "/api/v1/log"
                             (json/generate-string {:level "FATAL"
                                                    :service-name "mock-sentry-tests"
                                                    :message "Test log 1"})))
      (println (first @captured-logs))
      (is (= (count @captured-logs) 1)))))
