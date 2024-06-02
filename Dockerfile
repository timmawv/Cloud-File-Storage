FROM maven:3.8.5-openjdk-17 AS build
COPY /src /src
COPY pom.xml /
RUN mvn clean package -Dmaven.test.skip

FROM eclipse-temurin:17-jre
WORKDIR /
COPY --from=build /target/*.jar cloud_storage.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cloud_storage.jar", "--spring.profiles.active=prod"]