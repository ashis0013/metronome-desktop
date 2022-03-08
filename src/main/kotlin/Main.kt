// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import utils.Player
import kotlin.math.ceil
import kotlin.math.round

@Composable
@Preview
fun App() {
    val player = Player()

    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().background(Color(0xff161616))
        ) {
            textWithButtonPairs(
                "${player.bpmMutable} bpm",
                48.sp,
                FontFamily(Font(resource = "font/TickingTimebombBB.ttf")),
                FontWeight.Bold,
                Color(0xffededed),
                onUp = { player.bpm += 1 },
                onDown = { player.bpm -= 1 }
            )
            Spacer(Modifier.height(10.dp))
            if (player.isPolyMode) textWithButtonPairs(
                "${player.beatPrimaryMut} : ${player.beatSecondaryMut}",
                fontSize = 32.sp,
                color = Color(0xffededed),
                onUp = { player.beatSecondary += 1 },
                onDown = { player.beatSecondary -= 1 },
                isPolyMode = true,
                onUpLeft = { player.beatPrimary += 1 },
                onDownLeft = { player.beatPrimary -= 1 }
            ) else textWithButtonPairs(
                "${player.beatPrimaryMut} / 4",
                fontSize = 32.sp,
                color = Color(0xffededed),
                onUp = { player.beatPrimary += 1 },
                onDown = { player.beatPrimary -= 1 }
            )
            Spacer(Modifier.height(16.dp))
            Slider(
                player.bpmRatio(),
                modifier = Modifier.width(240.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xffda0037),
                    activeTrackColor = Color(0xffda0037),
                    inactiveTrackColor = Color(0xff444444)
                ),
                onValueChange = {
                    player.bpm = ceil(it.toDouble() * 280).toInt() + 20
                }
            )
            Spacer(Modifier.height(16.dp))
            button(player)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                player.isPolyMode = !player.isPolyMode
            }, colors = ButtonDefaults.textButtonColors(
                    backgroundColor = Color(0xff444444),
                contentColor = Color(0xffededed)
            )) {
                Text(if (player.isPolyMode) "Switch back to normal" else "Switch to Polyrhythms")
            }
        }
    }
}

@Composable
fun textWithButtonPairs(
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily? = null,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    onUp: () -> Unit,
    onDown: () -> Unit,
    isPolyMode: Boolean = false,
    onUpLeft: () -> Unit = {},
    onDownLeft: () -> Unit ={}
) {
    Row {
        if (isPolyMode) {
            buttonPair(onUpLeft, onDownLeft)
            Spacer(Modifier.width(16.dp))
        }
        Text(
            text,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            color = color
        )
        Spacer(Modifier.width(16.dp))
        buttonPair(onUp, onDown)
    }
}

@Composable
fun button(player: Player) {
    Row {
        Button(onClick = {
            player.toggle()
        }, colors = ButtonDefaults.buttonColors(Color(0xffda0037))) {
            Text(player.buttonText)
        }
    }
}

@Composable
fun buttonPair(onUp: () -> Unit, onDown: () -> Unit) {
    Column {
        buttonPairSingle(onUp)
        Spacer(Modifier.height(4.dp))
        buttonPairSingle(onDown)
    }
}

@Composable
fun buttonPairSingle(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        Modifier.height(20.dp).width(48.dp),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = Color(0xff444444),
            contentColor = Color(0xffededed)
        )
    ) { Icon(Icons.Filled.KeyboardArrowUp, "up") }
}

fun main() = application {
    Window(title = "Metronome",  onCloseRequest = ::exitApplication) {
        App()
    }
}
