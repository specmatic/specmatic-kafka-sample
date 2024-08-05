# Specmatic Kafka Sample

* [Specmatic Website](https://specmatic.io)
* [Specmatic Documenation](https://specmatic.io/documentation.html)

This sample project demonstrates how we can run contract tests against a service which interacts with a kafka broker. 

## Background
This project includes a consumer that listens to messages on a specific topic.
Upon receiving a message, the consumer processes it and publishes a new message to two other designated topics.

![Specmatic Kafka Sample Architecture](AsyncAPI-Request-Reply-Draft.gif)


## Pre-requisites
* Gradle
* JDK 17+
* Docker

## Run the tests
```shell
./gradlew clean test
```
