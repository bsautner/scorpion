package scorpion

import kotlinx.coroutines.launch
import mqtt.MQTT

class Program {


    private val broker =  "tcp://10.0.0.205:1883"
    private val eventListener = EventListener()
    private val mqtt : MQTT = MQTT(eventListener, broker)


    fun start() {
        println("Hello Kotlin")
        DeviceScope.launch {
            mqtt.start()
        }
        while (true) {
           Thread.sleep(10)
        }
    }
}