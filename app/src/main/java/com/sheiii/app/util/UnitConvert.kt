package com.sheiii.app.util

import android.content.Context
import android.util.TypedValue




/**
 * @author created by Zhenqing He on  16:27
 * @description
 */
class UnitConvert {
    companion object {
        fun px2dp(context: Context, pxValue: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                pxValue,
                context.resources.displayMetrics
            )
                .toInt()
        }

        fun px2sp(context: Context, pxValue: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                pxValue,
                context.resources.displayMetrics
            )
                .toInt()
        }

        fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        fun getImageHight(url: String) {

        }
    }


}