// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import scorpion.CommandContent
import scorpion.transcribe.Transcribe
import kotlin.concurrent.thread

@Composable
@Preview
fun App() {

    MaterialTheme {
        CommandContent()
    }
}

fun main() = application {


    val transcribe = Transcribe()

    thread(start = true, isDaemon = true) {
        println("I'm a thread ${Thread.currentThread().name}")
        transcribe.start()
    }
    Window(title = "Scorpion", state = WindowState(width = 1024.dp, height = 800.dp), onCloseRequest = ::exitApplication) {
        App()
    }
}


