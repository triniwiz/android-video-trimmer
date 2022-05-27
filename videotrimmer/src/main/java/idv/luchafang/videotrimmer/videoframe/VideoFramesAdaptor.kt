package idv.luchafang.videotrimmer.videoframe

import android.net.Uri
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import idv.luchafang.videotrimmer.tools.SetVideoThumbnailTask
import java.io.File

internal class VideoFramesAdaptor(
    private val video: File?,
    private val frames: List<Long>,
    private val frameWidth: Int,
    private val videoUri: Uri? = null
) : RecyclerView.Adapter<VideoFramesAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(frameWidth, MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView as ImageView
        val frame = frames[position]

        val task = SetVideoThumbnailTask(view, frame)
        if (videoUri != null) {
            task.execute(videoUri)
        } else {
            task.execute(video)
        }
    }

    override fun getItemCount(): Int = frames.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}