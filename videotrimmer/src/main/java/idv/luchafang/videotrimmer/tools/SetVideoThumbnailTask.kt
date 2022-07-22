package idv.luchafang.videotrimmer.tools

import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File


internal class SetVideoThumbnailTask constructor(
    var view: ImageView,
    var frameMs: Long = 0L,
    var fadeDuration: Long = 0L
) {
    val glide = Glide.with(view)

    private fun execute(file: Any?) {
        if (!(file is File || file is Uri)) {
            return
        }

        val options = RequestOptions()
            .frame(frameMs * 1000)
            .centerCrop()

        var request = glide
            .load(file)
            .apply(options)

        if (fadeDuration > 0) {
            request =
                request.transition(DrawableTransitionOptions.withCrossFade((fadeDuration / 1000).toInt()))
        }

        request
            .into(view)
    }

    fun execute(file: File?) {
        execute(file as Any?)
    }

    fun execute(file: Uri?) {
        execute(file as Any?)
    }
}