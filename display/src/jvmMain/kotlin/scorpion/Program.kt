package scorpion

import Wander
import com.example.myapp.transcribestreaming.Transcribe
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.mqtt.Command
import scorpion.mqtt.Topic
import scorpion.transcribestreaming.VoiceCommandListener

class Program : MqttListener, VoiceCommandListener {

    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)
//    private val wander: Wander = Wander(mqtt)


    suspend fun start() {
        mqtt.start()

    }

    override fun onConnected() {
        println("MQTT Connected")
        Topic.values().forEach {
            mqtt.subscribe(it.name)
        }


        DisplayScope.launch {
            Transcribe(this@Program).start()
        }

    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()

    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val m = message?.payload?.let { String(it) } ?: ""
//        println("$topic $m")
        val t = topic?.let { Topic.valueOf(it) }

        t?.let {
            if (t == Topic.SONAR) {
//                S.feed(message?.let { String(it.payload) }.toString())
            }

            if (t == Topic.MAG) {
                message?.let {
                    val p = String(it.payload)
                    val vals = Gson().fromJson(p, Array<Double>::class.java)

                    val x = vals[0]
                    val y = vals[1]
                    val z = vals[2]
                    var heading  =  kotlin.math.atan2(y, x) * 180 / Math.PI


                    println(heading)


                }
            }

        }



//

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    var job: Job? = null
    override fun onVoiceCommand(cmd: String) {


        if (cmd.lowercase().contains("scorpion")) {
            mqtt.publish(Topic.COMMAND, Command.ACK)
            mqtt.publish(Topic.VOICE, Command.ACK)
        }
        if (cmd.lowercase().contains("go play")) {
            mqtt.publish(Topic.COMMAND, Command.ACK)
            mqtt.publish(Topic.VOICE, Command.ACK)
            job?.cancel()

                job = DisplayScope.launch {
//                    wander.start()
                }

        }
        if (cmd.lowercase().contains("stop")) {
            job?.let {
                it.cancel()
            }
            mqtt.publish(Topic.VOICE, Command.STOP)
            mqtt.publish(Topic.COMMAND, Command.STOP)

        }
    }

}