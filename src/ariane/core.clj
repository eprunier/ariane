(ns ariane.core
  "Generic namespace for feed type autodetection and parsing."
  (:require [clojure.xml :as xml]
            [ariane.rss :as rss]
            [ariane.atom :as atom]))

(defn- rss-feed?
  [feed]
  (-> feed :tag (= :rss)))

(defn- atom-feed?
  [feed]
  (-> feed :tag (= :feed)))

(defn parse
  "Detects the feed type and calls the appropriate parser."
  [source]
  (let [feed (xml/parse source)]
    (cond 
     (rss-feed? feed) (rss/parse-rss feed)
     (atom-feed? feed) (atom/parse-atom feed)
     :else (println "Unsupported format"))))
