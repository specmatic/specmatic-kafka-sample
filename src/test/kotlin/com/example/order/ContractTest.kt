package com.example.order

import com.example.order.testcontainers.KafkaTestContainer
import `in`.specmatic.async.junit.SpecmaticKafkaContractTest
import `in`.specmatic.async.utils.CONSUMER_GROUP_ID
import `in`.specmatic.async.utils.EXAMPLES_DIR
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class ContractTest : SpecmaticKafkaContractTest {

    companion object {
        private val kafkaBroker = KafkaTestContainer()
        private lateinit var context: ConfigurableApplicationContext

        @JvmStatic
        @BeforeAll
        fun setup() {
            System.setProperty(EXAMPLES_DIR, "src/test/resources")
            System.setProperty(CONSUMER_GROUP_ID, "order-consumer-group-id")
            kafkaBroker.start()

            context = SpringApplication.run(OrderServiceApplication::class.java)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            context.stop()

            kafkaBroker.stop()
        }
    }

}