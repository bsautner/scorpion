package scorpion

import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.voice.Polly

class Program : MqttListener, VoiceCommandListener{


    private val broker =  "tcp://10.0.0.205:1883"

    private val mqtt : MQTT = MQTT(this, broker)
    private val transcribe = Transcribe(this)
    private val polly = Polly()
    var last = ""

    fun start() {
        println("Hello Kotlin")
        DeviceScope.launch {
            mqtt.start()
        }
        while (true) {
           Thread.sleep(10)
        }
    }

    override fun onConnected() {
        println("MQTT Connected!")
        polly.speak("Connected!")
        transcribe.start()
    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }

    override fun onVoiceCommand(cmd: String) {


        if (cmd.lowercase().contains("scorpion")) {

            println("Got Voice Command ${cmd}")
            if (last != cmd) {
                mqtt.publish("COMMAND", cmd)
                last = cmd
            }
        }


    }
}