package scorpion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import scorpion.device.Sonar

@Composable
fun RootContent() {

    val model = remember { RootStore() }

    val state = model.state

    MainContent(
        state.connected,
        state.status,
        state.sonar,
        state.s,
        model::update
    )

}