
import mqtt.MQTT
import scorpion.device.Sonar

class Wander(private val mqtt: MQTT, private val sonar: Sonar) {

    suspend fun start() {
        println(sonar.isClear())

//            delay(100)
//            while (S.clear()) {
//                mqtt.publish(Topic.COMMAND, Command.FORWARD)
//                delay(100)
//            }
//            mqtt.publish(Topic.COMMAND, Command.STOP)
//            delay(1000)
//            mqtt.publish(Topic.COMMAND, Command.REVERSE)
//            delay(250)
//            mqtt.publish(Topic.COMMAND, Command.STOP)
//            while (!S.clear()) {
//                if (S.getDistance(S.LEFT_FRONT_SONAR) > S.getDistance(S.RIGHT_FRONT_SONAR)) {
//                    mqtt.publish(Topic.COMMAND, Command.LEFT)
//                } else {
//                    mqtt.publish(Topic.COMMAND, Command.RIGHT)
//                }
//                delay(100)
//            }
//            start()
       }

    }

