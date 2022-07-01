
import kotlinx.coroutines.delay
import mqtt.MQTT
import scorpion.device.SonarData
import scorpion.mqtt.Command
import scorpion.mqtt.Topic

class Wander(private val mqtt: MQTT) {

    var enabled = false
    var sonarData = SonarData(0.0, 0.0, 0.0, 0.0)

    val pause = 250L


    var lastCommand = Command.STOP

    suspend fun explore() {
        if (enabled) {

            println("Exploring... ${lastCommand} ${sonarData.isClear()}")
            delay(100)
            if (sonarData.isClear() && lastCommand != Command.FORWARD) {
                mqtt.publish(Topic.COMMAND, Command.FORWARD)
                lastCommand = Command.FORWARD
            }
            delay(100)
            if (! sonarData.isClear() && lastCommand != Command.STOP) {
                 mqtt.publish(Topic.COMMAND, Command.STOP)
                 lastCommand = Command.STOP
            }
            delay(100)

            if (! sonarData.isClear() && lastCommand == Command.STOP) {
                lastCommand = Command.REVERSE
                mqtt.publish(Topic.COMMAND, Command.REVERSE)
                delay(1000)
                mqtt.publish(Topic.COMMAND, Command.STOP)
            }
            delay(100)
            if (! sonarData.isClear() && lastCommand == Command.REVERSE) {

                if (sonarData.l > sonarData.r) {
                    mqtt.publish(Topic.COMMAND, Command.LEFT)

                } else {
                    mqtt.publish(Topic.COMMAND, Command.RIGHT)
                }
                delay(1000)
                mqtt.publish(Topic.COMMAND, Command.STOP)
                lastCommand = Command.STOP

            }

        explore()
        }
    }


        fun update(sonarData: SonarData) {
//        println("sonar updated ${sonarData.isClear()}")
            this.sonarData = sonarData


        }
    }




