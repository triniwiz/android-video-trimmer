package idv.luchafang.videotrimmerexample

//import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import idv.luchafang.videotrimmer.VideoTrimmerView
import java.util.*


class MainActivity : AppCompatActivity(), VideoTrimmerView.OnSelectedRangeChangedListener {

    private val REQ_PICK_VIDEO = 100
    private val REQ_PERMISSION = 200

//    private val player: SimpleExoPlayer by lazy {
//        ExoPlayerFactory.newSimpleInstance(this).also {
//            it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
//            playerView.player = it
//        }
//    }


    internal class Adapter(
        var activity: MainActivity
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        var items = ArrayList<Uri>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val trimmer = inflater.inflate(R.layout.item, parent, false)
            return ViewHolder(trimmer)
        }


        fun handleVideo(videoTrimmerView: VideoTrimmerView, item: Uri) {
            videoTrimmerView.setBorderWidth(10f)
                .setBorderColor(Color.GREEN)
                .setBarBackgroundColor(Color.RED)
                .setLeftBarBackgroundColor(Color.RED)
                .setRightBarBackgroundColor(Color.RED)
                .setBarBackgroundColor(Color.GREEN)
                .setBarForegroundColor(Color.YELLOW)
                .setVideo(item)
                .setMinDuration(1_000)
                .setFrameCountInWindow(8)
                .setExtraDragSpace(activity.dpToPx(2f))
                .setOnSelectedRangeChangedListener(activity)
                .show()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            val itemView = holder.itemView
            val videoTrimmerView = itemView.findViewById<VideoTrimmerView>(R.id.videoTrimmerView)

            if (videoTrimmerView.isAttachedToWindow) {
                handleVideo(videoTrimmerView, item)
            } else {
                videoTrimmerView.addOnAttachStateChangeListener(object :
                    View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(view: View) {
                        handleVideo(videoTrimmerView, item)
                    }

                    override fun onViewDetachedFromWindow(view: View) {
                        videoTrimmerView.removeOnAttachStateChangeListener(this)
                    }
                })
            }
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }


    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "VideoTrimmer")
    }

    private var videoPath: String = ""

    private var videoUri: Uri? = null

    private var adapter: Adapter? = null

    private var layoutManager: LinearLayoutManager? = null

    private var pickVideoBtn: Button? = null

    private var listView: RecyclerView? = null

    /* -------------------------------------------------------------------------------------------*/
    /* Activity */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pickVideoBtn = findViewById(R.id.pickVideoBtn)
        listView = findViewById(R.id.listView)

        adapter = Adapter(this)


        pickVideoBtn?.setOnClickListener {
            Intent(Intent.ACTION_OPEN_DOCUMENT)
                .apply {
                    type = "video/*"
                }
                .also { startActivityForResult(it, REQ_PICK_VIDEO) }

        }

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listView?.layoutManager = layoutManager
        listView?.adapter = adapter
//        pickVideoBtn.setOnClickListener {
//            displayTrimmerView(
//                File(filesDir, "Firefox.mp4").absolutePath
//            )
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_PICK_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    //videoPath = getRealPathFromMediaData(data?.data)
                    // displayTrimmerView(videoPath)

                    data?.data?.let {
                        grantUriPermission(
                            packageName,
                            it,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        try {
                            videoUri = it
                            displayTrimmerView(videoUri!!)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_PERMISSION && grantResults.firstOrNull() != PackageManager.PERMISSION_GRANTED) {
            finish()
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmerView.OnSelectedRangeChangedListener */
    override fun onSelectRangeStart() {
        //player.playWhenReady = false
    }

    override fun onSelectRange(startMillis: Long, endMillis: Long) {
        showDuration(startMillis, endMillis)
    }

    override fun onSelectRangeEnd(startMillis: Long, endMillis: Long) {
        showDuration(startMillis, endMillis)
        if (videoUri != null) {
            // playVideo(videoUri, startMillis, endMillis)
        } else {
            // playVideo(videoPath, startMillis, endMillis)
        }


    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmer */
    private fun displayTrimmerView(path: String) {
//        videoTrimmerView
//            .setVideo(File(path))
//            .setMaxDuration(60_000)
//            .setMinDuration(3_000)
//            .setFrameCountInWindow(8)
//            .setExtraDragSpace(dpToPx(2f))
//            .setOnSelectedRangeChangedListener(this)
//            .show()
    }

    private fun displayTrimmerView(uri: Uri) {
        for (i in 0..9) {
            adapter?.items?.add(uri)
            adapter?.notifyItemInserted(i)
        }
//        videoTrimmerView
//            .setVideo(uri)
//            .setMaxDuration(60_000)
//            .setMinDuration(3_000)
//            .setFrameCountInWindow(8)
//            .setExtraDragSpace(dpToPx(2f))
//            .setOnSelectedRangeChangedListener(this)
//            .show()
    }

    /* -------------------------------------------------------------------------------------------*/
    /* ExoPlayer2 */
    private fun playVideo(path: String, startMillis: Long, endMillis: Long) {
        if (path.isBlank()) return

        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(path))
            .let {
                ClippingMediaSource(
                    it,
                    startMillis * 1000L,
                    endMillis * 1000L
                )
            }

        //   player.playWhenReady = true
        //  player.prepare(source)
    }


    private fun playVideo(path: Uri?, startMillis: Long, endMillis: Long) {
        if (path == null) return

        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(path)
            .let {
                ClippingMediaSource(
                    it,
                    startMillis * 1000L,
                    endMillis * 1000L
                )
            }

        //  player.playWhenReady = true
        // player.prepare(source)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Internal helpers */
    private fun getRealPathFromMediaData(data: Uri?): String {
        data ?: return ""

        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                data,
                arrayOf(MediaStore.Video.Media.DATA),
                null, null, null
            )

            val col = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()

            return cursor.getString(col)
        } finally {
            cursor?.close()
        }
    }

    private fun showDuration(startMillis: Long, endMillis: Long) {
        val duration = (endMillis - startMillis) / 1000L
        //  durationView.text = "$duration seconds selected"
    }

    fun dpToPx(dp: Float): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }
}
