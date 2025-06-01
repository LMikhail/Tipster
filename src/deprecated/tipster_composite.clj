
(ns tipster.store.composite
  "Delegates to live store or history store based on cut timestamp."
  (:require [tipster.time :as t]))

(defrecord CompositeStore [live history cut-ts]
  tipster.storage.proto/PKVTStore
  (put!  [this batch] (tipster.storage.proto/put! live batch))
  (get!  [this p k]
    (let [[wall _] (t/split-hlc (tipster.internal/extract-hlc k))]
      (if (< wall cut-ts)
        (tipster.storage.proto/get! history p k)
        (tipster.storage.proto/get! live p k))))
  (scan [this prefix opts]
    ;; naive delegation
    (concat (tipster.storage.proto/scan history prefix (assoc opts :to cut-ts))
            (tipster.storage.proto/scan live    prefix (assoc opts :from cut-ts)))))
