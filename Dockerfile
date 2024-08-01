FROM gradle:5.3.0-jdk-alpine AS build
COPY . /temp
RUN gradle build --no-daemon

FROM openjdk:17-oracle
EXPOSE 8080
#WORKDIR /app/
RUN mkdir /app
#COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar
COPY src/ ./src/
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]

#COPY target/news-application.jar /app/news-application.jar
#ENTRYPOINT ["java", "-jar", "news-application.jar"]