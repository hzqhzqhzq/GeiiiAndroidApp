package com.sheiii.app.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable


/**
 * @author created by Zhenqing He on  13:44
 * @description
 */
class DrawableToBitmap {
    companion object {
        fun drawableToBitmap(drawable: Drawable) : Bitmap {
            // 获取 drawable 长宽
            // 获取 drawable 长宽
            val width = drawable.intrinsicWidth
            val heigh = drawable.intrinsicHeight

            drawable.setBounds(0, 0, width, heigh)

            // 获取drawable的颜色格式

            // 获取drawable的颜色格式
            val config =
                if (drawable.opacity !== PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            // 创建bitmap
            // 创建bitmap
            val bitmap = Bitmap.createBitmap(width, heigh, config)
            // 创建bitmap画布
            // 创建bitmap画布
            val canvas = Canvas(bitmap)
            // 将drawable 内容画到画布中
            // 将drawable 内容画到画布中
            drawable.draw(canvas)
            return bitmap
        }
    }
}