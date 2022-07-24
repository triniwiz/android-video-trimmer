package idv.luchafang.videotrimmer

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import idv.luchafang.videotrimmer.data.TrimmerDraft
import idv.luchafang.videotrimmer.slidingwindow.SlidingWindowView
import idv.luchafang.videotrimmer.tools.dpToPx
import idv.luchafang.videotrimmer.videoframe.VideoFramesAdaptor
import idv.luchafang.videotrimmer.videoframe.VideoFramesScrollListener
import kotlinx.android.synthetic.main.layout_video_trimmer.view.*
import java.io.File
import kotlin.math.roundToInt


inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            v: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            val oldWidth = oldRight - oldLeft
            val oldHeight = oldBottom - oldTop
            val width = right - left
            val height = bottom - top

            if (oldWidth == 0 && oldHeight == 0 && width > 0 && height > 0) {
                f()
                removeOnLayoutChangeListener(this)
            }
        }
    })
}


class VideoTrimmerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), VideoTrimmerContract.View {

    private var barBackgroundColor: Int? = null
    private var barForegroundColor: Int? = null

    private var leftBarBackgroundColor: Int? = null
    private var leftBarForegroundColor: Int? = null

    private var rightBarBackgroundColor: Int? = null
    private var rightBarForegroundColor: Int? = null

    @DrawableRes
    private var leftBarRes: Int = R.drawable.trimmer_left_bar

    @DrawableRes
    private var rightBarRes: Int = R.drawable.trimmer_right_bar

    private var barWidth: Float = dpToPx(context, 10f)
    private var borderWidth: Float = 0f

    @ColorInt
    private var borderColor: Int = Color.BLACK

    @ColorInt
    private var overlayColor: Int = Color.argb(120, 183, 191, 207)

    private var presenter: VideoTrimmerContract.Presenter? = null
    private var adaptor: VideoFramesAdaptor? = null


    /* -------------------------------------------------------------------------------------------*/
    /* Initialize */
    init {
        inflate(context, R.layout.layout_video_trimmer, this)
        obtainAttributes(attrs)
        initViews()
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        attrs ?: return

        val array = resources.obtainAttributes(attrs, R.styleable.VideoTrimmerView)
        try {
            leftBarRes =
                array.getResourceId(R.styleable.VideoTrimmerView_vtv_window_left_bar, leftBarRes)
            slidingWindowView.leftBarRes = leftBarRes

            rightBarRes =
                array.getResourceId(R.styleable.VideoTrimmerView_vtv_window_right_bar, rightBarRes)
            slidingWindowView.rightBarRes = rightBarRes

            barWidth =
                array.getDimension(R.styleable.VideoTrimmerView_vtv_window_bar_width, barWidth)
            slidingWindowView.barWidth = barWidth

            borderWidth = array.getDimension(
                R.styleable.VideoTrimmerView_vtv_window_border_width,
                borderWidth
            )
            slidingWindowView.borderWidth = borderWidth

            borderColor =
                array.getColor(R.styleable.VideoTrimmerView_vtv_window_border_color, borderColor)
            slidingWindowView.borderColor = borderColor

            overlayColor =
                array.getColor(R.styleable.VideoTrimmerView_vtv_overlay_color, overlayColor)
            slidingWindowView.overlayColor = overlayColor
        } finally {
            array.recycle()
        }
    }

    private fun initViews() {
        videoFrameListView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }

    fun setBarBackgroundColor(color: Int): VideoTrimmerView {
        barBackgroundColor = color
        slidingWindowView.barBackgroundColor = color
        return this
    }

    fun setBarForegroundColor(color: Int): VideoTrimmerView {
        barForegroundColor = color
        slidingWindowView.barForegroundColor = color
        return this
    }


    fun setLeftBarBackgroundColor(color: Int): VideoTrimmerView {
        leftBarBackgroundColor = color
        slidingWindowView.leftBarBackgroundColor = color
        return this
    }

    fun setLeftBarForegroundColor(color: Int): VideoTrimmerView {
        leftBarForegroundColor = color
        slidingWindowView.leftBarForegroundColor = color
        return this
    }

    fun setRightBarBackgroundColor(color: Int): VideoTrimmerView {
        rightBarBackgroundColor = color
        slidingWindowView.rightBarBackgroundColor = color
        return this
    }

