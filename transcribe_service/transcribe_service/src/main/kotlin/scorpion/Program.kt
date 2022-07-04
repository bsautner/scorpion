package scorpion

import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.transcribe.Transcribe

class Program : MqttListener {

    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)
    private val transcribe = Transcribe(mqtt)

    fun start() {
        TranscribeScope.launch {
            mqtt.start()
        }

    }
    override fun onConnected() {
        println("MQTT Connected")
        transcribe.start()
    }

    override fun connectionLost(cause: Throwable?) {

    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }

}