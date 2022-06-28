package scorpion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage

class CommandVM : MqttListener {
    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)
    val queue = ArrayDeque<String>()
    init {
        DisplayScope.launch {
            mqtt.start()
        }

    }

    fun update() {
        println("update clicked")
        val store = mutableListOf<String>()
        store.addAll(state.list)

        store.add(System.currentTimeMillis().toString())

        setState { Data(store) }

    }

    var state: Data by mutableStateOf(initialState())
        private set

    private fun initialState(): Data {
        val store = mutableListOf<String>()
        store.add("ok")
        store.add("nono")

        return Data(store)
    }

    private inline fun setState(update: Data.() -> Data) {
        state = state.update()
    }


    data class Data(val list: MutableList<String>)

    override fun onConnected() {
        println("UX Connected to Broker")
        mqtt.subscribe("VOICE")
    }

    override fun connectionLost(cause: Throwable?) {

    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

        val m = message?.payload?.let { String(it) } ?: ""
        println("UX got $topic $m")
        if (topic == "VOICE") {
            queue.add(m)
        }

        val store = mutableListOf<String>()
        store.addAll(state.list)

        store.add(m)

        setState { Data(store) }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }


}

