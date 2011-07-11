(ns MyBot
  (:use ants.ants))

(def directions [:north :east :west :south])

(defn nearby [ant radius]
  (for [dist (range 1 radius)
        :let [deltas (interleave (range 0 dist)
                                 (range 0 (- dist) -1))]
        x deltas
        y deltas]
    #_(move-offset ant [x y])
    (map + ant [x y])))

(defn simple-bot []
  (try
    (reduce (fn [state ant]
              (when-let [dir (binding [*game-state* state]
                               (rand-nth (filter #(unoccupied?
                                                   (can-move? ant %))
                                                 directions)))]
                (let [new-pos (move-ant ant dir)]
                  (move ant dir)        ; send actual command
                  (update-in state [:ants] #(-> % ; update state
                                                (disj ant)
                                                (conj new-pos))))))
            *game-state*
            (my-ants))
    (catch Throwable t
      (spit "error.log"
            (with-out-str
              (.printStackTrace t)))
      (System/exit 0))))

(start-game simple-bot)
