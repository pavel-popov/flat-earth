(ns pp.flat-earth.main
  (:require
   [cljs.core.async :refer (chan put! <! go go-loop timeout)]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [pp.flat-earth.editor :as editor]
   [taoensso.timbre :as timbre :refer-macros [info]]))


(def state (r/atom {:clicked 0}))


(def event-queue (chan))


(defn mutate-state! [event payload]
  (info "Event" event "with payload" payload)

  (case event
    :button-clicked
    (swap! state update-in [:clicked] inc)

    :reset
    (reset! state {:clicked 0})

    (info "Nothing to do for" event)))


(go-loop [[event payload] (<! event-queue)]
  (mutate-state! event payload)
  (recur (<! event-queue)))


(defn main-component []
  [:div
   [:h1 "This is a component"]
   [:button.bg-blue-100.text-blue-600.px-4
    {:on-click #(put! event-queue [:button-clicked])} "Click me"]
   [:button.bg-green-100.text-green-600.px-4
    {:on-click #(put! event-queue [:reset])} "Reset"]
   [:pre (str @state)]
   [editor/editor "Some source code"]])


(defn mount [c]
  (rdom/render [c] (.getElementById js/document "app"))
  (editor/render))

(defn reload! []
  (mount main-component)
  (print "Hello reload!"))

(defn main! []
  (mount main-component)
  (print "Hello Main"))
