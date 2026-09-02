package com.example.climatrack.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 8f
    }

    private var extraBitmap: Bitmap? = null
    private var extraCanvas: Canvas? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (extraBitmap != null) extraBitmap?.recycle()
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        extraBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                extraCanvas?.drawPath(path, paint)
                path.reset()
                parent.requestDisallowInterceptTouchEvent(false)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        extraBitmap?.eraseColor(Color.TRANSPARENT)
        invalidate()
    }

    fun getSignatureBitmap(): Bitmap? {
        return extraBitmap
    }

    fun isEmpty(): Boolean {
        if (extraBitmap == null) return true
        val emptyBitmap = Bitmap.createBitmap(extraBitmap!!.width, extraBitmap!!.height, extraBitmap!!.config ?: Bitmap.Config.ARGB_8888)
        return extraBitmap!!.sameAs(emptyBitmap)
    }
}