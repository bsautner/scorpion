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
import scorpion.device.SonarData
import scorpion.mqtt.Command
import kotlin.math.roundToInt

@Composable
fun DistanceText(text: String) {

    Text(text, modifier = Modifier.padding(10.dp))
}


@Composable
fun MainScreen(update: MutableList<String>, sonarData: SonarData, onSimClick: () -> Unit, onCommand: (Command) -> Unit) {


    Row(modifier = Modifier.defaultMinSize(1024.dp, 768.dp)) {

       Column(modifier = Modifier.width(800.dp)) {
           Row {


               Button(onClick = {

                   onCommand.invoke(Command.EXPLORE)


               }) {
                   Text("SIM")
               }

               Button(onClick = {

                   onCommand.invoke(Command.FORWARD)


               }) {
                   Text("F")
               }

               Button(onClick = {

                   onCommand.invoke(Command.STOP)


               }) {
                   Text("S")
               }

               Button(onClick = {

                   onCommand.invoke(Command.REVERSE)


               }) {
                   Text("REV")
               }
               Button(onClick = {

                   onCommand.invoke(Command.RIGHT)


               }) {
                   Text("R")
               }
               Button(onClick = {

                   onCommand.invoke(Command.LEFT)


               }) {
                   Text("L")
               }



           }
           Row {
               DistanceText(sonarData.f.roundToInt().toString())
               DistanceText(sonarData.l.roundToInt().toString())
               DistanceText(sonarData.r.roundToInt().toString())
               DistanceText(sonarData.d.roundToInt().toString())
           }
           Row {
               Canvas(modifier = Modifier.width(800.dp).height(800.dp)) {

                   val color : Color = if (sonarData.isClear()) {
                       Color.Blue
                   } else {
                       Color.Red
                   }


                   drawCircle(color = color, radius = 10f, center = center)
                   rotate(0F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonarData.f +20) * -1).roundToInt()))))
                   }
                   rotate(45F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonarData.r +20) * -1).roundToInt()))))
                   }
                   rotate(-45F, center) {
                       drawLine(color = Color.Blue, start = this.center, end = this.center.plus(IntOffset(0, (((sonarData.l +20) * -1).roundToInt()))))
                   }

                   rotate(90F, center) {
                       drawLine(color = Color.Black, start = this.center.plus(Offset(-100f, ((sonarData.d * -1).toFloat()))), end = this.center.plus(Offset(-100f, +((sonarData.d).toFloat()))))
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
    LazyColumn {
        items(r.size) { message ->
            Text(r[message])
        }
    }
}
