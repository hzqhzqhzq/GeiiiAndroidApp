package com.sheiii.app.adapter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.marginEnd
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.sheiii.app.GlideApp
import com.sheiii.app.GlideRequest
import com.sheiii.app.R
import com.sheiii.app.util.MyApplication
import com.sheiii.app.viewmodel.ProductDetailsViewModel

/**
 * @author created by Zhenqing He on  16:52
 * @description 产品详情页面 顶部的图片滑动适配器
 */
class ProductDetailsImageAdapter(list: List<String>) : PagerAdapter() {
    private val list = list

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(
            R.layout.product_details_image,
            container,
            false
        )
        val model = list[position]
        val number = view.findViewById<TextView>(R.id.position_all)
        val image = view.findViewById<ImageView>(R.id.product_details_image)

        val layout = view.findViewById<ConstraintLayout>(R.id.product_details_image_layout)

        number.text = (position + 1).toString() + " / " + list.size
//        GlideApp.with(image).load(MyApplication.getImageHost() + model)
//            .placeholder(R.drawable.bg_logo_320x320).into(image)
//        GlideApp.with(image).load(MyApplication.getImageHost() + model).into(object : SimpleTarget<Bitmap?>() {
//            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
//                Log.d("onResourceReady", "${resource.width} + ${resource.height}")
//            }
//        })
        GlideApp.with(image).load(MyApplication.getImageHost() + model)
//            .apply(RequestOptions().override(image.width, image.height).centerCrop())
            .placeholder(R.drawable.bg_logo_320x320)
            .into(image)

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }

    override fun getPageWidth(position: Int): Float {
        Log.d("getPageWidth", count.toString())
        return if (count == 1) {
            1f
        } else {
            0.66f
        }
    }

//    enum class TypeInflateView {
//        FirstPageType, CenterPageType, LastPageType
//    }
}