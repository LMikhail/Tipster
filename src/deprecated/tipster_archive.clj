
(ns tipster.archive
  "SSTable cut & ship utilities for Tipster."
  (:require [clojure.java.io :as io]
            [tipster.time :as t])
  (:import [org.rocksdb SstFileWriter EnvOptions Options]))

(defn export-range!
  "Extracts all keys with HLC.wall < cut-ts to target-dir as SST."
  [rocksdb cut-ts target-dir]
  ;; NOTE: PoC — for real use wrap RocksJava SstFileWriter.
  (println "[archive] exporting range to" target-dir "before" (t/hlc->iso (bit-shift-left cut-ts 16))))

(defn write-tombstones!
  [rocksdb cut-ts]
  (println "[archive] writing tombstones for cut-ts" cut-ts))
