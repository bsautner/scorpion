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
import scorpion.device.SonarData
import scorpion.mqtt.Command
import scorpion.mqtt.Topic

class CommandVM : MqttListener {
    private val broker =  "tcp://scorpion:1883"
    private val mqtt : MQTT = MQTT(this, broker)

    init {
        DisplayScope.launch {
            mqtt.start()
        }

    }

    fun onCommand(command: Command) {
        mqtt.publish(Topic.COMMAND, command)
    }

    fun update() {
        val store = mutableListOf<String>()
        store.addAll(state.list)

        store.add(System.currentTimeMillis().toString())

        setState { Data(store) }
        setSonar { SonarData(500.0, 500.0, 500.0, 500.0) }

    }

    var state: Data by mutableStateOf(initialState())
        private set

    var sonarData: SonarData by mutableStateOf(initSonar())
        private set



    private fun initialState(): Data {
        val store = mutableListOf<String>("foo")


        return Data(store)
    }

    private fun initSonar(): SonarData {
        return SonarData(0.0, 0.0, 0.0, 0.0)
    }

    private inline fun setState(update: Data.() -> Data) {
        state = state.update()
    }

    private inline fun setSonar(update: SonarData.() -> SonarData) {
        sonarData = sonarData.update()
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


                setSonar { SonarData.fromJson(m) }

            }
        }




    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }


}

