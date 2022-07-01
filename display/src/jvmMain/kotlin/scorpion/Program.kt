package scorpion

import Wander
import com.example.myapp.transcribestreaming.Transcribe
import com.google.gson.Gson
import kotlinx.coroutines.*
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.SonarData
import scorpion.mqtt.Command
import scorpion.mqtt.Topic
import scorpion.transcribestreaming.VoiceCommandListener
import kotlin.math.atan2

class Program : MqttListener, VoiceCommandListener {

    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)
     private val wander: Wander = Wander(mqtt)


    suspend fun start() {
        mqtt.start()

    }

    override fun onConnected() {
        println("MQTT Connected")
        Topic.values().forEach {
            mqtt.subscribe(it.name)
        }


//        DisplayScope.launch {
//            Transcribe(this@Program).start()
//        }

    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()

    }

    var explore : Job? = null

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val m = message?.payload?.let { String(it) } ?: ""

        val top = topic?.let { Topic.valueOf(it) }

        top?.let {
            when (it) {
                Topic.COMMAND -> {
                    println("Command Recieved: ${m}")
                    val cmd = m.let { Command.valueOf(m) }
                    cmd.let { command ->
                        when (command) {
                            Command.STOP -> {



                            }
                            Command.LEFT -> {}
                            Command.RIGHT -> {}
                            Command.FORWARD -> {}
                            Command.REVERSE -> {}
                            Command.ACK -> {}
                            Command.EXPLORE -> {
                                println("exploring!")
                                wander.enabled = ! wander.enabled

                                if (wander.enabled) {
                                    explore = DisplayScope.launch {
                                        wander.explore()
                                    }
                                } else {
                                    explore?.cancelChildren()
                                }

                            }
                        }
                    }

                }
                Topic.VOICE -> {

                }
                Topic.MAG -> {

                    val vals = Gson().fromJson(m, Array<Double>::class.java)

                    val x = vals[0]
                    val y = vals[1]
                    val z = vals[2]
                    var heading  =  atan2(y, x) * 180 / Math.PI

                }
                Topic.SONAR -> {

                    val sonarData = SonarData.fromJson(m)
                    wander.update(sonarData)


                }
                Topic.GPS -> {

                    val splt = m.split(",")
                    println(splt[8])

                }
            }
        }



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