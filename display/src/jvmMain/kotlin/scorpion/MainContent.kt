package scorpion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.plus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import scorpion.device.Sonar
import kotlin.math.roundToInt

@DelicateCoroutinesApi
@Composable
fun MainContent(mqttConnected: Boolean,
                status: String,
                sonar: Sonar,
                s: String,
                update: () -> Unit) {

    var center : Offset = Offset(0f, 0f)

    Column {
        Row {
            Column {
                Button(onClick = {
                    GlobalScope.launch {
                        Runner(update).updateClock()
                    }
                }) {
                    Text("GO")
                }
            }
            Column { Text(s) }
          Column {
              Text(status)
          }
        }
        Row {
            Canvas(modifier = Modifier.fillMaxSize()) {
            rotate(0F) {
                center = this.center
                val c : Color
                if (mqttConnected) {
                    c = Color.Blue
                } else {
                    c = Color.Red
                }
                drawCircle(color = c, radius = 40f, center = center)


            }
                rotate(0F, center) {
                    drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.getDistance(Sonar.FRONT_SONAR) +20) * -1).roundToInt()))))
                }
                rotate(45F, center) {
                    drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.getDistance(Sonar.RIGHT_FRONT_SONAR)+20) * -1).roundToInt()))))
                }
                rotate(-45F, center) {
                    drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonar.getDistance(Sonar.LEFT_FRONT_SONAR) +20) * -1).roundToInt()))))
                }
        }

        }
    }


//    Column {
//        Row {
//            Text(s)
//        }
//        Button(onClick = {
//            GlobalScope.launch {
//                Runner(update).updateClock()
//            }
//
//        }) {
//
//            Text("click")
//        }
//        Row {
//            Canvas(modifier = Modifier.fillMaxSize()) {

//
//                rotate(90F, this.center.plus(IntOffset(0, -20))) {
//                    drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, ((Sonar.getDistance(Sonar.DOWN_FRONT_SONAR) * -1).roundToInt()))))
//                }
//                drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, ((Sonar.getDistance(Sonar.FRONT_SONAR) * -1).roundToInt()))))
//                drawCircle(color = Color.Red, radius = 10f, center = this.center)
//            }
//        }
//    }




}

class Runner(val update: () -> Unit) {
    suspend fun updateClock() {
        while(true) {
            this.update.invoke()
        }
    }
}