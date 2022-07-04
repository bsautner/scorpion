package scorpion

import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.sound.sampled.*


/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
class JavaSoundRecorder {
    // path of the wav file
    var wavFile = File("/tmp/RecordAudio.wav")

    // format of audio file
    var fileType = AudioFileFormat.Type.WAVE

    // the line from which audio data is captured
    var line: TargetDataLine? = null

    /**
     * Defines an audio format
     */
    val audioFormat: AudioFormat
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

    /**
     * Captures the sound and record into a WAV file
     */
    fun start() {
        try {
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
            println("Start capturing...")
            val ais = AudioInputStream(line)
            println("Start recording...")

            // start recording
            AudioSystem.write(ais, fileType, wavFile)
        } catch (ex: LineUnavailableException) {
            ex.printStackTrace()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    fun finish() {
        line!!.stop()
        line!!.close()
        println("Finished ${wavFile.absolutePath}")

        Thread.sleep(1000)
        LibVosk.setLogLevel(LogLevel.DEBUG)

        Model("/home/ben/code/vosk-api/model").use { model ->

            AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream(wavFile.absolutePath)))
                .use { ais ->
                    Recognizer(model, 48000F).use { recognizer ->
                        var nbytes: Int
                        val b = ByteArray(4096)
                        while (ais.read(b).also { nbytes = it } >= 0) {
                            if (recognizer.acceptWaveForm(b, nbytes)) {
                                println(recognizer.result)
                            } else {
                                println(recognizer.partialResult)
                            }
                        }
                        println(recognizer.finalResult)
                    }
                }
        }
    }

    companion object {
        // record duration, in milliseconds
        const val RECORD_TIME: Long = 3000

        /**
         * Entry to run the program
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val recorder = JavaSoundRecorder()

            // creates a new thread that waits for a specified
            // of time before stopping
            val stopper = Thread {
                try {
                    Thread.sleep(RECORD_TIME)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
                recorder.finish()
            }
            stopper.start()

            // start recording
            recorder.start()
        }
    }
}