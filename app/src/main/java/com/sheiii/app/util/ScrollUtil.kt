package com.sheiii.app.util

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author created by Zhenqing He on  11:18
 * @description
 */
class ScrollUtil {
    fun isScrollBottom(view: RecyclerView): Boolean {
        val layoutManager: StaggeredGridLayoutManager = view.layoutManager as StaggeredGridLayoutManager
        //屏幕中最后一个可见子项的position
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPositions(null)
        //当前屏幕所看到的子项个数
        val visibleItemCount = layoutManager.childCount
        //当前RecyclerView的所有子项个数
        val totalItemCount = layoutManager.itemCount
        //RecyclerView的滑动状态
//        val state = view.scrollState
//        Log.d("visibleItemCount", visibleItemCount.toString())
//        Log.d("lastVisibleItemPosition", lastVisibleItemPosition[0].toString())
//        Log.d("totalItemCount", totalItemCount.toString())
//        Log.d("rrrrrrsult", (visibleItemCount > 0 && lastVisibleItemPosition[0] >= totalItemCount - 1).toString())
        return visibleItemCount > 0 && lastVisibleItemPosition[0] >= totalItemCount - 1
    }
}