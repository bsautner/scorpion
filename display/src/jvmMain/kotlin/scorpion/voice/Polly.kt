package scorpion.voice

import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.FactoryRegistry
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.advanced.PlaybackListener
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.polly.PollyClient
import software.amazon.awssdk.services.polly.model.*
import java.io.IOException
import java.io.InputStream

class Polly {

    private var SAMPLE = "why was I programmed to feel pain!"

    fun speak(s: String) {
        val polly: PollyClient = PollyClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build()
        SAMPLE = s
        talkPolly(polly)
//        polly.close()
    }

    fun talkPolly(polly: PollyClient) {
        try {
            val describeVoiceRequest = DescribeVoicesRequest.builder()
                .engine("neural")
                .build()
            val describeVoicesResult = polly.describeVoices(describeVoiceRequest)
            val voice = describeVoicesResult.voices()[1]
            val stream: InputStream = synthesize(polly, SAMPLE, voice, OutputFormat.MP3)
            val device = FactoryRegistry.systemRegistry().createAudioDevice()
            val player = AdvancedPlayer(stream, device)
            player.playBackListener = object : PlaybackListener() {
                override fun playbackStarted(evt: PlaybackEvent) {
                    println("Playback started")
                    System.out.println(SAMPLE)
                }

                override fun playbackFinished(evt: PlaybackEvent) {
                    println("Playback finished")
                }
            }

            // play it!
            player.play()
        } catch (e: PollyException) {
            System.err.println(e.message)
            System.exit(1)
        } catch (e: JavaLayerException) {
            System.err.println(e.message)
            System.exit(1)
        } catch (e: IOException) {
            System.err.println(e.message)
            System.exit(1)
        }
    }

    @Throws(IOException::class)
    fun synthesize(
        polly: PollyClient,
        text: String?,
        voice: Voice,
        format: OutputFormat?
    ): InputStream {
        val synthReq = SynthesizeSpeechRequest.builder()
            .text(text)
            .voiceId(voice.id())
            .outputFormat(format)
            .build()
        return polly.synthesizeSpeech(synthReq)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Polly().speak("hello world")
        }
    }

}