
import kotlinx.coroutines.delay
import mqtt.MQTT
import scorpion.device.Sonar

class Wander(private val mqtt: MQTT) {
    private val COMMAND = "COMMAND"
    enum class Command {
        STOP, SPIN, FORWARD, REVERSE
    }

    suspend fun start() {
//            mqtt.publish(COMMAND, Command.STOP.name)
            delay(100)
            while (Sonar.clear()) {
                mqtt.publish(COMMAND, Command.FORWARD.name)
                delay(100)
            }
            mqtt.publish(COMMAND, Command.STOP.name)
            delay(1000)
            mqtt.publish(COMMAND, Command.REVERSE.name)
            delay(250)
            mqtt.publish(COMMAND, Command.STOP.name)
            while (! Sonar.clear() ) {

                mqtt.publish(COMMAND, Command.SPIN.name)
                delay(100)
            }
            start()

    }


}