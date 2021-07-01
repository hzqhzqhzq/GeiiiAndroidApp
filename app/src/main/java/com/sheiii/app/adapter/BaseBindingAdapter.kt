package com.sheiii.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author created by Zhenqing He on  15:01
 * @description
 */
class BaseBindingAdapter(context: Context) : RecyclerView.Adapter<BaseBindingAdapter.BaseBindingViewHolder>(){
    class BaseBindingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        TODO("Not yet implemented")

        /**
         * @description onCreateViewHolder
         * @return com.test.myapplication.adapter.BaseBindingAdapter.BaseBindingViewHolder
         */
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        TODO("Not yet implemented")

        /**
         * @description onBindViewHolder
         * @return kotlin.Unit
         */
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")

        /**
         * @description getItemCount
         * @return kotlin.Int
         */
    }
}