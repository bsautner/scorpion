package scorpion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun CommandContent() {

    val commandVM = remember { CommandVM() }

    val state = commandVM.state
    val sonar = commandVM.sonarData

    MainScreen(state.list, sonar, commandVM::update, commandVM::onCommand)

}