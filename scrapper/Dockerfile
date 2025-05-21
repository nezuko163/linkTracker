FROM eclipse-temurin:23-jdk
LABEL authors="nezuko163"

WORKDIR /app

COPY ./target/scrapper-1.0.jar scrapper.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "scrapper.jar"]