    fun setRightBarForegroundColor(color: Int): VideoTrimmerView {
        rightBarForegroundColor = color
        slidingWindowView.rightBarForegroundColor = color
        return this
    }

    fun setBarWidth(width: Float): VideoTrimmerView {
        barWidth = dpToPx(context, width)
        slidingWindowView.barWidth = barWidth
        return this
    }

    fun setBorderWidth(width: Float): VideoTrimmerView {
        borderWidth = dpToPx(context, width)
        slidingWindowView.borderWidth = borderWidth
        return this
    }

    fun setBorderColor(color: Int): VideoTrimmerView {
        borderColor = color
        slidingWindowView.borderColor = color
        return this
    }

    fun setOverLayColor(color: Int): VideoTrimmerView {
        overlayColor = color
        slidingWindowView.overlayColor = color
        return this
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Attach / Detach */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter = obtainVideoTrimmerPresenter()
            .apply { onViewAttached(this@VideoTrimmerView) }
        onPresenterCreated()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.onViewDetached()
        presenter = null
    }

    private fun onPresenterCreated() {
        presenter?.let {
            slidingWindowView.listener = presenter as SlidingWindowView.Listener

            val horizontalMargin = (dpToPx(context, barWidth)).roundToInt()
            val scrollListener = VideoFramesScrollListener(
                horizontalMargin,
                presenter as VideoFramesScrollListener.Callback
            )

            videoFrameListView.addOnScrollListener(scrollListener)
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Public APIs */
    fun setVideo(video: File): VideoTrimmerView {
        presenter?.setVideo(video)
        return this
    }

    fun setVideo(video: Uri): VideoTrimmerView {
        presenter?.setVideo(video)
        return this
    }

    fun setMaxDuration(millis: Long): VideoTrimmerView {
        presenter?.setMaxDuration(millis)
        return this
    }

    fun setMinDuration(millis: Long): VideoTrimmerView {
        presenter?.setMinDuration(millis)
        return this
    }

    fun setFrameCountInWindow(count: Int): VideoTrimmerView {
        presenter?.setFrameCountInWindow(count)
        return this
    }

    fun setOnSelectedRangeChangedListener(listener: OnSelectedRangeChangedListener): VideoTrimmerView {
        presenter?.setOnSelectedRangeChangedListener(listener)
        return this
    }

    fun setExtraDragSpace(spaceInPx: Float): VideoTrimmerView {
        slidingWindowView.extraDragSpace = spaceInPx
        return this
    }

    fun show() {
        presenter?.show()
    }

    fun getTrimmerDraft(): TrimmerDraft? = presenter?.getTrimmerDraft()

    fun restoreTrimmer(draft: TrimmerDraft) {
        presenter?.restoreTrimmer(draft)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmerContract.View */

    override fun getSlidingWindowWidth(): Int {
        // val screenWidth = resources.displayMetrics.widthPixels
        val margin = dpToPx(context, 10f)
        return width - 2 * (margin + barWidth).roundToInt()
    }

    override fun setupAdaptor(video: File, frames: List<Long>, frameWidth: Int) {
        adaptor = VideoFramesAdaptor(video, frames, frameWidth).also {
            videoFrameListView.adapter = it
        }
    }

    override fun setupAdaptor(video: Uri, frames: List<Long>, frameWidth: Int) {
        adaptor = VideoFramesAdaptor(null, frames, frameWidth, video).also {
            videoFrameListView.adapter = it
        }
    }

    override fun setupSlidingWindow() {
        slidingWindowView.reset()
    }

    override fun restoreSlidingWindow(left: Float, right: Float) {
        slidingWindowView.setBarPositions(left, right)
    }

    override fun restoreVideoFrameList(framePosition: Int, frameOffset: Int) {
        val layoutManager = videoFrameListView.layoutManager as? LinearLayoutManager ?: return
        layoutManager.scrollToPositionWithOffset(framePosition, frameOffset)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* Listener */
    interface OnSelectedRangeChangedListener {
        fun onSelectRangeStart()
        fun onSelectRange(startMillis: Long, endMillis: Long)
        fun onSelectRangeEnd(startMillis: Long, endMillis: Long)
    }
}