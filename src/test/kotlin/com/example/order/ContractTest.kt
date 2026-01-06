package com.example.order

import io.specmatic.async.core.constants.AVAILABLE_SERVERS
import io.specmatic.kafka.test.SpecmaticKafkaContractTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.EmbeddedKafkaZKBroker

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContractTest : SpecmaticKafkaContractTest {
    private lateinit var embeddedKafka: EmbeddedKafkaBroker

    @BeforeAll
    fun setup() {
        embeddedKafka =
            EmbeddedKafkaZKBroker(
                1,
                false,
                "place-order",
                "process-order",
                "notification"
            ).kafkaPorts(9092)
        embeddedKafka.afterPropertiesSet()
        System.setProperty(AVAILABLE_SERVERS, "localhost:9092")
    }

    @AfterAll
    fun tearDown() {
        embeddedKafka.destroy()
        Thread.sleep(200)
    }
}