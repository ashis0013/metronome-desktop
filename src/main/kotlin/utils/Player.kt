package utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.sound.sampled.AudioSystem

class Player {
    private val tick = File("src/main/resources/sound/MetronomeUp.wav").absoluteFile
    private val tock = File("src/main/resources/sound/Metronome.wav").absoluteFile
    var bpm = 80
        set(value) {
            field = when {
                value < 20 -> 20
                value > 300 -> 300
                else -> value
            }
            bpmMutable = bpm
        }
    var beatPrimary = 4
        set(value) {
            field = when {
                value < 2 -> 2
                else -> value
            }
            beatPrimaryMut = beatPrimary
        }
    var beatSecondary = 4
        set(value) {
            field = when {
                value < 2 -> 2
                else -> value
            }
            beatSecondaryMut= beatSecondary
        }

    private var isPrimaryRunning = false
    private var isSecondaryRunning = false
    var buttonText by mutableStateOf("Start")
    var bpmMutable by mutableStateOf(bpm)
    var beatPrimaryMut by mutableStateOf(beatPrimary)
    var beatSecondaryMut by mutableStateOf(beatSecondary)
    var isPolyMode by mutableStateOf(false)

    fun bpmRatio() = (bpm - 20).toFloat() / 280.0f


    private fun loop() {
        if (isPrimaryRunning) return
        isPrimaryRunning = true
        buttonText = "Stop"
        playLoop(beatPrimary, 60000L / bpm)
        if (isSecondaryRunning || !isPolyMode) return
        isSecondaryRunning = true
        playLoop(beatSecondary, 60000L * beatPrimary / (bpm * beatSecondary))
    }

    private fun playLoop(beat: Int, delay: Long) {
        var tickCount = 0
        CoroutineScope(Dispatchers.Main).launch {
            while (isPrimaryRunning || isSecondaryRunning) {
                AudioSystem.getClip().apply {
                    open(AudioSystem.getAudioInputStream(if (tickCount == 0) tick else tock))
                    start()
                }
                tickCount = (tickCount + 1) % beat
                delay(delay)
            }
        }
    }

    private fun stop() {
        isPrimaryRunning = false
        isSecondaryRunning = false
        buttonText = "Start"
    }

    fun toggle() {
        if (isPrimaryRunning) stop() else loop()
    }
}