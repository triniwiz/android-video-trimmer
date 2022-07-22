package idv.luchafang.videotrimmer.videoframe

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import idv.luchafang.videotrimmer.tools.SetVideoThumbnailTask
import kotlinx.android.synthetic.main.layout_video_trimmer.view.*
import java.io.File

internal class VideoFramesAdaptor(
    private val video: File?,
    private val frames: List<Long>,
    private val frameWidth: Int,
    private val videoUri: Uri? = null,
    private val context: Context? = null
) : RecyclerView.Adapter<VideoFramesAdaptor.ViewHolder>(),
    ListPreloader.PreloadModelProvider<Long> {

    private var preloadSizeProvider = ViewPreloadSizeProvider<Long>()

    var preloader: RecyclerViewPreloader<Long>? = null

    init {
        context?.let {

            preloader = RecyclerViewPreloader(
                Glide.with(it), this, preloadSizeProvider, frames.size
            )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(frameWidth, MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val vh = ViewHolder(imageView)

        preloadSizeProvider.setView(vh.itemView)

        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val frame = frames[position]

        holder.runner.frameMs = frame
        if (videoUri != null) {
            holder.runner.execute(videoUri)
        } else {
            holder.runner.execute(video)
        }
    }

    override fun getItemCount(): Int = frames.size


    override fun getPreloadItems(position: Int): List<Long> {
        return frames.subList(position, position + 1)
    }

    override fun getPreloadRequestBuilder(item: Long): RequestBuilder<Drawable>? {
        return context?.let {
            val options = RequestOptions()
                .frame(item * 1000)
                .centerCrop()

            var request = Glide.with(context).asDrawable()
            request = if (videoUri != null) {
                request.load(videoUri)
            } else {
                request.load(video)
            }

            request.apply(options)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var runner = SetVideoThumbnailTask(itemView as ImageView)
    }
}