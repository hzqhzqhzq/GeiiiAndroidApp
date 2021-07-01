package com.sheiii.app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.R
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.model.SubList
import com.sheiii.app.util.PerfectClickListener

/**
 * @author created by Zhenqing He on  10:55
 * @description
 */
class AreaAdapter(area: AreaListModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val area = area
    private val mList = area.allList
    private lateinit var clickInterface: ClickInterface

    class AreaHolder(view: View) : RecyclerView.ViewHolder(view) {
        val areaRecyclerView = view.findViewById<RecyclerView>(R.id.recycler_area_sub)
        val areaTitle = view.findViewById<TextView>(R.id.area_initials_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AreaHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_area_all_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AreaHolder

        holder.areaRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder()
                .setDatas(mList[position].list as List<*>)
                .setLayoutId(R.layout.recycler_area_sub_item)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as SubList
                        val name = viewHolder.itemView.findViewById<TextView>(R.id.area_name)
                        name.text = data.name

                        name.setOnClickListener(object : PerfectClickListener() {
                            override fun onNoDoubleClick(v: View?) {
                                clickInterface.areaSubItemClick(v!!, position, data, area.field)
                            }
                        })
                    }
                })
                .create()
        }
        holder.areaTitle.text = mList[position].firstLetter
    }

    override fun getItemCount(): Int = mList.size

    interface ClickInterface {
        fun areaSubItemClick(view: View, position: Int, data: SubList, type: String?)
    }

    fun areaSubItemClick(clickInterface: ClickInterface) {
        this.clickInterface = clickInterface
    }
}