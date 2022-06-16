package scorpion

import com.example.myapp.transcribestreaming.DefaultSubscription
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
 import software.amazon.awssdk.services.transcribestreaming.model.AudioStream
import java.io.InputStream

class AudioStreamPublisher(private val inputStream: InputStream): Publisher<AudioStream> {

    private var currentSubscription: Subscription? = null


    override fun subscribe(s: Subscriber<in AudioStream?>) {
        if (currentSubscription == null) {
            currentSubscription = DefaultSubscription(s, inputStream)
        } else {
            currentSubscription!!.cancel()
            currentSubscription = DefaultSubscription(s, inputStream)
        }
        s.onSubscribe(currentSubscription)
    }
}