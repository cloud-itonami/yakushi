(ns yakushi.repository-contract-test
  (:require [clojure.edn :as edn] [clojure.java.io :as io]
            [clojure.test :refer [deftest is]]))
(def root (io/file (System/getProperty "user.dir")))
(deftest canonical-edn-contract
  (doseq [path ["manifest.edn" "products.edn" "schema/schema.edn" "schema/kotoba.edn" "data/seed.kotoba.edn"]]
    (is (some? (edn/read-string (slurp (io/file root path)))) path)))
(deftest artifact-boundaries
  (doseq [f (filter #(.isFile %) (file-seq root))]
    (when (re-find #"\\.(?:json|jsonld|bpmn)$" (.getName f))
      (is (.startsWith (.getCanonicalPath f) (.getCanonicalPath (io/file root "wire")))))
    (is (not (re-find #"\\.(?:go|sh)$" (.getName f))))))
