(ns pie-storefront.baking-service.core)

(defprotocol BakingService
  (ping [this])
  (request [this order]))
