//snippet-sourcedescription:[BidirectionalStreaming.java demonstrates how to use the AWS Transcribe service to transcribe an audio input from the microphone.]
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
//snippet-start:[transcribe.java2.bidir_streaming.complete]
package scorpion.temp;

//snippet-start:[transcribe.java2.bidir_streaming.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.*;

import javax.sound.sampled.*;
//snippet-end:[transcribe.java2.bidir_streaming.import]

public class BidirectionalStreaming {

    private static Object TranscribeStreamingException;

    public static void main(String[] args) throws Exception {

        Region region = Region.US_EAST_1;
        TranscribeStreamingAsyncClient client = TranscribeStreamingAsyncClient.builder()
                .region(region)
                .build();

        convertAudio(client) ;
    }

    //snippet-start:[transcribe.java2.bidir_streaming.main]
    public static void convertAudio(TranscribeStreamingAsyncClient client) throws Exception {

        try {

            StartStreamTranscriptionRequest request = StartStreamTranscriptionRequest.builder()
                    .mediaEncoding(MediaEncoding.PCM)
                    .languageCode(LanguageCode.EN_US)
                    .mediaSampleRateHertz(16_000).build();

            TargetDataLine mic = Microphone.get();
            mic.start();

            AudioStreamPublisher publisher = new AudioStreamPublisher(new AudioInputStream(mic));

            StartStreamTranscriptionResponseHandler response =
                    StartStreamTranscriptionResponseHandler.builder().subscriber(e -> {
                        TranscriptEvent event = (TranscriptEvent) e;
                        event.transcript().results().forEach(r -> r.alternatives().forEach(a -> System.out.println(a.transcript())));
                    }).build();

            // Keeps Streaming until you end the Java program
            client.startStreamTranscription(request, publisher, response);

        } catch (TranscribeStreamingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }
    }
    //snippet-end:[transcribe.java2.bidir_streaming.main]

    public static TargetDataLine get() throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(datalineInfo);
        dataLine.open(format);
        return dataLine;
    }
}
//snippet-end:[transcribe.java2.bidir_streaming.complete]