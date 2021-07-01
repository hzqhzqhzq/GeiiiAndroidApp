package com.sheiii.app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.sheiii.app.model.HomeModuleProduct

/**
 * @author created by Zhenqing He on  09:29
 * @description 通用 recyclerView适配器
 *
 * 使用代码示例 ：
 * recyclerRecommend.adapter = CommentAdapter.Builder()
 *   .setDatas(videos.data)
 *   .setLayoutId(R.layout.item_recommend)
 *   .bindView(object : CommentAdapter.BindView{
 *       override fun onBindView(viewHolder: CommentAdapter.MyViewHolder, data: Any?) {
 *           val myData:Data = data as Data
 *           viewHolder.itemView.text_comment.text = myData.name
 *       }
 *   })
 *   .create()
 *
 */
class CommonAdapter private constructor() : RecyclerView.Adapter<CommonAdapter.MyViewHolder>() {
    private val BASE_ITEM_TYPE_HEADER = 100000
    private val BASE_ITEM_TYPE_FOOTER = 200000

    private val mHeaderViews: SparseArrayCompat<View> = SparseArrayCompat()
    private val mFootViews: SparseArrayCompat<View> = SparseArrayCompat()

    private val mInnerAdapter: RecyclerView.Adapter<*>? = null

    private var mDataList: List<*>? = null
    private var mLayoutId: Int? = null
    private var mFooterLayoutId: Int? = null
    private var mBindView: BindView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonAdapter.MyViewHolder {
        if (mHeaderViews.get(viewType) != null) {
            return MyViewHolder(mHeaderViews.get(viewType)!!)
        } else if (mFootViews.get(viewType) != null) {
            return MyViewHolder(mFootViews.get(viewType)!!)
        }
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(mLayoutId!!, parent, false))
    }

    override fun onBindViewHolder(holder: CommonAdapter.MyViewHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }
        mBindView?.onBindView(holder, mDataList?.get(position), position)
    }

    override fun getItemCount(): Int {
//        return mDataList!!.size
        return getRealItemCount() + getFootersCount() + getHeadersCount()
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position)
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount())
        }
        return position - getHeadersCount()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface BindView {
        fun onBindView(viewHolder: MyViewHolder, data: Any?, position: Int)
    }

    class Builder {
        private var commonAdapter: CommonAdapter = CommonAdapter()

        fun setDatas(lists: List<*>): Builder {
            commonAdapter.mDataList = lists
            return this
        }

        fun setLayoutId(layoutId: Int): Builder {
            commonAdapter.mLayoutId = layoutId
            return this
        }

        fun addHeaderView(view: View): Builder {
            commonAdapter.mHeaderViews.put(
                commonAdapter.mHeaderViews.size() + commonAdapter.BASE_ITEM_TYPE_HEADER,
                view
            )
            return this
        }

        fun addFootView(view: View): Builder {
            commonAdapter.mFootViews.put(
                commonAdapter.mFootViews.size() + commonAdapter.BASE_ITEM_TYPE_FOOTER,
                view
            )
            return this
        }

        fun setFootViewClickListener(listener: View.OnClickListener): Builder {
            commonAdapter.mFootViews[commonAdapter.mFootViews.size() + commonAdapter.BASE_ITEM_TYPE_FOOTER]?.setOnClickListener(listener)

            return this
        }

        fun bindView(bindView: BindView): Builder {
            commonAdapter.mBindView = bindView
            return this
        }

        fun create(): CommonAdapter {
            return commonAdapter
        }
    }

    fun updateData(newData: List<*>) {
        mDataList = newData
    }

    private fun isHeaderViewPos(position: Int): Boolean {
        return position < getHeadersCount()
    }

    private fun isFooterViewPos(position: Int): Boolean {
        return position >= getHeadersCount() + getRealItemCount()
    }

    private fun getRealItemCount(): Int {
        return mDataList?.size!!
    }

    private fun getHeadersCount(): Int {
        return mHeaderViews.size()
    }

    private fun getFootersCount(): Int {
        return mFootViews.size()
    }
}