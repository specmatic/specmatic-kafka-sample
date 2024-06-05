package com.example.order

import com.example.order.testcontainers.KafkaTestContainer
import `in`.specmatic.async.junit.SpecmaticKafkaContractTest
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
            kafkaBroker.start()
            Thread.sleep(1000)

            context = SpringApplication.run(OrderServiceApplication::class.java)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            context.stop()

            kafkaBroker.stop()
            Thread.sleep(1000)
        }
    }

}