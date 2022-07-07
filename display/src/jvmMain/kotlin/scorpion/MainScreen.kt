package scorpion

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.plus
import androidx.compose.ui.unit.sp
import com.google.gson.JsonArray
import scorpion.device.SonarData
import scorpion.mqtt.Command
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

fun Double.round(): Double {
    return BigDecimal.valueOf(this).setScale(2, RoundingMode.FLOOR).toDouble()
}

@Composable
fun DistanceText(text: String) {

    Text(text, modifier = Modifier.padding(10.dp))
}

@Composable
fun MainScreen3(
    status: String,
    update: MutableList<String>,
    sonarData: SonarData,
    compass: Double,
    lidar: JsonArray,
    onCommand: (Command) -> Unit
) {

    Scaffold(
        topBar = { TopBar("") },
        bottomBar = { BottomBar(status, sonarData, compass) },
        content = {
            Box(
                Modifier
                    .background(Color(0XFFE3DAC9))
                    .padding(16.dp)
                    .fillMaxSize(),
            ) {
                CenterContent(update, sonarData, compass, lidar, onCommand)
            }
        }
    )

}


@Composable
fun CenterContent(
    update: MutableList<String>,
    sonarData: SonarData,
    compass: Double,
    lidar: JsonArray,
    onCommand: (Command) -> Unit
) {


    Column(Modifier.fillMaxSize().padding(0.dp)) {

        Column(Modifier.fillMaxSize().padding(5.dp)) {

            Row {
                Column(Modifier.fillMaxSize(.7F)) {

                    Display(sonarData, compass, lidar)

                }
                Row {
                    Controller(onCommand)
                }


            }
        }

    }


}

@Composable
fun Controller(onCommand: (Command) -> Unit) {

    Column {
        Row {
            ControllerButton("FL") { onCommand.invoke(Command.LEFT) }
            ControllerButton("F") { onCommand.invoke(Command.FORWARD) }
            ControllerButton("FR") { onCommand.invoke(Command.RIGHT) }
        }
        Row {
            ControllerButton("L") { onCommand.invoke(Command.LEFT) }
            ControllerButton("S") { onCommand.invoke(Command.STOP) }
            ControllerButton("R") { onCommand.invoke(Command.RIGHT) }
        }
        Row {
            ControllerButton("RL", {})
            ControllerButton("R") { onCommand.invoke(Command.REVERSE) }
            ControllerButton("RF", {})
        }
    }


}

@Composable
fun ControllerButton(text: String, onClick: () -> Unit) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        modifier = Modifier.padding(5.dp)
    ) {
        Text(text)
    }


}

@Composable
fun BottomBar(status: String, sonarData: SonarData, compass: Double) {
    BottomAppBar(backgroundColor = Color.LightGray) {
        Text("Status: $status  Heading: $compass   Sonar: F: ${sonarData.f.round()}cm L:${sonarData.l.round()}cm  R: ${sonarData.r.round()}cm ${sonarData.d.round()}cm ")
    }
}


@Composable
fun TopBar(title: String) {
    var expanded by remember { mutableStateOf(false) }
    val modifier = Modifier.size(width = 70.dp, height = 50.dp).background(Color.LightGray)

    TopAppBar(
        backgroundColor = Color.LightGray,
        title = {
            Text(
                text = title, fontSize = 16.sp,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "", tint = Color.DarkGray)
                    DropdownMenu(
                        modifier = Modifier.width(150.dp),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {

                        DropdownMenuItem(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Refresh, " Refresh")
                            Text("Refresh")
                        }

                    }
                }
            }
        },
        actions = {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.Face, contentDescription = "", tint = Color.DarkGray)
                }
            }
        },

        elevation = 1.dp
    )
}


