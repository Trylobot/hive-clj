(defproject trylobot/hive "0.1.3-SNAPSHOT"
  :description "Hive(tm) core logic library, rewritten in full native clojure"
  :url "https://github.com/Trylobot/hive-clj"
  :license {:name "Research License 1.0"
            :url "https://github.com/Trylobot/hive-clj/blob/master/LICENSE.md"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [prismatic/schema "1.0.5"]]
  :source-paths ["src"]
  :test-paths ["src/hive/test"]
  :main ^:skip-aot hive.main
  :target-path "target/%s"
  :profiles {
    :uberjar {:aot :all} }
)
