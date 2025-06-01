; Tipster prototype — single-file PoC (Clojure 1.11)
; -----------------------------------------------------------------------------
; Provides three namespaces:
;   1. tipster.time   – Hybrid‑Logical Clock helpers
;   2. tipster.rocks  – RocksDB PKVT store (embedded single‑node)
;   3. tipster.cli    – minimal CLI with `timeline` command
;
; For brevity all namespaces are placed in one file. In production split them.
;
; External deps (add to deps.edn):
;   org.rocksdb/rocksdbjni "8.8.1"
;   org.clojure/tools.cli   "1.0.214"
; -----------------------------------------------------------------------------

(ns tipster.time)

(def ^:private last-hlc
  "Atom {:wall <millis> :logi <0-65535>} tracking the latest issued clock."
  (atom {:wall (System/currentTimeMillis) :logi 0}))

(defn- inc-logi [{:keys [wall logi]}]
  (if (< logi 65535)
    {:wall wall :logi (inc logi)}
    (throw (ex-info "Logical counter overflow (65k/1 ms)" {}))))

(defn next-hlc!
  "Return next monotonic HLC map {:wall :logi}.  Accepts optional peer‐seen
  HLC to preserve global order."
  ([]
   (next-hlc! nil))
  ([peer-hlc]
   (let [now  (System/currentTimeMillis)
         peer (or peer-hlc {:wall 0 :logi 0})]
     (swap! last-hlc
            (fn [prev]
              (cond
                (> now (:wall prev)) {:wall now :logi 0}
                (> (:wall peer) (:wall prev)) peer
                :else                 (inc-logi prev)))))))

(defn hlc->long
  "Pack {:wall w :logi l} → 64‑bit long (big‑endian semantics for numeric sort)."
  [{:keys [wall logi]}]
  (bit-or (bit-shift-left wall 16) logi))

(defn long->hlc [x]
  {:wall (unsigned-bit-shift-right x 16)
   :logi (bit-and x 0xFFFF)})

(defn hlc->iso
  "Printable ISO‑8601 + optional +n suffix if logical>0."
  [hlc]
  (let [{w :wall l :logi} hlc]
    (str (java.time.Instant/ofEpochMilli w)
         (when (pos? l) (str "+" l)))))

(defn iso->hlc [s]
  (let [[inst l] (clojure.string/split s #"\+")]
    {:wall (.toEpochMilli (java.time.Instant/parse inst))
     :logi (Long/parseLong (or l "0"))}))

; -----------------------------------------------------------------------------
(ns tipster.rocks
  (:require [tipster.time :as t])
  (:import (org.rocksdb RocksDB Options ReadOptions FlushOptions
                        WriteOptions ColumnFamilyDescriptor ColumnFamilyHandle
                        ColumnFamilyOptions))
  (:gen-class))

(RocksDB/loadLibrary)

(defn- open-db [path]
  (let [opts (doto (Options.)
               (.setCreateIfMissing true)
               (.setPrefixExtractor org.rocksdb.BuiltinComparator/ASCII))]
    (RocksDB/open opts path)))

(defprotocol PKVTStore
  (put!  [_ p k v t])
  (get!  [_ p k])
  (timeline [_ p k from-iso to-iso]))

(defrecord RocksStore [^RocksDB db]
  PKVTStore
  (put! [this p k v t]
    (let [hlc   (t/next-hlc!)
          key   (encode-key p k (t/hlc->long hlc))
          value (encode-val v t)]
      (.put db key value)))
  (get! [this p k]
    (let [prefix (encode-key-prefix p k)]
      (with-open [it (.newIterator db)]
        (.seek it prefix)
        (when (.isValid it)
          (decode-val (.value it))))))
  (timeline [_ p k from-iso to-iso]
    (let [lo  (t/iso->hlc from-iso)
          hi  (t/iso->hlc to-iso)
          lo-k (encode-key p k (t/hlc->long lo))
          hi-k (encode-key p k (t/hlc->long hi))]
      (lazy-seq
        (let [it (.newIterator db)]
          (.seek it lo-k)
          (loop [acc []]
            (if (and (.isValid it)
                     (neg? (org.rocksdb.RocksDB/compare (.key it) hi-k)))
              (let [val (decode-val (.value it))
                    hlc (decode-key-hlc (.key it))]
                (.next it)
                (recur (conj acc [hlc val])))
              acc)))))))

; --- encoding helpers (placeholder, must match PKVT spec) --------------------
(defn encode-key [p k hlc]
  ;; TODO: real encoding — here naive str->bytes
  (.getBytes (str p "|" k "|" hlc)))

(defn encode-key-prefix [p k]
  (.getBytes (str p "|" k "|")))

(defn decode-key-hlc [^bytes key]
  (let [s (String. key)
        hlc (Long/parseLong (last (clojure.string/split s #"\\|")))]
    hlc))

(defn encode-val [v t]
  ;; placeholder CBOR encoder → here plain EDN
  (.getBytes (pr-str {:v v :t t})))

(defn decode-val [^bytes b]
  (-> b String. read-string))

(defn open-store [path]
  (->RocksStore (open-db path)))

; -----------------------------------------------------------------------------
(ns tipster.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [tipster.rocks :as store]
            [tipster.time  :as time])
  (:gen-class))

(def cli-opts
  [["-d" "--db PATH" "Path to RocksDB" :default "./tipster.db"]
   ["--from TS" "ISO‑time start" :default "1970-01-01T00:00:00Z"]
   ["--to TS"   "ISO‑time end"   :default "2100-01-01T00:00:00Z"]])

(defn timeline-cmd [{:keys [db from to]} [pred k]]
  (let [st   (store/open-store db)
        rows (store/timeline st (keyword pred) k from to)]
    (doseq [[hlc v] rows]
      (println (format "%s | %s" (time/hlc->iso (time/long->hlc hlc)) v)))))

(defn -main [& args]
  (let [{:keys [options arguments errors]} (parse-opts args cli-opts)]
    (when errors (binding [*out* *err*] (run! println errors) (System/exit 1)))
    (case (first arguments)
      "timeline" (timeline-cmd options (rest arguments))
      (println "Unknown command"))))

;; ## 7 · Decomposition strategies (parent vs child links) — *2025‑05‑18 late*

;; | Strategy | PKVT fields | Traversal direction | Pros | Cons |
;; |----------|-------------|---------------------|------|------|
;; | **Child‑up (↑)** | child row stores `parent` (RefID = P/K/hlc) + optional `owner` tag | Walk **upward** for assembly | • Parent rows immutable, cheap child insertion • No variable‑length list in parent | • Full object needs many lookups • Streaming subtree downward heavy |
;; | **Parent‑down (↓)** | parent row embeds `[ #ref child‑1, #ref child‑2 … ]` inside **V** | Walk **downward** (natural for BFS) | • One‑shot fetch of subtree • Better for breadth‑first queries | • Parent row rewritten on each child add • Child alone не знает владельца |

;; **Owner vs Parent**  
;; *Parent* — физическая вложенность (one true ancestor).  
;; *Owner* — логическая группировка, вычисляется на запросе или хранится отдельным тегом; может быть множественным.

;; **Hybrid**: Child‑up pointer + async cached list in parent (Bloom or vector) → чтение вниз быстро, запись дёшево.

---

