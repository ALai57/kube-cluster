(ns pie-storefront.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [pie-storefront.core :refer :all]
            [ring.mock.request :as mock]))

(defn order-pie [m]
  (handler (mock/request :put "/api/v1/orders"
                         (json/generate-string m))))

(deftest server-test
  (testing "ping"
    (is (= {:status 200 :headers {} :body "Hello, pie!"}
           (handler (mock/request :get "/api/v1/ping")))))
  (testing "orders"
    (is (= #{["peach" 2 14.00] ["apple" 1 5.00]}
           (req->itemized-list {:peach 2 :apple 1})))
    (let [{:keys [status body]} (order-pie {:peach 2 :apple 1})
          {:keys [items total]} body]
      (is (= #{["peach" 2 14.00] ["apple" 1 5.00]} items))
      (is (= 200 status))
      (is (= 19.00 total)))
    (let [{:keys [status body]} (order-pie {:pumpkin 3 :peach 1 :apple 2})
          {:keys [items total]} body]
      (is (= #{["pumpkin" 3 16.50] ["apple" 2 10.00] ["peach" 1 7.00]} items))
      (is (= 200 status))
      (is (= 33.5 total)))))
