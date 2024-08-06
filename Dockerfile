FROM gradle:8.4-jdk21 as build
COPY . /app
WORKDIR /app
RUN chown -R gradle /app
USER gradle
RUN gradle build -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/build/libs/orchestrator-0.0.1-SNAPSHOT.jar /app/application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]