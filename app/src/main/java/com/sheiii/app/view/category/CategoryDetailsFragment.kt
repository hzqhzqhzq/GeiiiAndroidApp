package com.sheiii.app.view.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.CategorySub
import com.sheiii.app.model.Child
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.viewmodel.CategoryViewModel

class CategoryDetailsFragment(index: Int, pid: Int) : Fragment() {
    private lateinit var root: View
    private val index = index
    private val pid = pid
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_category_details, container, false)
        categoryViewModel.getSubList(pid)

        categoryRecyclerView = root.findViewById(R.id.category_sub_recyclerview)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        initSubList()
        super.onResume()
    }

    /**
     * 初始化 sub 分类 recyclerview
     */
    private fun initSubList() {
        categoryViewModel.subList.observe(viewLifecycleOwner, Observer {
            if (categoryViewModel.subList.value != null) {
                categoryRecyclerView.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter = CommonAdapter.Builder().setDatas(categoryViewModel.subList.value!!)
                        .setLayoutId(R.layout.recycler_category_sub)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as CategorySub
                                val view = viewHolder.itemView
                                view.findViewById<TextView>(R.id.category_sub_title).text =
                                    data.title
                                val recyclerView =
                                    view.findViewById<RecyclerView>(R.id.category_sub_item_recyclerview)

                                /* 初始化 sub 分类，子 item recyclerview */
                                recyclerView.apply {
                                    setHasFixedSize(true)
                                    layoutManager = StaggeredGridLayoutManager(
                                        3,
                                        StaggeredGridLayoutManager.VERTICAL
                                    )
                                    isNestedScrollingEnabled = false
                                    adapter = CommonAdapter.Builder()
                                        .setLayoutId(R.layout.recycler_category_sub_item)
                                        .setDatas(data.childs)
                                        .bindView(object : CommonAdapter.BindView {
                                            override fun onBindView(
                                                viewHolder: CommonAdapter.MyViewHolder,
                                                data: Any?,
                                                position: Int
                                            ) {
                                                data as Child
                                                val view = viewHolder.itemView
                                                view.findViewById<TextView>(R.id.category_sub_item_title).text =
                                                    data.title
                                                val image =
                                                    view.findViewById<ImageView>(R.id.category_sub_item_image)
                                                GlideApp.with(image)
                                                    .load(MyApplication.getImageHost() + data.image)
                                                    .placeholder(R.drawable.bg_logo_jpg_405x540)
                                                    .into(image)

                                                viewHolder.itemView.setOnClickListener(object : PerfectClickListener() {
                                                    override fun onNoDoubleClick(v: View?) {
                                                        CategoryActivity.actionStart(context, data.h5Url, false)
                                                    }
                                                })
                                            }
                                        }).create()
                                }
                            }
                        }).create()
                }
            }
        })
    }

}

