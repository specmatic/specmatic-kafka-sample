# Specmatic Kafka Sample

* [Specmatic Website](https://specmatic.io)
* [Specmatic Documenation](https://specmatic.io/documentation.html)

This sample project demonstrates how we can run contract tests against a service which interacts with a kafka broker.

**NOTE**: This project uses **AsyncAPI 2.6** specification. For equivalent sample project that uses **AsyncAPI 3.0** spec please refer to **[specmatic-kafka-sample-asyncapi3](https://github.com/znsio/specmatic-kafka-sample-asyncapi3)**.

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

## Run the contract tests using specmatic-kafka docker image

1. Start the kafka broker using below command.
   ```shell
   docker compose up
   ```
2. Run the application.
   ```shell
   ./gradlew bootRun
   ```
3. Run the contract tests.
   ```shell
   docker run --network host -v "$PWD/specmatic.yaml:/usr/src/app/specmatic.yaml" -v "$PWD/src/test/resources:/usr/src/app/examples" -v "$PWD/build/reports:/usr/src/app/build/reports" znsio/specmatic-kafka:0.22.13 test --examples=examples
   ```

## Get information around other CLI args exposed by specmatic-kafka docker image

1. To get information around all the CLI args of the `virtualize` command, run the following.
   ```shell
    docker run znsio/specmatic-kafka virtualize --help
   ```
2. To get information around all the CLI args of the `test` command, run the following.
   ```shell
    docker run znsio/specmatic-kafka test --help
   ```
