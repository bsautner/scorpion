package scorpion

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient
import software.amazon.awssdk.services.transcribestreaming.model.*
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import javax.sound.sampled.*

class Transcribe(private val listener: VoiceCommandListener) {

    private val REGION = Region.US_EAST_1
    private lateinit var client: TranscribeStreamingAsyncClient

    fun start() {
        println("Starting listing for commands")
         client = TranscribeStreamingAsyncClient.builder()
            .credentialsProvider(getCredentials())
            .region(REGION)
            .build()

        val result = client.startStreamTranscription(
            getRequest(16000),
            AudioStreamPublisher(getStreamFromMic()),
            getResponseHandler()
        )

        result.get()
        client.close()
    }

    @Throws(LineUnavailableException::class)
    private fun getStreamFromMic(): InputStream {

        // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
        val sampleRate = 16000
        val format = AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        if (!AudioSystem.isLineSupported(info)) {
            println("Line not supported")
            System.exit(0)
        }
        val line = AudioSystem.getLine(info) as TargetDataLine
        line.open(format)
        line.start()
        return AudioInputStream(line)
    }

    private fun getResponseHandler(): StartStreamTranscriptionResponseHandler? {
        return StartStreamTranscriptionResponseHandler.builder()
            .onResponse { r: StartStreamTranscriptionResponse? ->
                println(
                    "Received Initial response"
                )
            }
            .onError { e: Throwable ->
                println(e.message)
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                println("Error Occurred: $sw")
            }
            .onComplete { println("=== All records stream successfully ===") }
            .subscriber { event: TranscriptResultStream ->
                val results =
                    (event as TranscriptEvent).transcript().results()
                if (results.size > 0) {
                    if (!results[0].alternatives()[0].transcript().isEmpty()) {
                        listener.onVoiceCommand(results[0].alternatives()[0].transcript())

                    }
                }
            }
            .build()
    }

    private fun getRequest(mediaSampleRateHertz: Int): StartStreamTranscriptionRequest? {
        return StartStreamTranscriptionRequest.builder()
            .languageCode(LanguageCode.EN_US.toString())
            .mediaEncoding(MediaEncoding.PCM)
            .mediaSampleRateHertz(mediaSampleRateHertz)
            .build()
    }

    private fun getCredentials(): AwsCredentialsProvider? {
        return DefaultCredentialsProvider.create()
    }
}