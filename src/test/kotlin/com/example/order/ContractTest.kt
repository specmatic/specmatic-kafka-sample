package com.example.order

import io.specmatic.async.core.constants.AVAILABLE_SERVERS
import io.specmatic.async.core.constants.KAFKA_PORT
import io.specmatic.kafka.test.SpecmaticKafkaContractTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.EmbeddedKafkaZKBroker

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContractTest : SpecmaticKafkaContractTest {

    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private lateinit var embeddedKafka: EmbeddedKafkaBroker

        @JvmStatic
        @BeforeAll
        fun setup() {
            embeddedKafka = EmbeddedKafkaZKBroker(
                1,
                false,
                "place-order", "process-order"
            ).kafkaPorts(9092)
            embeddedKafka.afterPropertiesSet()
            System.setProperty(AVAILABLE_SERVERS, "localhost:9092")
            startApplication()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            stopApplication()
            embeddedKafka.destroy()
            System.clearProperty(AVAILABLE_SERVERS)
        }

        private fun startApplication() {
            Thread.sleep(1000)
            context = SpringApplication.run(OrderServiceApplication::class.java)
        }

        private fun stopApplication() {
            context.stop()
        }
    }
}