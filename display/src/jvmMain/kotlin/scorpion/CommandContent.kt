package scorpion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun CommandContent() {

    val commandVM = remember { CommandVM() }
    val state = commandVM.state

    MainScreen(state.list, commandVM::update)

}