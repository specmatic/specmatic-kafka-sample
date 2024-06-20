package com.example.order

import com.google.gson.Gson
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.Acknowledgment
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

    // TODO - use jackson instead or kotlinx serialization
    private val gson = Gson()

    @KafkaListener(topics = [PLACE_ORDER_TOPIC])
    fun run(orderRequest: String, ack: Acknowledgment) {
        println("[$SERVICE_NAME] Received message on topic $PLACE_ORDER_TOPIC - $orderRequest")
        processMessage(gson.fromJson(orderRequest, OrderRequest::class.java))
        ack.acknowledge()
    }

    private fun processMessage(orderRequest: OrderRequest) {
        sendMessageOnProcessOrderTopic(orderRequest)
        sendMessageOnNotificationTopic()
    }

    private fun sendMessageOnProcessOrderTopic(orderRequest: OrderRequest) {
        val totalAmount = orderRequest.orderItems.sumOf { it.price * BigDecimal(it.quantity) }
        val taskMessage = """{"totalAmount": $totalAmount, "status": "$ORDER_STATUS_PROCESSED"}"""

        println("[$SERVICE_NAME] Publishing a message on $PROCESS_ORDER_TOPIC topic: $taskMessage")
        kafkaTemplate.send(PROCESS_ORDER_TOPIC, taskMessage)
    }

    private fun sendMessageOnNotificationTopic() {
        val taskMessage = """{"message": "Order processed successfully", "type": "$NOTIFICATION_TYPE_ORDER_PLACED"}"""
        println("[$SERVICE_NAME] Publishing a message on $NOTIFICATION_TOPIC topic: $taskMessage")
        kafkaTemplate.send(NOTIFICATION_TOPIC, taskMessage)
    }
}

data class OrderRequest(
    val orderItems: List<OrderItem>,
    val status: String
)

data class OrderItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: BigDecimal
)
