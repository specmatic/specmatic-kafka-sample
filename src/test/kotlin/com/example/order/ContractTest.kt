package com.example.order

import io.specmatic.async.junit.SpecmaticKafkaContractTest
import io.specmatic.async.utils.CONSUMER_GROUP_ID
import io.specmatic.async.utils.EXAMPLES_DIR
import io.specmatic.kafka.mock.KafkaMock
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

class ContractTest : SpecmaticKafkaContractTest {

    companion object {
        private lateinit var context: ConfigurableApplicationContext
        private lateinit var kafkaMock: KafkaMock

        @JvmStatic
        @BeforeAll
        fun setup() {
            System.setProperty(EXAMPLES_DIR, "src/test/resources")
            // NOTE - cannot get it from application.properties using @Value since @Value does not work in a companion object.
            System.setProperty(CONSUMER_GROUP_ID, "order-consumer-group-id")
            kafkaMock = KafkaMock.startInMemoryBroker("localhost", 9092)

            context = SpringApplication.run(OrderServiceApplication::class.java)

            // wait for application to be up before running the tests.
            // needs better approach
            // -> specmatic-kafka can hit the health actuator endpoint for a certain time period to check if it's up
            Thread.sleep(5000)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            context.stop()
            kafkaMock.stop()
        }
    }

}