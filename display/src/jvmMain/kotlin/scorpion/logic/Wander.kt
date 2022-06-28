
import kotlinx.coroutines.delay
import mqtt.MQTT
import scorpion.device.Sonar
import scorpion.mqtt.Command
import scorpion.mqtt.Topic

class Wander(private val mqtt: MQTT) {

    suspend fun start() {

            delay(100)
            while (Sonar.clear()) {
                mqtt.publish(Topic.COMMAND, Command.FORWARD)
                delay(100)
            }
            mqtt.publish(Topic.COMMAND, Command.STOP)
            delay(1000)
            mqtt.publish(Topic.COMMAND, Command.REVERSE)
            delay(250)
            mqtt.publish(Topic.COMMAND, Command.STOP)
            while (!Sonar.clear()) {
                if (Sonar.getDistance(Sonar.LEFT_FRONT_SONAR) > Sonar.getDistance(Sonar.RIGHT_FRONT_SONAR)) {
                    mqtt.publish(Topic.COMMAND, Command.LEFT)
                } else {
                    mqtt.publish(Topic.COMMAND, Command.RIGHT)
                }
                delay(100)
            }
            start()
        }

    }

