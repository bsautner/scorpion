// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import scorpion.*

@Composable
@Preview
fun App() {

    MaterialTheme {
        CommandContent()
    }
}

fun main() = application {


    DisplayScope.launch {
        Program().start()
    }

    Window(title = "test", state = WindowState(width = 1024.dp, height = 800.dp), onCloseRequest = ::exitApplication) {
        App()
    }
}


