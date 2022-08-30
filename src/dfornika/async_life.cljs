(ns ^:figwheel-hooks dfornika.async-life
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [reagent.dom :as rdom]))


(def num-cells 64)
(def canvas-width 500)
(def canvas-height 500)
(def canvas-background-color "black")

;; define your app data so that it doesn't get over-written on reload
(defonce db (r/atom {:xs []
                     :ys []
                     :dxs []
                     :dys []
                     :colors []}))

(defn random-pos
  []
  (-> (rand)
      (* 400)
      (+ 50)))

(defn init-random-positions
  [db num-cells]
  (let [xs (into [] (repeatedly num-cells random-pos))
        ys (into [] (repeatedly num-cells random-pos))
        dxs (into [] (repeat num-cells 0.0))
        dys (into [] (repeat num-cells 0.0))]
    (swap! db assoc :xs xs :ys ys :dxs dxs :dys dys)))

(def colors [:red
              :blue
              :green
             :yellow])


(defn random-color
  []
  (rand-nth colors))

(defn init-random-colors
  [db num-cells]
  (let [colors (into [] (repeatedly num-cells random-color))]
    (swap! db assoc :colors colors)))


(defn get-cell-by-id
  [db id]
  (let [db-val @db
        x (get (:xs db-val) id)
        y (get (:ys db-val) id)
        dx (get (:dxs db-val) id)
        dy (get (:dys db-val) id)
        color (get (:colors db-val) id)]
    {:x x
     :y y
     :dx dx
     :dy dy
     :color color}))

(defn get-ids-by-color
  [db color]
  (let [db-val @db
        indexed-colors (map-indexed vector (:colors db-val))]
    (->> indexed-colors
         (filter #(= (second %) color))
         (map first))))



(defn get-canvas
  []
  (gdom/getElement "canvas"))

(def canvas (get-canvas))


(defn create-cell
  ([id x y c]
   {:id id
    :x x
    :y y
    :vx 0
    :vy 0
    :color c})
  ([id c]
   {:id id
    :x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :color c})
  ([id]
   {:id id
    :x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :color (random-color)}))

(defn update-cell
  ([db id x y c]
   (let [db-val @db
         new-xs (assoc-in db-val [:xs id] x)
         new-ys (assoc-in db-val [:ys id] y)
         new-colors (assoc-in db-val [:colors id] c)]
     (swap! db assoc :xs new-xs :ys new-ys :colors new-colors)))
  ([db id c]
   {:id id
    :x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :color c})
  ([id]
   {:id id
    :x (random-pos)
    :y (random-pos)
    :vx 0
    :vy 0
    :color (random-color)}))

(defn draw-cell-by-id
  [db id]
  (let [cell (get-cell-by-id db id)
        ctx (-> js/document
                (.getElementById "canvas")
                (.getContext "2d"))
        tau (* 2 Math/PI)]
    (set! (.-fillStyle ctx) (name (:color cell)))
    (doto ctx
      (.beginPath)
      (.arc (:x cell) (:y cell) 5 0 tau)
      (.fill))))

(defn clear!
  []
  (let [ctx (-> js/document
                (.getElementById "canvas")
                (.getContext "2d"))]
    (set! (.-fillStyle ctx) canvas-background-color)
    (.fillRect ctx 0 0 canvas-width canvas-height)))

(defn render!
  [db]
  (clear!)
  (doseq [id (range (count (:xs @db)))]
    (draw-cell-by-id db id)))

(defn get-app-element []
  (gdom/getElement "app"))



(defn root []
  [:canvas {:id "canvas"
            :width canvas-width
            :height canvas-height
            :style {:background "black"}}])

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
  (init-random-positions db num-cells)
  (init-random-colors db num-cells)
  (mount-app-element)
  
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)

   )


