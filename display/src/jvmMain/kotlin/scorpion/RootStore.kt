package scorpion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapp.transcribestreaming.Transcribe
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.Sonar
import scorpion.transcribestreaming.VoiceCommandListener

class RootStore: MqttListener, VoiceCommandListener {

    private val broker =  "tcp://10.0.0.205:1883"
    private val mqtt : MQTT = MQTT(this, broker)

    init {
        DisplayScope.launch {
            mqtt.start()
        }

    }


    var state: RootState by mutableStateOf(initialState())
        private set

    private fun initialState(): RootState {
        return RootState(false, "connecting...")
    }

    fun update() {

//        setState{
//            RootState(System.currentTimeMillis().toString())
//        }
    }

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }


    data class RootState(val connected: Boolean, val status: String)


    override fun onConnected() {
        println("MQTT Connected")
        mqtt.subscribe("sonar")
        DisplayScope.launch {
            Transcribe(this@RootStore).start()
        }

        setState {
            RootState(true, "connected to mqtt broker")
        }
    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()
        setState {
            cause?.message?.let { RootState(false, it) }!!
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
         Sonar.feed(message?.let { String(it.payload) }.toString())
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
     }

    override fun onVoiceCommand(cmd: String) {
        println(cmd)
        setState {
            RootState(true, cmd)
        }
    }
}