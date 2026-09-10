(require '[clojure.test :as t])
(def suites '[yakushi.methods.test-charter-gates
              yakushi.methods.test-agent
              yakushi.repository-contract-test])
(apply require suites)
(let [{:keys [fail error]} (apply t/run-tests suites)]
  (when-not (zero? (+ fail error)) (System/exit 1)))
