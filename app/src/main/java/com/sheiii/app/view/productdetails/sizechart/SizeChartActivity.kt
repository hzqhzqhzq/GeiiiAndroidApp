package com.sheiii.app.view.productdetails.sizechart

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.SizeExplanation
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.SizeChartViewModel

class SizeChartActivity : AppCompatActivity() {
    private lateinit var sizeChartViewModel: SizeChartViewModel
    private lateinit var sizeChartColumnAdapter: CommonAdapter
    private lateinit var sizeChartRecyclerView: RecyclerView
    private lateinit var sizeChartExplanationRecyclerView: RecyclerView
    private lateinit var sizeChartExplanationAdapter: CommonAdapter
    private lateinit var sizeChartTabLayout: TabLayout
    private lateinit var sizeChartScroller: NestedScrollView

    private var sizeList: MutableList<List<String>> = mutableListOf()

    private var allSizeList: MutableList<String> = mutableListOf()

    private var scrollY = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_size_chart)


        val sid = intent.getStringExtra("sid")

        sizeChartViewModel = ViewModelProvider(this).get(SizeChartViewModel::class.java)
        if (sid != null) {
            sizeChartViewModel.getSizeChart(sid)
        }

        sizeChartScroller = findViewById(R.id.size_chart_scroller)

        sizeChartTabLayout = findViewById(R.id.size_chart_tab)
        sizeChartTabLayout.addTab(
            sizeChartTabLayout.newTab().setText(resources.getString(R.string.size_chart))
        )
        sizeChartTabLayout.addTab(
            sizeChartTabLayout.newTab().setText(resources.getString(R.string.how_to_measure))
        )
        sizeChartTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    sizeChartScroller.scrollTo(0, 0)
                } else {
                    sizeChartScroller.scrollTo(0, 1800)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()

        initSizeChart()

        initSizeExplanation()

    }

    /**
     * 初始化尺码表
     */
    private fun initSizeChart() {
        sizeChartViewModel.itemSize.observe(this, Observer {
            if (sizeChartViewModel.itemSize.value != null) {
                sizeList.add(sizeChartViewModel.itemSize.value!!.sizeHeaderList)
                sizeList.addAll(sizeChartViewModel.itemSize.value!!.sizeInchList)
                sizeList = switchColumnRow(sizeList)
                sizeChartColumnAdapter = CommonAdapter.Builder()
                    .setDatas(sizeList)
                    .setLayoutId(R.layout.recycler_size_chart_column_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            val sizeChartRawAdapter = CommonAdapter.Builder()
                                .setDatas(data as List<*>)
                                .setLayoutId(R.layout.recycler_size_chart_item)
                                .bindView(object : CommonAdapter.BindView {
                                    override fun onBindView(
                                        viewHolder: CommonAdapter.MyViewHolder,
                                        data: Any?,
                                        position: Int
                                    ) {
                                        viewHolder.itemView.findViewById<TextView>(R.id.size_info).text =
                                            data as String
                                    }
                                })
                                .create()
                            val recyclerView =
                                viewHolder.itemView.findViewById<RecyclerView>(R.id.size_chart_row_recycler)
                                    .apply {
                                        setHasFixedSize(false)
                                        layoutManager = StaggeredGridLayoutManager(
                                            1,
                                            StaggeredGridLayoutManager.VERTICAL
                                        )
                                        adapter = sizeChartRawAdapter
                                    }
                            if (position % 2 == 0) {
                                recyclerView.setBackgroundColor(resources.getColor(R.color.size_chart_ccc))
                            } else {
                                recyclerView.setBackgroundColor(resources.getColor(R.color.size_chart_eee))
                            }
                        }
                    })
                    .create()
                sizeChartRecyclerView =
                    findViewById<RecyclerView>(R.id.size_chart_column_recycler).apply {
                        setHasFixedSize(false)
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = sizeChartColumnAdapter
                    }
            }
        })

        // 尺码类型切换监听
        val cm = findViewById<TextView>(R.id.cm)
        val inch = findViewById<TextView>(R.id.inch)

        findViewById<TextView>(R.id.cm).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                sizeList.clear()
                sizeList.add(sizeChartViewModel.itemSize.value!!.sizeHeaderList)
                sizeList.addAll(sizeChartViewModel.itemSize.value!!.sizeCmList)
                val temp = switchColumnRow(sizeList)
                sizeList.clear()
                sizeList.addAll(temp)
                cm.background = resources.getDrawable(R.drawable.shape_stroke_333_solid_333)
                cm.setTextColor(resources.getColor(R.color.white))
                inch.background = resources.getDrawable(R.drawable.shape_stroke_333_solid_eee)
                inch.setTextColor(resources.getColor(R.color.black))
                sizeChartColumnAdapter.notifyDataSetChanged()
            }
        })

        findViewById<TextView>(R.id.inch).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                sizeList.clear()
                sizeList.add(sizeChartViewModel.itemSize.value!!.sizeHeaderList)
                sizeList.addAll(sizeChartViewModel.itemSize.value!!.sizeInchList)
                val temp = switchColumnRow(sizeList)
                sizeList.clear()
                sizeList.addAll(temp)
                cm.background = resources.getDrawable(R.drawable.shape_stroke_333_solid_eee)
                cm.setTextColor(resources.getColor(R.color.black))
                inch.background = resources.getDrawable(R.drawable.shape_stroke_333_solid_333)
                inch.setTextColor(resources.getColor(R.color.white))
                sizeChartColumnAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * 初始化尺码描述
     */
    private fun initSizeExplanation() {
        sizeChartViewModel.sizeExplanationList.observe(this, Observer {
            if (sizeChartViewModel.sizeExplanationList.value != null) {
                sizeChartExplanationAdapter = CommonAdapter.Builder()
                    .setDatas(sizeChartViewModel.sizeExplanationList.value as List<*>)
                    .setLayoutId(R.layout.recycler_size_chart_explanation_item)
                    .bindView(object : CommonAdapter.BindView {
                        override fun onBindView(
                            viewHolder: CommonAdapter.MyViewHolder,
                            data: Any?,
                            position: Int
                        ) {
                            data as SizeExplanation
                            viewHolder.itemView.findViewById<TextView>(R.id.exlanation_title).text =
                                data.title
                            var webView =
                                viewHolder.itemView.findViewById<WebView>(R.id.explanation_web)
                            var allContent = ""
                            for (content in data.contentList) {
                                allContent += "<p>$content</p>"
                            }
                            webView.isVerticalScrollBarEnabled = false //不能垂直滑动
                            webView.isHorizontalScrollBarEnabled = false //不能水平滑动
                            webView.loadDataWithBaseURL(
                                null,
                                "<html><head><style>img{max-width:100%;height:auto;}</style></head><body class=\"my_body\">$allContent</body></html>",
                                "text/html", "UTF-8", null
                            )
                        }
                    })
                    .create()
                sizeChartExplanationRecyclerView =
                    findViewById<RecyclerView>(R.id.explanation).apply {
                        setHasFixedSize(true)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter = sizeChartExplanationAdapter
                    }

                scrollY = sizeChartExplanationRecyclerView.y.toInt()
            }
        })
    }

    /**
     * 更换二维数组的行列
     * @param list 二维数组
     * @return resultList 更换之后的二维数组
     */
    private fun switchColumnRow(list: MutableList<List<String>>): MutableList<List<String>> {
        val resultList: MutableList<List<String>> = mutableListOf()
        var temp: MutableList<String>
        for (i in list[0].indices) {
            temp = mutableListOf()
            for (j in 0 until list.size) {
                temp.add(list[j][i])
            }
//            Log.d("switchColumnRow", temp.toString())
            resultList.add(temp)
        }
        Log.d("switchColumnRow", resultList.toString())
        return resultList
    }

    companion object {
        fun sizeChartStart(context: Context, sid: String) {
            val intent = Intent(context, SizeChartActivity::class.java)
            intent.putExtra("sid", sid)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}