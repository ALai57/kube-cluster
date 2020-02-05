(ns pie-storefront.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as json]
            [pie-storefront.core :refer :all]
            [ring.mock.request :as mock]))

(defn order-pie [m]
  (let [{:keys [status body]}
        (handler (mock/request :put "/api/v1/orders"
                               (json/generate-string m)))
        {:keys [items total id]} body]
    {:status status
     :items items
     :total total}))

(deftest server-test
  (testing "ping"
    (is (= {:status 200 :headers {} :body "Hello, from the pie storefront!"}
           (handler (mock/request :get "/api/v1/ping")))))
  (testing "orders"
    (is (= #{["peach" 2 14.00] ["apple" 1 5.00]}
           (req->itemized-list {:peach 2 :apple 1})))
    (let [{:keys [status items total]} (order-pie {:peach 2 :apple 1})]
      (is (= #{["peach" 2 14.00] ["apple" 1 5.00]} items))
      (is (= 200 status))
      (is (= 19.00 total)))
    (let [{:keys [status items total]} (order-pie {:pumpkin 3 :peach 1 :apple 2})]
      (is (= #{["pumpkin" 3 16.50] ["apple" 2 10.00] ["peach" 1 7.00]} items))
      (is (= 200 status))
      (is (= 33.5 total)))))
