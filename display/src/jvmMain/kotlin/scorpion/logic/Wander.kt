
import kotlinx.coroutines.delay
import mqtt.MQTT
import scorpion.device.Sonar

class Wander(private val mqtt: MQTT) {
    private val COMMAND = "COMMAND"
    private var stopped = false
    enum class Command {
        STOP, LEFT, RIGHT, FORWARD, REVERSE
    }

    suspend fun start() {
         if (! stopped) {
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
            while (!Sonar.clear()) {
                if (Sonar.getDistance(Sonar.LEFT_FRONT_SONAR) > Sonar.getDistance(Sonar.RIGHT_FRONT_SONAR)) {
                    mqtt.publish(COMMAND, Command.LEFT.name)
                } else {
                    mqtt.publish(COMMAND, Command.RIGHT.name)
                }
                delay(100)
            }
            start()
        }

    }

    fun stop() {
        this.stopped = true
    }

    fun reset() {
        this.stopped = false
    }

}