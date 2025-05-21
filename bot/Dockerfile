FROM eclipse-temurin:23-jdk
LABEL authors="nezuko163"

WORKDIR /app

COPY ./target/bot-1.0.jar bot.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "bot.jar"]
