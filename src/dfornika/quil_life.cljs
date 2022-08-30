(ns ^:figwheel-hooks dfornika.quil-life
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [quil.core :as q :include-macros true]
   [quil.middleware :as qm]))

;; define your app data so that it doesn't get over-written on reload
(defonce db (atom {}))


(def colours ["red"
              "blue"
              "green"
              "yellow"])

(defn random-colour
  []
  (rand-nth colours))

(defn random-pos
  []
  (-> (rand)
      (* 400)
      (+ 50)))

(defn make-atom
  ([x y c]
   {:x x
    :y y
    :vx 0
    :vy 0
    :colour c})
  ([c]
   {:x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :colour c})
  ([]
   {:x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :colour (random-colour)}))

(defn draw []
  (q/background 255)
  (q/fill (:fill @db))
  (q/ellipse 56 46 55 55))

(defn sketch []
  (q/sketch
    :draw draw
    :host "sketch"
    :middleware [qm/fun-mode]
    :size [300 300]))


(defn get-app-element []
  (gdom/getElement "app"))

(defn root []
  [:div {:id "sketch"}])

(defn mount [el]
  (rdom/render [root] el))

(defn mount-app-element []
  (when-let [app (get-app-element)]
    (mount app)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

   )


