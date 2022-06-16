package scorpion.temp;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.IOException;
import java.io.InputStream;
public class Polly {
    private static String SAMPLE = "why was I programmed to feel pain!";

    public static void speak(String s) {
        PollyClient polly = PollyClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        SAMPLE = s;
        talkPolly(polly);
        polly.close();
    }

    public static void main(String args[]) {


        PollyClient polly = PollyClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        talkPolly(polly);
        polly.close();
    }

    public static void talkPolly(PollyClient polly) {

        try {
            DescribeVoicesRequest describeVoiceRequest = DescribeVoicesRequest.builder()
                    .engine("neural")
                    .build();

            DescribeVoicesResponse describeVoicesResult = polly.describeVoices(describeVoiceRequest);
            Voice voice = describeVoicesResult.voices().get(1);
            InputStream stream = synthesize(polly, SAMPLE, voice, OutputFormat.MP3);
            AudioDevice device = javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice();
            AdvancedPlayer player = new AdvancedPlayer(stream, device);
            player.setPlayBackListener(new PlaybackListener() {

                public void playbackStarted(PlaybackEvent evt) {
                    System.out.println("Playback started");
                    System.out.println(SAMPLE);
                }

                public void playbackFinished(PlaybackEvent evt) {
                    System.out.println("Playback finished");
                }
            });

            // play it!
            player.play();
        } catch (PollyException | JavaLayerException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static InputStream synthesize(PollyClient polly, String text, Voice voice, OutputFormat format) throws IOException {

        SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(voice.id())
                .outputFormat(format)
                .build();

        ResponseInputStream<SynthesizeSpeechResponse> synthRes = polly.synthesizeSpeech(synthReq);
        return synthRes;
    }
}
