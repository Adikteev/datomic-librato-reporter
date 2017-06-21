(ns datomic-librato-reporter
  (:require [clojure.string :as s]
            [environ.core :refer [env]]
            [metrics.meters :as mm])
  (:import (java.net InetAddress)
           (java.util.concurrent TimeUnit)
           (com.codahale.metrics SharedMetricRegistries MetricRegistry)
           (com.librato.metrics LibratoReporter)))

(defn ^MetricRegistry shared-registry [name]
  (SharedMetricRegistries/getOrCreate name))


(defn start-librato-reporter [registry]
  (let [source-prefix (str (:app-name env) "-" (:app-env env))
        source-suffix (.getCanonicalHostName (InetAddress/getLocalHost))
        source-name (str source-prefix "." source-suffix)
        librato-email (:librato-email env)
        librato-api-token (:librato-api-token env)
        builder (LibratoReporter/builder registry librato-email librato-api-token source-name)
        librato-reporter (.build builder)]
    (.start librato-reporter 5 TimeUnit/SECONDS)
    librato-reporter))

(def ^:dynamic *client* nil)

(defn client []
  (if *client*
    *client*
    (if (and (env :librato-api-token) (env :librato-email))
      (let [initialized (start-librato-reporter (shared-registry "datomic"))]
        (println (str "event=starting-librato-client email=" (env :librato-email) " token=" (env :librato-api-token)))
        (alter-var-root #'*client* (constantly initialized))
        initialized)
      (do
        (println (str "event=not-starting-librato-client email=" (env :librato-email) " token=" (env :librato-api-token)))
        nil))))

(defn send-event [name value]
  (if-let [actual-client (client)]
    (-> (shared-registry "datomic")
        (mm/meter name)
        (mm/mark! value))))


(defn report-datomic-metrics-to-librato [metrics]
  (doseq [[metric-name value] metrics]
    (if (map? value)
      (doseq [[sub-metric-name sub-metric-value] value]
        (send-event [(:app-name env) "datomic" (clojure.string/join #"." [(name metric-name) (name sub-metric-name)])] sub-metric-value))
      (send-event [(:app-name env) "datomic" (name metric-name)] value))))
