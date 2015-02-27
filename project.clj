(defproject org.snake/jdbc "0.3.7-SNAPSHOT"
  :description "一个底层基于JDBC数据库访问的clojure包装"
  :url "https://github.com/clojure/java.jdbc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src"]
  :test-paths ["test"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [mysql/mysql-connector-java "5.1.25"]]
  :main ^:skip-aot org.snake.jdbc
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
