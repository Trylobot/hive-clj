(defproject hive "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [prismatic/schema "1.0.5"]]
  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :main ^:skip-aot hive.core.core
  :target-path "target/%s"
  :profiles {
    :uberjar {:aot :all}
    :dev {:plugins [
      [venantius/ultra "0.4.1"]
      [lein-autoreload "0.1.0"]]} }
)
