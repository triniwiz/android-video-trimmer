package idv.luchafang.videotrimmer.tools

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.ViewPropertyTransition
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors


internal class SetVideoThumbnailTask constructor(
    view: ImageView,
    private val timeMs: Long = 0L,
    private val fadeDuration: Long = 0L
) {
    private val viewRef = WeakReference<ImageView>(view)

    private fun execute(file: Any?) {
        if (!(file is File || file is Uri)) {
            return
        }
        val view = viewRef.get() ?: return
        val options = RequestOptions()
            .frame(timeMs * 1000)

        var request = Glide.with(view)
            .asBitmap()
            .load(file)
            .apply(options)

        if (fadeDuration > 0) {
            request =
                request.transition(BitmapTransitionOptions.withCrossFade((fadeDuration / 1000).toInt()))
        }
        request.into(view)
    }

    fun execute(file: File?) {
        execute(file as Any?)
    }

    fun execute(file: Uri?) {
        execute(file as Any?)
    }
}