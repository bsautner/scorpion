package scorpion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.plus
 import scorpion.device.Sonar
import kotlin.math.roundToInt

@Composable
fun MainScreen(update: MutableList<String> , sonar: Sonar,  onSimClick: () -> Unit) {


    Row(modifier = Modifier.defaultMinSize(1024.dp, 768.dp)) {

       Column(modifier = Modifier.width(800.dp)) {
           Button(onClick = {

               onSimClick.invoke()


           }) {
               Text("SIM")
           }
           Row {
               Canvas(modifier = Modifier.fillMaxSize()) {
                   drawCircle(color = Color.Red, radius = 40f, center = center)
                   rotate(0F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.f +20) * -1).roundToInt()))))
                   }
                   rotate(45F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.r +20) * -1).roundToInt()))))
                   }
                   rotate(-45F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.l +20) * -1).roundToInt()))))
                   }

                   rotate(90F, center) {
                       drawLine(color = Color.Black, start = this.center.plus(Offset(-100f, ((sonar.d * -1).toFloat()))), end = this.center.plus(Offset(-100f, +((sonar.d).toFloat()))))
                   }
               }
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
