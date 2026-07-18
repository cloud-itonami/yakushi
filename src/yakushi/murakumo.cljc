(ns yakushi.murakumo
  "Pure cljc actor boundary generated from manifest migration scaffold."
  (:require [clojure.string :as str]))

(def actor-did
  "did:web:etzhayyim.com:yakushi")

(def common-gates
  [:council-charter-attestation
   :no-platform-held-key-baseline
   :no-probing-baseline
   :murakumo-only-inference-baseline
   :did-primary-baseline
   :append-only-gate-baseline
   :kotoba-only-substrate-baseline])

(defn collection
  [name]
  (str "com.etzhayyim.yakushi." name))

(def cell-specs {
  :pharmarawmaterialcell {:legacy-cell "PharmaRawMaterialCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmarawmaterialcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmarawmaterialcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmaapisynthesiscell {:legacy-cell "PharmaApiSynthesisCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmaapisynthesiscell")]
     :required-gates common-gates
     :trigger "manifest cell pharmaapisynthesiscell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmapurificationcell {:legacy-cell "PharmaPurificationCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmapurificationcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmapurificationcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmaqccell {:legacy-cell "PharmaQcCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmaqccell")]
     :required-gates common-gates
     :trigger "manifest cell pharmaqccell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmasterilefillfinishcell {:legacy-cell "PharmaSterileFillFinishCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmasterilefillfinishcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmasterilefillfinishcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmacontainercell {:legacy-cell "PharmaContainerCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmacontainercell")]
     :required-gates common-gates
     :trigger "manifest cell pharmacontainercell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmapackagingcell {:legacy-cell "PharmaPackagingCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmapackagingcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmapackagingcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmacoldchaincell {:legacy-cell "PharmaColdChainCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmacoldchaincell")]
     :required-gates common-gates
     :trigger "manifest cell pharmacoldchaincell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmapostmarketsurveillancecell {:legacy-cell "PharmaPostMarketSurveillanceCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmapostmarketsurveillancecell")]
     :required-gates common-gates
     :trigger "manifest cell pharmapostmarketsurveillancecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmaadverseeventcell {:legacy-cell "PharmaAdverseEventCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmaadverseeventcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmaadverseeventcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmatabletmanufacturecell {:legacy-cell "PharmaTabletManufactureCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmatabletmanufacturecell")]
     :required-gates common-gates
     :trigger "manifest cell pharmatabletmanufacturecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmatopicalformulationcell {:legacy-cell "PharmaTopicalFormulationCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmatopicalformulationcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmatopicalformulationcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmachiralresolutioncell {:legacy-cell "PharmaChiralResolutionCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmachiralresolutioncell")]
     :required-gates common-gates
     :trigger "manifest cell pharmachiralresolutioncell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :pharmaliquidformulationcell {:legacy-cell "PharmaLiquidFormulationCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "pharmaliquidformulationcell")]
     :required-gates common-gates
     :trigger "manifest cell pharmaliquidformulationcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
})

(defn safe-rkey
  [s]
  (let [clean (-> (str s)
                  (str/replace #"^did:web:" "")
                  (str/replace #"[^A-Za-z0-9._~-]" "-"))]
    (if (str/blank? clean) "unknown" clean)))

(defn gate-value
  [attestations gate]
  (or (get attestations gate)
      (get attestations (name gate))
      (when (set? attestations) (attestations gate))
      (when (set? attestations) (attestations (name gate)))))

(defn missing-gates
  [spec attestations]
  (->> (:required-gates spec)
       (remove #(boolean (gate-value attestations %)))
       vec))

(defn put-record-effect
  [collection rkey record]
  {:op :mst/put-record
   :actor actor-did
   :collection collection
   :rkey rkey
   :record record})

(defn records-for
  [spec {:keys [records record computed-at request-id]
         :as input}]
  (let [input-records (cond
                        (map? records) records
                        (some? record) {0 record}
                        :else {})
        base {:actorDid actor-did
              :computedAt computed-at
              :legacyCell (:legacy-cell spec)
              :phase (:phase spec)
              :requestId request-id
              :actorBoundary "cljc-migration-scaffold"
              :scaffold true
              :constitutionalStatus "attested-plan"}]
    (map-indexed
     (fn [idx coll]
       (let [record* (merge {:$type coll}
                            base
                            (or (get input-records coll)
                                (get input-records idx)
                                {}))
             rkey (safe-rkey (or (:rkey record*)
                                 (get record* "rkey")
                                 (:tid record*)
                                 request-id
                                 (str (:legacy-cell spec) "-" idx)))]
         {:collection coll
          :record record*
          :rkey rkey}))
     (:collections spec))))

(defn cell-plan
  [cell-key {:keys [attestations] :as input}]
  (let [spec (get cell-specs cell-key)]
    (when-not spec
      (throw (ex-info "unknown cell" {:cell cell-key})))
    (let [missing (missing-gates spec attestations)]
      (merge
       {:cell cell-key
        :legacy-cell (:legacy-cell spec)
        :actor actor-did
        :phase (:phase spec)
        :murakumo-node (:murakumo-node spec)
        :trigger (:trigger spec)
        :ceiling (:ceiling spec)
        :required-gates (:required-gates spec)
        :missing-gates missing}
       (if (seq missing)
         {:status :blocked
          :effects []}
         (let [planned-records (records-for spec input)]
           {:status :ready
            :records (vec planned-records)
            :effects (mapv (fn [{:keys [collection record rkey]}]
                             (put-record-effect collection rkey record))
                           planned-records)}))))))

(defn all-cell-plans
  [input]
  (into {}
        (map (fn [cell-key] [cell-key (cell-plan cell-key input)]))
        (keys cell-specs)))
