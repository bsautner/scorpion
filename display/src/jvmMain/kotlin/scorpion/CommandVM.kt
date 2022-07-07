package scorpion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.launch
import mqtt.MQTT
import mqtt.MqttListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import scorpion.device.SonarData
import scorpion.mqtt.Command
import scorpion.mqtt.Topic

class CommandVM : MqttListener {
    private val broker =  "tcp://10.0.0.79:1883"
    private val mqtt : MQTT = MQTT(this, broker)

    init {
        DisplayScope.launch {
            mqtt.start()
        }

    }

    fun onCommand(command: Command) {
        mqtt.publish(Topic.COMMAND, command)
    }


    var state: Data by mutableStateOf(initialState())
        private set

    var sonarData: SonarData by mutableStateOf(initSonar())
        private set

    var compass: Double by mutableStateOf(0.0)

    var lidar: JsonArray by mutableStateOf(initLidar())

    var status: String by mutableStateOf("")


    private fun initialState(): Data {
        val store = mutableListOf<String>("foo")


        return Data(store)
    }

    private fun initSonar(): SonarData {
        return SonarData(0.0, 0.0, 0.0, 0.0)
    }

    private fun initLidar(): JsonArray {

        return JsonArray(1)
    }
    private inline fun setState(update: Data.() -> Data) {
        state = state.update()
    }

    private inline fun setSonar(update: SonarData.() -> SonarData) {
        sonarData = sonarData.update()
    }

    private inline fun setCompass(update: Double.() -> Double) {
        compass = compass.update()
    }

    private inline fun setLidar(update: JsonArray.() -> JsonArray) {
        lidar = lidar.update()
    }

    private inline fun setStatus(update: String.() -> String) {
        status = status.update()
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

            Topic.MAG.name -> {
                setCompass {m.toDouble()}
            }

            Topic.LIDAR.name -> {
                val data = Gson().fromJson(m, JsonArray::class.java)
                setLidar { data }
                if (isObstructed(data, compass)) {
                    setStatus { "obstructed" }
                }
                else {
                    setStatus { "clear" }
                }

            }
        }
    }

    fun isObstructed(lidar: JsonArray, compass: Double) : Boolean {

        if (! sonarData.isClear()) {
            return false
        }
        lidar.forEach {
                val arr = it.asJsonArray
                if (arr[2].asInt < 200 && compass in arr[1].asDouble -10..arr[1].asDouble +10) {
                    return true
                }

        }

        return false
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }


}

