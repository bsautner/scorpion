package scorpion.transcribe

import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mqtt.MQTT
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import scorpion.TranscribeScope
import scorpion.mqtt.Topic
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.UUID
import javax.sound.sampled.*

class Transcribe(val mqtt: MQTT) {



    // format of audio file
    var fileType = AudioFileFormat.Type.WAVE

    // the line from which audio data is captured
    var line: TargetDataLine? = null

    /**
     * Defines an audio format
     */
    private val audioFormat: AudioFormat
        get() {
            val sampleRate = 48000F
            val sampleSizeInBits = 16
            val channels = 1
            val signed = true
            val bigEndian = false
            return AudioFormat(
                sampleRate, sampleSizeInBits,
                channels, signed, bigEndian
            )
        }



    private suspend fun stopper(wavFile: File) {

        delay(DURATION)
        line!!.stop()
        line!!.close()

        LibVosk.setLogLevel(LogLevel.WARNINGS)

        start()
        Model("/home/ben/voice_model").use { model ->

            AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(wavFile.absolutePath)))
                .use { ais ->
                    Recognizer(model, 48000F).use { recognizer ->
                        var nbytes: Int
                        val b = ByteArray(4096)
                        while (ais.read(b).also { nbytes = it } >= 0) {
                            if (recognizer.acceptWaveForm(b, nbytes)) {
//                                println(recognizer.result)
                            } else {
//                                println(recognizer.partialResult)
                            }
                        }
                        val r = (recognizer.finalResult)
                        val result :VoiceResult = Gson().fromJson(r, VoiceResult::class.java)
                        if (result.text.isNotEmpty()) {
                            println(result.text)
                            mqtt.publish(Topic.VOICE, result.text)
                        }
                        wavFile.delete()
                    }
                }
        }


    }

    fun start() {
        var wavFile = File("/tmp/${UUID.randomUUID()}.wav")
        try {
            TranscribeScope.launch {
                stopper(wavFile)
            }
            val format = audioFormat
            val info = DataLine.Info(TargetDataLine::class.java, format)

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                println("Line not supported")
                System.exit(0)
            }
            line = AudioSystem.getLine(info) as TargetDataLine
            line!!.open(format)
            line!!.start() // start capturing


            val ais = AudioInputStream(line)


            // start recording
            AudioSystem.write(ais, fileType, wavFile)

        } catch (ex: LineUnavailableException) {
            ex.printStackTrace()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
//    println("Recording")
//    delay(DURATION)



    }

    data class VoiceResult(val text: String)


    companion object {
        private const val DURATION = 1000L

    }
}