package idv.luchafang.videotrimmer.tools

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

internal class SetVideoThumbnailTask constructor(
    view: ImageView,
    private val timeMs: Long = 0L,
    private val size: Int = 512,
    private val fadeDuration: Long = 0L
) {
    private val executor = Executors.newSingleThreadExecutor();
    private val viewRef = WeakReference<ImageView>(view)

    private val handler = Handler(Looper.getMainLooper())

    fun execute(file: File?) {
        executor.execute {
            val filePath = file?.path

            val retriever = MediaMetadataRetriever()

            val bitmap = try {
                retriever.setDataSource(filePath)

                val timeUs = if (timeMs == 0L) -1 else timeMs * 1000
                val bitmap = retriever.getFrameAtTime(timeUs)
                scaleBitmap(bitmap!!, size)
            } catch (e: Exception) {
                null
            } finally {
                runCatching { retriever.release() }
            }

            val view = viewRef.get() ?: return@execute

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
    }

    fun execute(file: Uri?) {
        executor.execute {
            val view = viewRef.get() ?: return@execute
            val context = view.context

            val retriever = MediaMetadataRetriever()

            val bitmap = try {
                retriever.setDataSource(context, file)

                val timeUs = if (timeMs == 0L) -1 else timeMs * 1000
                val bitmap = retriever.getFrameAtTime(timeUs)
                scaleBitmap(bitmap!!, size)
            } catch (e: Exception) {
                null
            } finally {
                runCatching { retriever.release() }
            }

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
    }


    private fun fadeOutEndListener(view: ImageView, result: Bitmap): AnimatorListenerAdapter =
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                view.setImageBitmap(result)
            }
        }
}