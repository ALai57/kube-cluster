(defproject pie-storefront "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[cheshire "5.9.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [ring/ring-mock "0.4.0"]
                 [mount "0.1.16"]
                 [org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot pie-storefront.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
