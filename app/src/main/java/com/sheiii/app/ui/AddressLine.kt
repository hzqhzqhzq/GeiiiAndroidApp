package com.sheiii.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


/**
 * @author created by Zhenqing He on  11:02
 * @description
 */
class AddressLine : View {
    //颜色块的宽度，可定义成XML属性，颜色值也可以定义为XML属性，请自行处理
    private val colorWidth = 7

    //空白块的宽度
    private val emptyWidth = 1

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onDraw(canvas: Canvas) {
        //获取View宽度
        val viewHeight = height
        //绘制完成的长度
        var drawLength = 0
        val paint = Paint()
        paint.isAntiAlias = true
        var count = 0

        //使用canvas循环绘制颜色块
        while (drawLength < width) {
            drawLength += emptyWidth * viewHeight
            count++
            //切换颜色
            if (count % 2 == 1) {
                paint.color = Color.rgb(255, 134, 134)
            } else {
                paint.color = Color.rgb(134, 194, 255)
            }
            //使用路径绘制一个菱形
            val path = Path()
            path.moveTo(drawLength.toFloat(), viewHeight.toFloat()) // 此点为多边形的起点
            path.lineTo((drawLength + colorWidth * viewHeight - viewHeight).toFloat(),
                viewHeight.toFloat()
            )
            path.lineTo((drawLength + colorWidth * viewHeight).toFloat(), 0F)
            path.lineTo((drawLength + viewHeight).toFloat(), 0F)
            path.close() // 使这些点构成封闭的多边形
            canvas.drawPath(path, paint)
            drawLength += colorWidth * viewHeight
        }
    }
}