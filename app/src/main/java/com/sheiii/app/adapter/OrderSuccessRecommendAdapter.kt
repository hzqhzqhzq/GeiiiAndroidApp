package com.sheiii.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sheiii.app.R
import com.sheiii.app.model.Recommend
import com.sheiii.app.util.MyApplication

/**
 * @author created by Zhenqing He on  17:45
 * @description
 */
class OrderSuccessRecommendAdapter(dataList: List<Recommend>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = dataList

    class RecommendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val price = view.findViewById<TextView>(R.id.recommend_price)
        val image = view.findViewById<ImageView>(R.id.recommend_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecommendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_success_recommend_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        holder as RecommendViewHolder

        holder.price.text = data.salePriceTxt
        Glide.with(holder.image)
            .load(MyApplication.getImageHost() + data.picUrl)
            .into(holder.image)
        holder.image.background = null
    }

    override fun getItemCount(): Int {
        return list.size
    }

}