(defproject url-shorter-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/tools.logging "1.3.0"]
                 [ring/ring-core "1.14.1"]
                 [ring/ring-jetty-adapter "1.14.1"]
                 [ring/ring-devel "1.14.1"]
                 [ring/ring-json "0.5.1"]
                 [compojure/compojure "1.7.1"]
                 [ring/ring-defaults "0.6.0"]
                 [ring-logger/ring-logger "1.1.1"]
                 [ring-cors "0.1.13"]
                 [org.apache.logging.log4j/log4j-core "2.24.3"]
                 [org.apache.logging.log4j/log4j-jcl "2.24.3"]
                 [org.apache.logging.log4j/log4j-jul "2.24.3"]
                 [org.apache.logging.log4j/log4j-slf4j2-impl "2.24.3"]
                 [com.novemberain/langohr "5.5.0"]
                 [celtuce-core "0.4.2"]]
  :main ^:skip-aot url-shorter-api.core
  :target-path "target/%s"
  :profiles {:dev {:aot :all
                   :jvm-opts ["-javaagent:resources/otel/opentelemetry-javaagent.jar"
                              "-Dotel.javaagent.configuration-file=resources/config/otel.dev.properties"
                              "-Dclojure.compiler.direct-linking=true"
                              "-Dconf=config/dev.edn"
                              "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"
                              "-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager"]}
             :docker {:aot :all
                      :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                 "-Dconf=config/docker.edn"
                                 "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"
                                 "-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager"]}})
