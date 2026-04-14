FROM eclipse-temurin:25-jre AS builder
WORKDIR /build
COPY target/match-service-0.0.1-SNAPSHOT.jar .
RUN java -Djarmode=tools -jar match-service-0.0.1-SNAPSHOT.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre
RUN useradd -ms /bin/sh -u 1001 app
RUN apt-get update && apt-get install curl -y
USER app
WORKDIR /app
COPY --from=builder --chown=app:app /build/extracted/dependencies/ .
COPY --from=builder --chown=app:app /build/extracted/spring-boot-loader/ .
COPY --from=builder --chown=app:app /build/extracted/snapshot-dependencies/ .
COPY --from=builder --chown=app:app /build/extracted/application/ .
ENTRYPOINT ["java", "-jar", "match-service-0.0.1-SNAPSHOT.jar"] 