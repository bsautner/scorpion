package scorpion

import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage

class EventListener: MqttListener {
    override fun onConnected() {
        println("MQTT Connected!")
    }

    override fun connectionLost(cause: Throwable?) {
       cause?.printStackTrace()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        TODO("Not yet implemented")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        TODO("Not yet implemented")
    }
}