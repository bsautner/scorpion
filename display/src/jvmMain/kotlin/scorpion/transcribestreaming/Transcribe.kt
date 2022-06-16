// snippet-sourcedescription:[TranscribeStreamingDemoApp.java transcribes streaming audio from your computer's microphone or a file upload. The output is presented on your computer's standard output.]
// snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.myapp.transcribestreaming

import scorpion.AudioStreamPublisher
import scorpion.transcribestreaming.VoiceCommandListener
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient
import software.amazon.awssdk.services.transcribestreaming.model.*
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URISyntaxException
import java.util.concurrent.ExecutionException
import javax.sound.sampled.*

// snippet-start:[transcribe.java-streaming-demo]
class Transcribe(private val voiceCommandListener: VoiceCommandListener) {
    @Throws(
        URISyntaxException::class,
        ExecutionException::class,
        InterruptedException::class,
        LineUnavailableException::class
    )
    fun start() {
        var client: TranscribeStreamingAsyncClient  = TranscribeStreamingAsyncClient.builder()
            .credentialsProvider(credentials)
            .region(REGION)
            .build()
        val result = client.startStreamTranscription(
            getRequest(16000),
            AudioStreamPublisher(streamFromMic),
            responseHandler
        )
        result.get()
        client.close()
    }

    // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
    @get:Throws(LineUnavailableException::class)
    private val streamFromMic: InputStream
        private get() {

            // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
            val sampleRate = 16000
            val format =
                AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
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

    private fun getRequest(mediaSampleRateHertz: Int): StartStreamTranscriptionRequest {
        return StartStreamTranscriptionRequest.builder()
            .languageCode(LanguageCode.EN_US.toString())
            .mediaEncoding(MediaEncoding.PCM)
            .mediaSampleRateHertz(mediaSampleRateHertz)
            .build()
    }

    //                            System.out.println(results.get(0).isPartial());
//                            System.out.println(results.get(0).alternatives().size());
//                            Polly.speak(results.get(0).alternatives().get(0).transcript());
    private val responseHandler: StartStreamTranscriptionResponseHandler
        private get() = StartStreamTranscriptionResponseHandler.builder()
            .onResponse { r: StartStreamTranscriptionResponse? -> println("Received Initial response") }
            .onError { e: Throwable ->
                println(e.message)
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                println("Error Occurred: $sw")
                e.message?.let { voiceCommandListener.onVoiceCommand(it) }
            }
            .onComplete { println("=== All records stream successfully ===") }
            .subscriber { event: TranscriptResultStream ->
                val results = (event as TranscriptEvent).transcript().results()
                if (results.size > 0) {
                    if (!results[0].alternatives()[0].transcript().isEmpty()) {
                        println(results[0].alternatives()[0].transcript())
                        voiceCommandListener.onVoiceCommand(results[0].alternatives()[0].transcript())
                        //                            System.out.println(results.get(0).isPartial());
//                            System.out.println(results.get(0).alternatives().size());
//                            Polly.speak(results.get(0).alternatives().get(0).transcript());
                    }
                }
            }
            .build()



    companion object {
        private val REGION = Region.US_EAST_1

        private val credentials: AwsCredentialsProvider
            private get() = DefaultCredentialsProvider.create()
    }
}