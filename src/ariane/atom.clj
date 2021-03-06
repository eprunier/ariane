(ns ariane.atom
  "Namespace for parsing Atom feeds."
  (:require [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml]))

(defn- add-map-entry
  "Add key/value entry to map only if value is not nil."
  [to [key value]]
  (if value
    (assoc to key value)
    to))

(defn- links
  [links]
  (vec (for [link links]
         (let [link-spec {:href 
                          (first (zip-xml/xml-> link (zip-xml/attr :href)))}]
           (if-let [rel (first (zip-xml/xml-> link (zip-xml/attr :rel)))]
             (assoc link-spec :rel rel)
             link-spec)))))

(defn- infos
  [root]
  (-> {}
      (add-map-entry [:title (first (zip-xml/xml-> root :title zip-xml/text))])
      (add-map-entry [:description (first (zip-xml/xml-> root :summary zip-xml/text))])
      (add-map-entry [:updated (first (zip-xml/xml-> root :updated zip-xml/text))])
      (add-map-entry [:links (links (zip-xml/xml-> root :link))])))

(defn- authors
  [authors]
  (vec (for [author authors]
         {:name (first (zip-xml/xml-> author :name zip-xml/text))
          :uri (first (zip-xml/xml-> author :uri zip-xml/text))})))

(defn- entries 
  [root]
  (for [entry (zip-xml/xml-> root :entry)]
    (-> {}
        (add-map-entry [:id (first (zip-xml/xml-> entry :id zip-xml/text))])
        (add-map-entry [:title (first (zip-xml/xml-> entry :title zip-xml/text))])
        (add-map-entry [:links (links (zip-xml/xml-> entry :link))])
        (add-map-entry [:updated (first (zip-xml/xml-> entry :updated zip-xml/text))])
        (add-map-entry [:authors (authors (zip-xml/xml-> entry :author))])
        (add-map-entry [:description 
                        {:type (first (zip-xml/xml-> entry :content (zip-xml/attr :type)))
                         :content (first (zip-xml/xml-> entry :content zip-xml/text))}]))))

(defn parse-atom
  "Parse Atom feed generated by ariane.core/parse. "
  [feed]
  (let [root (zip/xml-zip feed)]
    {:infos (infos root) 
     :entries (entries root)}))
