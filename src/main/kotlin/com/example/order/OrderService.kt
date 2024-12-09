package com.example.order

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.math.BigDecimal

private const val ORDER_STATUS_PROCESSED = "PROCESSED"
private const val ORDER_STATUS_CANCELLED = "CANCELLED"
private const val NOTIFICATION_TYPE_ORDER_PLACED = "ORDER_PLACED"
private const val SERVICE_NAME = "order-service"

@Service
class OrderService(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    init {
        println("$SERVICE_NAME started running..")
    }

    companion object {
        private const val PLACE_ORDER_TOPIC = "place-order"
        private const val PROCESS_ORDER_TOPIC = "process-order"
        private const val NOTIFICATION_TOPIC = "notification"
    }

    @KafkaListener(topics = [PLACE_ORDER_TOPIC])
    fun run(orderRequest: Message<String>, ack: Acknowledgment) {
        val payload = orderRequest.payload
        val requestId = orderRequest.headers.get(
            "requestId",
            ByteArray::class.java
        )?.decodeToString().orEmpty()
        println("[$SERVICE_NAME] Received message on topic $PLACE_ORDER_TOPIC - $payload")

        val orderRequestJson = try {
            jacksonObjectMapper().apply {
                configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
            }.readValue(payload, OrderRequest::class.java)
        } catch(e: Exception) {
            throw e
        }
        processMessage(orderRequestJson, requestId)
        ack.acknowledge()
    }

    private fun processMessage(orderRequest: OrderRequest, requestId: String) {
        sendMessageOnNotificationTopic()
        sendMessageOnProcessOrderTopic(orderRequest, requestId)
    }

    private fun sendMessageOnProcessOrderTopic(orderRequest: OrderRequest, requestId: String) {
        val totalAmount = orderRequest.orderItems.sumOf { it.price * BigDecimal(it.quantity) }
        val taskMessage = """{"totalAmount": $totalAmount, "status": "$ORDER_STATUS_PROCESSED"}"""

        println("[$SERVICE_NAME] Publishing a message on $PROCESS_ORDER_TOPIC topic: $taskMessage")

        val producerRecord = ProducerRecord<String, String>(PROCESS_ORDER_TOPIC, taskMessage).apply {
            if(requestId.isNotEmpty()) {
                headers().add(
                    RecordHeader("requestId", requestId.toByteArray())
                )
            }
        }

        kafkaTemplate.send(producerRecord)
    }

    private fun sendMessageOnNotificationTopic() {
        val taskMessage = """{"message": "Order processed successfully", "type": "$NOTIFICATION_TYPE_ORDER_PLACED"}"""
        println("[$SERVICE_NAME] Publishing a message on $NOTIFICATION_TOPIC topic: $taskMessage")
        kafkaTemplate.send(NOTIFICATION_TOPIC, taskMessage)
    }
}

data class OrderRequest(
    val orderItems: List<OrderItem>,
    val id: Int
)

data class OrderItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: BigDecimal
)
