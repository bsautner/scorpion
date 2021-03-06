package scorpion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun CommandContent() {

    val commandVM = remember { CommandVM() }

    val state = commandVM.state
    val sonar = commandVM.sonarData
    val compass = commandVM.compass
    val lidar = commandVM.lidar
    var status = commandVM.status

    MainScreen3(status, state.list, sonar, compass, lidar, commandVM::onCommand)

}