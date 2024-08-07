FROM gradle:5.3.0-jdk-alpine AS build
COPY . /temp
RUN gradle build --no-daemon

FROM openjdk:17-oracle
EXPOSE 8080
RUN mkdir /app
COPY src/ ./src/
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]