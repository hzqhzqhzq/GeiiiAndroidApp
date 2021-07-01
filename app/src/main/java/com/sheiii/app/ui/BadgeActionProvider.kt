package com.sheiii.app.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ActionProvider
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.util.PerfectClickListener

/**
 * @author created by Zhenqing He on  18:15
 * @description
 */
class BadgeActionProvider(context: Context) : ActionProvider(context) {
    lateinit var icon: IconNumberView
    private lateinit var view: View
//    private lateinit var text: TextView

    override fun onCreateActionView(): View {
        val size = context.resources.getDimensionPixelSize(R.dimen.myActionBarSize)

        view = LayoutInflater.from(context).inflate(R.layout.badge_action_provider, null)
        icon = view.findViewById(R.id.toolbar_cart)
        icon.setLayerType(LAYER_TYPE_SOFTWARE, null)
        val layoutParams = ViewGroup.LayoutParams(size, size)
        view.layoutParams = layoutParams

//        view.setOnClickListener(object : PerfectClickListener() {
//            override fun onNoDoubleClick(v: View?) {
//                MainActivity.actionStart(context)
//            }
//        })

        return view
    }

    fun updateNumber(number: Int) {
        icon.setNumber(number.toString())
        icon.postInvalidate()
    }

    fun setClickListener(listener: PerfectClickListener) {
        view.setOnClickListener(listener)
    }
}