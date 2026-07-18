(ns yakushi.murakumo-test
  (:require [clojure.test :refer [deftest is testing]]
            [yakushi.murakumo :as yakushi]))

(def full-attestations
  (into {}
        (map (fn [gate] [gate (str "attested-" (name gate))]))
        (distinct (mapcat :required-gates (vals yakushi/cell-specs)))))

(deftest maps-all-legacy-pharma-cells
  (is (= #{"pharma_adverse_event"
           "pharma_api_synthesis"
           "pharma_chiral_resolution"
           "pharma_cold_chain"
           "pharma_container"
           "pharma_liquid_formulation"
           "pharma_packaging"
           "pharma_post_market_surveillance"
           "pharma_purification"
           "pharma_qc"
           "pharma_raw_material"
           "pharma_sterile_fill_finish"
           "pharma_tablet_manufacture"
           "pharma_topical_formulation"}
         (set (map :legacy-cell (vals yakushi/cell-specs))))))

(deftest r0-gates-block-effects
  (let [plan (yakushi/cell-plan :raw-material
                                {:source-id "coa-001"
                                 :computed-at "2026-06-29T00:00:00Z"})]
    (is (= :blocked (:status plan)))
    (is (= [:council-charter-attestation
            :silen-pharma-baseline-review
            :qp-equivalent-registry
            :dangerous-goods-officer-registry
            :opcw-declaration-channel]
           (:missing-gates plan)))
    (is (empty? (:effects plan)))))

(deftest attested-raw-material-emits-mst-effect
  (let [plan (yakushi/cell-plan :raw-material
                                {:attestations full-attestations
                                 :source-id "coa-001"
                                 :computed-at "2026-06-29T00:00:00Z"
                                 :record {:tid "rm-001"
                                          :materialName "sodium cromoglicate"
                                          :grade "official"
                                          :hazardClass "low-risk"}})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= :mst/put-record (:op effect)))
    (is (= yakushi/actor-did (:actor effect)))
    (is (= "com.etzhayyim.yakushi.rawMaterialAttestation" (:collection effect)))
    (is (= "rm-001" (:rkey effect)))
    (is (= "sodium cromoglicate" (get-in effect [:record :materialName])))))

(deftest special-gates-are-cell-specific
  (testing "sterile fill-finish keeps Annex 1 and media-fill gates"
    (let [attestations (dissoc full-attestations :media-fill-3-batch-consecutive)
          plan (yakushi/cell-plan :sterile-fill-finish {:attestations attestations})]
      (is (= [:media-fill-3-batch-consecutive] (:missing-gates plan)))
      (is (empty? (:effects plan)))))
  (testing "adverse event keeps sealed-recipient and cipher registries"
    (let [attestations (dissoc full-attestations :patient-privacy-cipher-registry)
          plan (yakushi/cell-plan :adverse-event {:attestations attestations})]
      (is (= [:patient-privacy-cipher-registry] (:missing-gates plan)))
      (is (empty? (:effects plan))))))

(deftest all-cell-plans-ready-when-attested
  (let [plans (yakushi/all-cell-plans {:attestations full-attestations
                                       :lot-id "lot-001"
                                       :computed-at "2026-06-29T00:00:00Z"})]
    (is (= (set (keys yakushi/cell-specs)) (set (keys plans))))
    (is (every? #(= :ready (:status %)) (vals plans)))
    (is (= (count yakushi/cell-specs)
           (count (mapcat :effects (vals plans)))))))
