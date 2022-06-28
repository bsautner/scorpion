package scorpion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import scorpion.device.Sonar

@Composable
fun MainScreen(update: MutableList<String>, kFunction0: () -> Unit) {


    Row(modifier = Modifier.defaultMinSize(1024.dp, 768.dp)) {

       Column(modifier = Modifier.width(800.dp)) {
           Button(onClick = {

               Sonar.feed("${Sonar.RIGHT_FRONT_SONAR},100.0")
               Sonar.feed("${Sonar.LEFT_FRONT_SONAR},100.0")
               Sonar.feed("${Sonar.FRONT_SONAR},100.0")
               Sonar.feed("${Sonar.DOWN_FRONT_SONAR},50.0")


           }) {
               Text("SIM")
           }
        }
        Column {

            Row {
                MessageList(update)
            }

        }


    }
}



@Composable
fun MessageList(messages: List<String>) {
    val r = messages.asReversed()
    LazyColumn() {
        items(r.size) { message ->
            Text(r[message])
        }
    }
}
