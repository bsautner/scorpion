package scorpion.transcribe

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import javax.sound.sampled.*

class Transcribe( ) {



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




    }

    fun start() {
        val wavFile = File("/tmp/${UUID.randomUUID()}.wav")
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

    }

    data class VoiceResult(val text: String)


    companion object {
        private const val DURATION = 500L

    }
}