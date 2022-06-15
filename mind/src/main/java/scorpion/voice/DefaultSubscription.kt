package scorpion.voice

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.transcribestreaming.model.AudioEvent
import software.amazon.awssdk.services.transcribestreaming.model.AudioStream
import java.io.IOException
import java.io.InputStream
import java.io.UncheckedIOException
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class DefaultSubscription(private val s: Subscriber<in AudioStream?>?, private val inputStream: InputStream?)  : Subscription {
    private val CHUNK_SIZE_IN_BYTES = 1024
    private var subscriber: Subscriber<in AudioStream?>? = null
    private val executor = Executors.newFixedThreadPool(1)
    private val demand = AtomicLong(0)


    override fun request(n: Long) {
        if (n <= 0) {
            subscriber!!.onError(IllegalArgumentException("Demand must be positive"))
        }
        demand.getAndAdd(n)
        executor.submit {
            try {
                do {
                    val audioBuffer = getNextEvent()
                    if (audioBuffer!!.remaining() > 0) {
                        val audioEvent = audioEventFromBuffer(audioBuffer)
                        subscriber!!.onNext(audioEvent)
                    } else {
                        subscriber!!.onComplete()
                        break
                    }
                } while (demand.decrementAndGet() > 0)
            } catch (e: Exception) {
                subscriber!!.onError(e)
            }
        }
    }

    override fun cancel() {
        executor.shutdown()
    }

    private fun getNextEvent(): ByteBuffer? {
        var audioBuffer: ByteBuffer? = null
        val audioBytes = ByteArray(CHUNK_SIZE_IN_BYTES)
        var len = 0
        try {
            len = inputStream!!.read(audioBytes)
            audioBuffer = if (len <= 0) {
                ByteBuffer.allocate(0)
            } else {
                ByteBuffer.wrap(audioBytes, 0, len)
            }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
        return audioBuffer
    }

    private fun audioEventFromBuffer(bb: ByteBuffer?): AudioEvent {
        return AudioEvent.builder()
            .audioChunk(SdkBytes.fromByteBuffer(bb))
            .build()
    }
}