@Composable
fun Display(sonarData: SonarData, compass: Double, lidar: JsonArray) {


    Canvas(modifier = Modifier.fillMaxSize()) {

        val north: Float = (360 - compass).toFloat()


        rotate(0F) {
            drawCircle(
                color = Color.Blue,
                center = center,
                radius = 100F,
                style = Stroke(1F)
            )
//            drawLine(Color.Blue, center, center.plus((Offset(0f, -100F))), 3f)
        }



        rotate(360 - compass.toFloat()) {
            drawLine(Color.Black, center, center.plus((Offset(0f, -100F))), 3f)


            val surroundings: MutableList<Offset> = ArrayList()
            val warnings: MutableList<Offset> = ArrayList()
            lidar.forEach {

                val list = it.asJsonArray
                val distance = list[2].asDouble
                val angle = list[1].asDouble
                val rad = angle * PI / 180
                val d = distance / 3

                val off = center.plus(IntOffset((d * cos(rad)).toInt(), (d * sin(rad)).toInt()))

                if (distance > 300) {
                    surroundings.add(off)
                } else {
                    warnings.add(off)
                }


            }
            drawPoints(surroundings, PointMode.Points, Color.Cyan, strokeWidth = 5f)
            drawPoints(warnings, PointMode.Points, Color.Red, strokeWidth = 5f)


            rotate(0F) {
                val A: MutableList<Offset> = ArrayList()
                val B: MutableList<Offset> = ArrayList()
                val C: MutableList<Offset> = ArrayList()
                val D: MutableList<Offset> = ArrayList()


                val d = 100

                A.add(center.plus(IntOffset((d * cos(0 * PI / 180)).toInt(), (d * sin(0 * PI / 180)).toInt())))
                B.add(center.plus(IntOffset((d * cos(90 * PI / 180)).toInt(), (d * sin(90 * PI / 180)).toInt())))
                C.add(center.plus(IntOffset((d * cos(180 * PI / 180)).toInt(), (d * sin(180 * PI / 180)).toInt())))
                D.add(center.plus(IntOffset((d * cos(270 * PI / 180)).toInt(), (d * sin(270 * PI / 180)).toInt())))





                drawPoints(A, PointMode.Points, Color.Red, strokeWidth = 5f)
                drawPoints(B, PointMode.Points, Color.Blue, strokeWidth = 5f)
                drawPoints(C, PointMode.Points, Color.Black, strokeWidth = 5f)
                drawPoints(D, PointMode.Points, Color.Green, strokeWidth = 5f)
            }
        }
    }
}


//
//
//            drawCircle(color = color, radius = 10f, center = center)
//            rotate(0F, center) {
//                drawLine(
//                    color = Color.Blue,
//                    start = this.center,
//                    end = this.center.plus(IntOffset(0, (((sonarData.f + 20) * -1).roundToInt())))
//                )
//            }
//            rotate(45F, center) {
//                drawLine(
//                    color = Color.Blue,
//                    start = this.center,
//                    end = this.center.plus(IntOffset(0, (((sonarData.r + 20) * -1).roundToInt())))
//                )
//            }
//            rotate(-45F, center) {
//                drawLine(
//                    color = Color.Blue,
//                    start = this.center,
//                    end = this.center.plus(IntOffset(0, (((sonarData.l + 20) * -1).roundToInt())))
//                )
//            }
//
//            rotate(90F, center) {
//                drawLine(
//                    color = Color.Black,
//                    start = this.center.plus(Offset(-100f, ((sonarData.d * -1).toFloat()))),
//                    end = this.center.plus(Offset(-100f, +((sonarData.d).toFloat())))
//                )
//            }
//        }



@Preview
@Composable
fun MainScreen(
    update: MutableList<String>,
    sonarData: SonarData,
    compass: Double,
    onSimClick: () -> Unit,
    onCommand: (Command) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    Column(modifier = Modifier.defaultMinSize(1024.dp, 768.dp)) {
        Row {

            TopAppBar(modifier = Modifier.padding(0.dp).height(40.dp), backgroundColor = Color.LightGray) {
                Button(
                    modifier = Modifier.padding(0.dp).fillMaxHeight(),
                    onClick = { expanded = !expanded },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
                ) {
                    Icon(Icons.Filled.MoreVert, " Refresh")
                }

                DropdownMenu(
                    modifier = Modifier.width(150.dp),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {

                    DropdownMenuItem(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Refresh, " Refresh")
                        Text("Refresh")
                    }

                }
            }
        }
        Row {


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

                }
            }

            Column {

                Row {
                    MessageList(update)
                }

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
