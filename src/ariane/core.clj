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
     (rss-feed? feed) (rss/parse-rss (:content feed))
     (atom-feed? feed) (atom/parse-atom (:content feed))
     :else (println "Unkown format"))))

(defn infos
  "Returns feed informations."
  [data]
  (filter (fn [[name _]]
            (not= :item name))
          data))

(defn items
  "Returns feed items."
  [data]
  (filter (fn [[name _]]
            (= :item name)) 
          data))
