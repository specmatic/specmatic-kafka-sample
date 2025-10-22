package com.example.order

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.EmbeddedKafkaZKBroker
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@EnabledIf(value = "isNonCIOrLinux", disabledReason = "Run only on Linux in CI; all platforms allowed locally")
class ContractTestUsingTestContainer {

    companion object {
        @JvmStatic
        fun isNonCIOrLinux(): Boolean = System.getenv("CI") != "true" || System.getProperty("os.name").lowercase().contains("linux")

        private lateinit var embeddedKafka: EmbeddedKafkaBroker

        @JvmStatic
        @BeforeAll
        fun setup() {
            val brokerProperties = mapOf(
                "listeners" to "PLAINTEXT://0.0.0.0:9092",
                "advertised.listeners" to "PLAINTEXT://localhost:9092"
            )
            embeddedKafka =
                EmbeddedKafkaZKBroker(
                    1,
                    false,
                    "place-order",
                    "process-order",
                    "notification"
                ).kafkaPorts(9092).brokerProperties(brokerProperties)
            embeddedKafka.afterPropertiesSet()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            embeddedKafka.destroy()
            Thread.sleep(200)
        }
    }

    private val testContainer: GenericContainer<*> =
        GenericContainer("specmatic/specmatic-kafka")
            .withCommand(
                "test",
                "--broker=localhost:9092",
            )
            .withFileSystemBind(
                "./specmatic.yaml",
                "/usr/src/app/specmatic.yaml",
                BindMode.READ_ONLY,
            ).withFileSystemBind(
                "./build/reports/specmatic",
                "/usr/src/app/build/reports/specmatic",
                BindMode.READ_WRITE,
            ).waitingFor(Wait.forLogMessage(".*The coverage report is generated.*", 1))
            .withNetworkMode("host")
            .withLogConsumer { print(it.utf8String) }

    @Test
    fun specmaticContractTest() {
        testContainer.start()
        val hasSucceeded = testContainer.logs.contains("Result: FAILED").not()
        assertThat(hasSucceeded).isTrue()
    }
}
