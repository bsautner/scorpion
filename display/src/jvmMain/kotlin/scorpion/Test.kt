package scorpion

import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.FileInputStream
import javax.sound.sampled.AudioSystem

class Test {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            println("test")
            LibVosk.setLogLevel(LogLevel.DEBUG)

            Model("/home/ben/code/vosk-api/big_model").use { model ->

                AudioSystem.getAudioInputStream(BufferedInputStream(FileInputStream("/tmp/RecordAudio.wav")))
                    .use { ais ->
                        Recognizer(model, 16000f).use { recognizer ->
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
    }
}