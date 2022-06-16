package scorpion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun RootContent() {

    val model = remember { RootStore() }

    val state = model.state

    MainContent(
        state.connected,
        model::update
    )

}