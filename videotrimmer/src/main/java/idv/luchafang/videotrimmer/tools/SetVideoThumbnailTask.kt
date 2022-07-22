package idv.luchafang.videotrimmer.tools
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import java.io.File


internal class SetVideoThumbnailTask constructor(
    var view: ImageView,
    var frameMs: Long = 0L,
    var fadeDuration: Long = 0L
) {
    private fun execute(file: Any?) {
        if (!(file is File || file is Uri)) {
            return
        }
        val options = RequestOptions()
            .frame(frameMs * 1000)
            .centerCrop()

        val glide = Glide.with(view)
        var request = glide
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