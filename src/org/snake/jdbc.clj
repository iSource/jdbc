(ns org.snake.jdbc
  (:gen-class)
  (:import [java.net URI]
           [java.sql BatchUpdateException DriverManager
            PreparedStatement ResultSet SQLException Statement Types]
           [java.util Hashtable Map Properties]
           [javax.sql DataSource])
  (:require [clojure.string :as str]
            [clojure.walk :as walk]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn as-sql-name
  "
  把一个x.y的参数拆开来，然后遍历应用函数f，然后在用.拼接起来。
  "
  ([f]
   (fn [x]
     (as-sql-name f x)))
  ([f x]
   (let [n (name x)
         i (.indexOf n (int \.))]
     (if (= -1 i)
       (f n)
       ;; 参数x用.来分割成序列
       ;; 遍历所有序列元素 应用函数f
       ;; 把所有的结果再用.来接连
       (str/join "." (map f (.split n "\\.")))))))

(defn quoted
  "
  把参数包裹起来
  "
  ([q]
   (fn [x]
     (quoted q x)))
  ([q x]
   (if (vector? q)
     (str (first q) x (last q))
     (str q x q))))

(defn- ^Properties as-properties
  "
  转化任何序列对到java.util.Properties实例
  使用as-sql-name来转化键值对为字符串
  "
  [m]
  (let [p (Properties.)]
    (doseq [[k v] m]
      (.setProperty p (as-sql-name identity k)
                    (if (instance? clojure.lang.Named v)
                      (as-sql-name identity v)
                      (str v))))
    p))

(defprotocol Connectable
  (add-connection [db connection])
  (get-level [db]))

(defn- inc-level
  [db]
  (let [nested-db (update-in db [:level] (fnil inc 0))]
    (if (= 1 (:level nested-db))
      (assoc nested-db :rollback (atom false))
      nested-db)))

