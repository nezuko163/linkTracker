![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.

Для дополнительной справки: [HELP.md](HELP.md)

# Запуск приложения

1) создать файл .env и записать в него такие значение
- BOT_TOKEN
- SO_TOKEN_KEY
- SO_ACCESS_TOKEN
- GITHUB_TOKEN

2) запустить команды для сборки скраппера и бота
mvn clean -pl scrapper -am package -DskipTests
mvn clean -pl bot -am package -DskipTests

3) запустить докер композ

