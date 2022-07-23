package idv.luchafang.videotrimmer.videoframe

import android.content.Context
import android.net.Uri
import android.util.Log
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
    private val videoUri: Uri? = null,
    private val context: Context? = null
) : RecyclerView.Adapter<VideoFramesAdaptor.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(frameWidth, MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val frame = frames[position]

        holder.task.frameMs = frame
        if (videoUri != null) {
            holder.task.execute(videoUri)
        } else {
            holder.task.execute(video)
        }
    }

    override fun getItemCount(): Int = frames.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task = SetVideoThumbnailTask(itemView as ImageView)
    }
}