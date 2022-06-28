package scorpion

import Wander
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapp.transcribestreaming.Transcribe
import com.google.gson.Gson
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.Sonar
import scorpion.mqtt.Topic
import scorpion.transcribestreaming.VoiceCommandListener
import scorpion.voice.Polly
import java.lang.Math.PI
import java.lang.Math.atan2

class RootStore: MqttListener, VoiceCommandListener {

    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)
    val polly: Polly = Polly()
    val wander = Wander(mqtt)

    init {
        DisplayScope.launch {
            mqtt.start()
        }

    }


    var state: RootState by mutableStateOf(initialState())
        private set

    private fun initialState(): RootState {
        return RootState(false, "connecting...", Sonar, System.currentTimeMillis().toString())
    }

    fun update() {

        setState{
            RootState(state.connected, state.status, Sonar, System.currentTimeMillis().toString())
        }
    }

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }


    data class RootState(val connected: Boolean, val status: String, val sonar: Sonar, val s : String)


    override fun onConnected() {
        println("MQTT Connected")
        mqtt.subscribe("sonar")
        mqtt.subscribe("MAG")
        DisplayScope.launch {
//            polly.speak("Connected!")
        }
        DisplayScope.launch {
            Transcribe(this@RootStore).start()
        }

        setState {
            RootState(true, "connected to mqtt broker", Sonar, System.currentTimeMillis().toString())
        }
    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()
        setState {
            cause?.message?.let { RootState(false, it, Sonar, System.currentTimeMillis().toString()) }!!
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
         val m = message?.payload?.let { String(it) } ?: ""
//         println("$topic $m")


        if (topic == "sonar") {
            Sonar.feed(message?.let { String(it.payload) }.toString())
        }

        if (topic == "MAG") {
            message?.let {
                val p = String(it.payload)
                val vals = Gson().fromJson(p, Array<Double>::class.java)

                val x = vals[0]
                val y = vals[1]
                val z = vals[2]
                var heading  =  kotlin.math.atan2(y, x) * 180 / PI


                println(heading)


            }
        }


//

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
     }

    override fun onVoiceCommand(cmd: String) {

    }


}