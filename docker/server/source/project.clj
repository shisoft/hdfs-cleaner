(defproject hdfs-cleaner "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [aleph "0.4.3"]
                 [manifold "0.1.6"]
                 [cluster-connector "0.1.0-SNAPSHOT"]
                 [com.taoensso/timbre "4.10.0"]
                 [cheshire "5.7.1"]
                 [dscribe-logger-clj "0.1.0-SNAPSHOT"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.6.0"]
                 [org.apache.httpcomponents/httpclient "4.5.3"]
                 [org.apache.hadoop/hadoop-common "2.6.0-cdh5.11.0"]
                 [org.apache.hadoop/hadoop-hdfs "2.6.0-cdh5.11.0"]
                 [com.cemerick/pomegranate "0.3.1"]]
  :main ^:skip-aot hdfs-cleaner.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :repositories [["releases" {:url "http://maven.corp.factual.com/nexus/content/repositories/releases"
                              :sign-releases false}]
                 ["snapshots" "http://maven.corp.factual.com/nexus/content/repositories/snapshots"]
                 ["factual" "http://maven.corp.factual.com/nexus/content/groups/public/"]
                 ["cloudera" "https://repository.cloudera.com/artifactory/cloudera-repos"]])
