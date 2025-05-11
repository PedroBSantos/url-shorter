FROM clojure:temurin-23-lein-2.11.2-alpine AS build
COPY . /app/
WORKDIR /app/
RUN lein clean && lein deps
RUN lein with-profile docker uberjar

FROM eclipse-temurin:23.0.1_11-jre-alpine AS final
COPY --from=build /app/target/uberjar/url-shorter-api-0.1.0-SNAPSHOT-standalone.jar /app/url-shorter-api.jar
COPY resources/config/otel.docker.properties /app/
WORKDIR /app/
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=./otel.docker.properties"
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
ENTRYPOINT java -Dconf="config/docker.edn" \
    -Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory \
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
    -jar ./url-shorter-api.jar