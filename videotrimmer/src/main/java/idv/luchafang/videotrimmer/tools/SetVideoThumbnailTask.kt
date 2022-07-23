package idv.luchafang.videotrimmer.tools

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

internal class SetVideoThumbnailTask constructor(
    view: ImageView,
    var frameMs: Long = 0L,
    private val size: Int = 512,
    private val fadeDuration: Long = 0L
) {

    private val viewRef = WeakReference<ImageView>(view)
    private val executor = Executors.newCachedThreadPool()

    private var currentTask: java.util.concurrent.Future<*>? = null

    private var handler = Handler(Looper.getMainLooper())

    private fun process(image: Any?) {
        currentTask?.let {
            if (!it.isCancelled || !it.isDone) {
                it.cancel(true)
                currentTask = null
            }
        }

        currentTask = executor.submit {
            val view = viewRef.get() ?: return@submit

            val retriever = MediaMetadataRetriever()

            val bitmap = try {
                if (image is String) {
                    retriever.setDataSource(image)
                }

                if (image is Uri) {
                    retriever.setDataSource(view.context, image)
                }


                val timeUs = if (frameMs == 0L) -1 else frameMs * 1000
                val bitmap = retriever.getFrameAtTime(timeUs)
                scaleBitmap(bitmap!!, size)
            } catch (e: Exception) {
                null
            } finally {
                runCatching { retriever.release() }
            }

            postToMain(view, bitmap)

        }
    }

    private fun postToMain(view: ImageView, bitmap: Bitmap?) {
        bitmap?.let {
            handler.post {
                if (fadeDuration == 0L) {
                    view.setImageBitmap(it)
                    return@post
                }

                val fadeOut = animateAlpha(
                    view,
                    1f,
                    0f,
                    fadeDuration,
                    autoPlay = false,
                    listener = fadeOutEndListener(view, bitmap)
                )
                val fadeIn = animateAlpha(view, 0f, 1f, fadeDuration, autoPlay = false)

                val animators = AnimatorSet()
                animators.playSequentially(fadeOut, fadeIn)
                animators.start()
            }
        }
    }

    fun execute(file: File?) {
        process(file?.path)
    }

    fun execute(uri: Uri?) {
        process(uri)
    }

    private fun fadeOutEndListener(view: ImageView, result: Bitmap): AnimatorListenerAdapter =
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                view.setImageBitmap(result)
            }
        }
}