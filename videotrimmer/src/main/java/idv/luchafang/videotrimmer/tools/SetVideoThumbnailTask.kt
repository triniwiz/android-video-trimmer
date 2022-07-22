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
    private val glide = Glide.with(view)

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

        request.addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {


                val retriever = MediaMetadataRetriever()
                val context = view.context
                val bitmap = try {

                    if (file is File) {
                        retriever.setDataSource(file.absolutePath)
                    }

                    if (file is Uri) {
                        retriever.setDataSource(context, file)
                    }

                    val timeUs = if (frameMs == 0L) -1 else frameMs * 1000
                    retriever.getFrameAtTime(timeUs)
                } catch (e: Exception) {
                    null
                } finally {
                    runCatching { retriever.release() }
                }

                bitmap?.let {
                    view.setImageBitmap(bitmap)
                } ?: run {
                    view.setImageDrawable(null)
                }

                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
            .into(view)
    }

    fun execute(file: File?) {
        execute(file as Any?)
    }

    fun execute(file: Uri?) {
        execute(file as Any?)
    }
}