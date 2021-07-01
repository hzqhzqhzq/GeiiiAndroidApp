package com.sheiii.app.ui

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuItemCompat
import androidx.navigation.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.sheiii.app.MainActivity
import com.sheiii.app.R
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.UnitConvert
import java.util.zip.Inflater

/**
 * @author created by Zhenqing He on  13:20
 * @description
 */
class MyToolBar : MaterialToolbar {
//    private val view = LayoutInflater.from(context).inflate(R.layout.my_toolbar, this, false)
//    private var goCart: IconNumberView = view.findViewById(R.id.toolbar_cart)

//    private lateinit var view : View
//    private lateinit var goCart

//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//        goCart.setOnClickListener {
//            MainActivity.actionStart(context)
//        }

//        attachViewToParent(view, )
//        addView(view)
//    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        this.setNavigationIcon(R.drawable.ic_detail_back)
        this.setBackgroundColor(resources.getColor(R.color.red, context.theme))
        this.elevation = UnitConvert.dp2px(context, 4f).toFloat()
        this.background = resources.getDrawable(R.color.white, context.theme)

        val layoutParams = Toolbar.LayoutParams(LayoutParams.MATCH_PARENT, R.dimen.myActionBarSize)
        this.layoutParams = layoutParams

        this.logo = resources.getDrawable(R.drawable.mlogo, context.theme)

        this.setOnMenuItemClickListener {
            when(it.itemId) {
//                R.id.search -> {
//
//                }
//                R.id.go_cart -> {
//                    MainActivity.actionStart(context)
//                }
            }
            true
        }

        this.setNavigationOnClickListener {
            (context as Activity).finish()
        }
    }
}