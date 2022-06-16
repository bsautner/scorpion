package scorpion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.Sonar

class RootStore: MqttListener {

    private val broker =  "tcp://10.0.0.205:1883"
    private val mqtt : MQTT = MQTT(this, broker)

    init {
        GlobalScope.launch {
            mqtt.start()
        }
    }


    var state: RootState by mutableStateOf(initialState())
        private set

    private fun initialState(): RootState {
        return RootState(false)
    }

    fun update() {

//        setState{
//            RootState(System.currentTimeMillis().toString())
//        }
    }

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }


    data class RootState(val connected: Boolean)

    override fun onConnected() {
        println("MQTT Connected")
        mqtt.subscribe("sonar")
        setState {
            RootState(true)
        }
    }

    override fun connectionLost(cause: Throwable?) {
        cause?.printStackTrace()
        setState {
            RootState(true)
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
         Sonar.feed(message?.let { String(it.payload) }.toString())
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
     }
}