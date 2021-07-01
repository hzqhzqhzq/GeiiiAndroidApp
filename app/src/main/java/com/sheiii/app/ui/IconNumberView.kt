package com.sheiii.app.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.sheiii.app.util.MyApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler
import kotlin.math.sqrt

/**
 * @author created by Zhenqing He on  13:29
 * @description
 */
class IconNumberView(context: Context, attributeSet: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attributeSet) {
    private lateinit var myCanvas: Canvas

//    private val bitmap: Bitmap = Bitmap.createBitmap(R.drawable.ic_outline_shopping_cart_24)
    private lateinit var bitmap: Bitmap
//    private var number = if (MyApplication.getCartNumber() != null) MyApplication.getCartNumber().toString() else "0"
    private var number = "0"
    private var isSetIcon = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        if (canvas != null) {
            myCanvas = canvas
        }
//        Log.d("ttttt-2.1", canvas?.isHardwareAccelerated.toString())
//        Log.d("ttttt-2", this.number)
        if (number != "0") {
            drawNumber()
        }
    }

    fun setIcon(bitmap: Bitmap) {
        this.bitmap = bitmap
        this.isSetIcon = true
    }

    fun setNumber(number: String) {
        this.number = number
//        drawNumber()
//        Log.d("ttttt-1", this.number)
//        GlobalScope.launch {
//            delay(1000)
//            this@IconNumberView.postInvalidate()
//        }
        if (this::myCanvas.isInitialized) {
//            draw(myCanvas)
//            postInvalidate()
        }
    }

    private fun drawNumber() {
        val x = width / 2
        val y = height / 2

        val paint = Paint()

        //设置画笔为红色
        paint.color = Color.RED

        //去除锯齿效果
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.isAntiAlias = true

//        if (isSetIcon) {
//            Log.d("isSetIcon", "11111111")
//            myCanvas.drawFilter = PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG)
//            myCanvas.save()
//            myCanvas.
//            myCanvas.drawBitmap(bitmap,Matrix(), paint)
//            myCanvas.restore()
//        }

        //计算小圆形的圆心图标，半径取大图标半径的四分之一
//        canvas?.drawCircle((x + sqrt((x * x / 2).toDouble())).toFloat(),(x - sqrt((x * x / 2).toDouble())).toFloat(), (x/2).toFloat(), paint)
        myCanvas.drawCircle((x + (x * 2 / 3).toDouble()).toFloat(),(x - (x * 2 / 3).toDouble()).toFloat(), (x / 3).toFloat(), paint)

        paint.color = Color.WHITE
        //为适应各种屏幕分辨率，字体大小取半径的3.5分之一，具体根据项目需要调节
        paint.textSize = (x / 2).toFloat()

        paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)

        paint.textAlign = Paint.Align.CENTER

        myCanvas.drawText(number, (x + (x * 2 / 3).toDouble()).toFloat(), (x- (x / 2).toDouble()).toFloat(), paint)
    }
}