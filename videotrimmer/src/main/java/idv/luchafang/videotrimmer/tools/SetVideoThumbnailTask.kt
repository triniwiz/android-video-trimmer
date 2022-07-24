package idv.luchafang.videotrimmer.tools
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File

internal class SetVideoThumbnailTask constructor(
    val view: ImageView,
    var frameMs: Long = 0L,
    private val size: Int = 512,
    private val fadeDuration: Long = 0L
) {
    private var glide = Glide.with(view)
    private fun handleFile(file: Any?) {
        if (file is Uri || file is File) {
            val options = RequestOptions().frame(frameMs)
            var request = glide
                .asBitmap()
                .apply(options)
                .load(file)

            if (fadeDuration > 0) {
                request =
                    request.transition(BitmapTransitionOptions.withCrossFade((fadeDuration / 1000).toInt()))
            }

            request
                .into(view)
        }
    }

    fun execute(file: File?) {
        handleFile(file)
    }

    fun execute(uri: Uri?) {
        handleFile(uri)
    }
}