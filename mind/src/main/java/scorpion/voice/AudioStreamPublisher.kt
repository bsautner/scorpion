package scorpion

import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import scorpion.voice.TranscribeStreamingDemoApp
import software.amazon.awssdk.services.transcribestreaming.model.AudioStream
import java.io.InputStream

class AudioStreamPublisher(private val inputStream: InputStream): Publisher<AudioStream> {

    private var currentSubscription: Subscription? = null


    override fun subscribe(s: Subscriber<in AudioStream?>) {
        if (currentSubscription == null) {
            currentSubscription = TranscribeStreamingDemoApp.SubscriptionImpl(s, inputStream)
        } else {
            currentSubscription!!.cancel()
            currentSubscription = TranscribeStreamingDemoApp.SubscriptionImpl(s, inputStream)
        }
        s.onSubscribe(currentSubscription)
    }
}