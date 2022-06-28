package scorpion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.Sonar
import scorpion.mqtt.Topic

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
        setSonar { Sonar(500.0, 500.0, 500.0, 500.0) }

    }

    var state: Data by mutableStateOf(initialState())
        private set

    var sonar: Sonar by mutableStateOf(initSonar())
        private set



    private fun initialState(): Data {
        val store = mutableListOf<String>()


        return Data(store)
    }

    private fun initSonar(): Sonar {
        return Sonar(0.0, 0.0, 0.0, 0.0)
    }

    private inline fun setState(update: Data.() -> Data) {
        state = state.update()
    }

    private inline fun setSonar(update: Sonar.() -> Sonar) {
        sonar = sonar.update()
    }


    data class Data(val list: MutableList<String>)


    override fun onConnected() {
        println("UX Connected to Broker")
        Topic.values().forEach {
            mqtt.subscribe(it.name)
        }
    }

    override fun connectionLost(cause: Throwable?) {

    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

        val m = message?.payload?.let { String(it) } ?: ""


        when (topic) {
            Topic.VOICE.name -> {
                val store = mutableListOf<String>()
                store.addAll(state.list)

                store.add(m)

                setState { Data(store) }
            }
            Topic.SONAR.name -> {
                val f1 = m.trimStart('\"').trimEnd('\"').replace("\\", "")
                val v  = Gson().fromJson(f1, Sonar::class.java)
                println(v.f)
                setSonar { v }

            }
        }




    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }


}

