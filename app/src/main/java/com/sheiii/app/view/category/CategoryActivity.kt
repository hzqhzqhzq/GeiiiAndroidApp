package com.sheiii.app.view.category

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.sheiii.app.GlideApp
import com.sheiii.app.R
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.adapter.ProductListTwoAdapter
import com.sheiii.app.model.*
import com.sheiii.app.ui.InterceptTouchConstraintLayout
import com.sheiii.app.util.*
import com.sheiii.app.viewmodel.CategoryViewModel
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.MultiStatePage
import com.zy.multistatepage.OnRetryEventListener
import com.zy.multistatepage.state.EmptyState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState

/**
 * @description category activity
 * @author Zhenqing He
 * @createDate 2021/4/13 17:36
 */

class CategoryActivity : AppCompatActivity() {
    private var searchUrl: String = ""
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryProductRecyclerView: RecyclerView
    private var categoryId = 0
    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View
    private lateinit var attrRecyclerView: RecyclerView
    private val searchProductList = mutableListOf<MutableList<ProductModel>>()

    /* 存储选中的filter attr */
    private var checkAttrList: MutableList<MutableMap<String, String>> = mutableListOf()

    /* 存储选中的category id */
    private var checkCategory = -1

    /* 存储选中的sortCode */
    private var checkSortCode = ""

    /* 标记当前level */
    private var currentLevel = -1

    /* 用来同步下拉加载，防止重复请求 */
    private var flag = true

    /* 判断是否从home页面进入 */
    private var isFromHome = false

    private lateinit var multiStateContainer: MultiStateContainer
    private lateinit var listMultiStateContainer: MultiStateContainer
    private lateinit var refreshLayout: RefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        multiStateContainer = MultiStatePage.bindMultiState(this, object : OnRetryEventListener {
            override fun onRetryEvent(multiStateContainer: MultiStateContainer) {
                categoryViewModel.getFilter(categoryId)
                search()
            }
        })
        multiStateContainer.show<LoadingState> { }

        refreshLayout = findViewById<View>(R.id.refreshLayout) as RefreshLayout
        refreshLayout.setRefreshFooter(ClassicsFooter(this))
        refreshLayout.setEnableHeaderTranslationContent(false)
        refreshLayout.setOnLoadMoreListener {
//            homeViewModel.loadRecommendList(index)
//            refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
//            search()
            categoryViewModel.search(
                checkCategory,
                sortCode = checkSortCode,
                attrsList = checkAttrList,
                pageNumber = categoryViewModel.searchResult.value?.pageNumber!! + 1,
                plusSizeFlag = null
            )
        }

        refreshLayout.setOnRefreshListener {
//            multiStateContainer.show<LoadingState> { }
            categoryProductRecyclerView.adapter = null
            categoryViewModel.search(
                checkCategory,
                sortCode = checkSortCode,
                attrsList = checkAttrList,
                pageNumber = null,
                plusSizeFlag = null
            )
        }

        findViewById<ConstraintLayout>(R.id.category_parent).layoutTransition = LayoutTransition()

        searchUrl = intent.getStringExtra("searchUrl").toString()
        isFromHome = intent.getBooleanExtra("isFromHome", false)

        categoryProductRecyclerView = findViewById(R.id.category_product_recyclerview)

//        listMultiStateContainer = categoryProductRecyclerView.bindMultiState()
//        listMultiStateContainer.show<LoadingState> {  }

        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        categoryViewModel.getFilter(categoryId)

        /* 从url中获取 categoryFid */
        search()

        popupView = layoutInflater.inflate(R.layout.popup_filter, null)
        popupWindow = PopupWindow(
            popupView,
            UnitConvert.dp2px(this, 300f),
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
//        popupWindow.animationStyle = R.style.AnimationFade
        popupWindow.setOnDismissListener {
            setBackgroundAlpha(1f);
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        initSearchResultView()

        categoryViewModel.filter.observe(this, Observer {
            if (categoryViewModel.filter.value != null) {
                // 设置 select category 点击监听
                initCategoryTopDialog()
                // 设置 select sort 点击监听
                initSortTopDialog()
                // 设置 过滤器 点击监听
                initFilterDialog()
                /* 设置当前选中category id */
                checkCategory = categoryViewModel.filter.value?.selectCategoryId!!
            }
        })
    }

    /**
     * 初始化 搜索结果产品列表内容
     * @author Zhenqing He
     * @createDate 2021/4/13 17:54
     */
    private fun initSearchResultView() {
        categoryViewModel.searchProductList.observe(this, Observer {
            if (categoryViewModel.searchProductList.value != null) {
                Log.d("searchProductList", categoryViewModel.searchProductList.value?.size.toString())
                val oldSize = searchProductList.size
                searchProductList.clear()
                searchProductList.addAll(InitProductListTwo().initList(categoryViewModel.searchProductList.value!!))
//                if ()
                if (categoryProductRecyclerView.adapter == null) {
                    categoryProductRecyclerView.apply {
                        setHasFixedSize(false)
                        layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        adapter =
                            ProductListTwoAdapter(searchProductList, null)
                    }
                } else {
                    categoryProductRecyclerView.adapter?.notifyItemRangeInserted(oldSize, searchProductList.size - oldSize)
                }
//                multiStateContainer.show
                refreshLayout.finishLoadMore()
                refreshLayout.finishRefresh()
                if (searchProductList.size == 0) {
                    multiStateContainer.show<EmptyState> {  }
//                    listMultiStateContainer.show<EmptyState> {  }
                } else {
                    multiStateContainer.show<SuccessState> {  }
//                    listMultiStateContainer.show<SuccessState> {  }
                }

            }
        })

    }

    /**
     * 打开 category 顶部弹出框
     * @author Zhenqing He
     * @createDate 2021/4/14 14:35
     */
    private fun initCategoryTopDialog() {
        val background = findViewById<View>(R.id.category_background)
        val reset = findViewById<TextView>(R.id.category_top_reset)
        val done = findViewById<TextView>(R.id.category_top_done)
        val tabLayout = findViewById<TabLayout>(R.id.category_top_tablayout)
        val data = categoryViewModel.filter.value!!
        findViewById<TextView>(R.id.select_category_name).text = data.selectCategoryName
        val recyclerView = findViewById<RecyclerView>(R.id.category_top_recyclerview)

        /**
         * @if 如果tablayout的大小不等于selectParentCategory的大小，则移除原有tab添加tab
         * @else 重新设置tab的title
         */
        if (tabLayout.tabCount != data.selectParentCategory.size) {
            tabLayout.removeAllTabs()
            if (data.selectParentCategory.isNotEmpty()) {
                for (data in data.selectParentCategory) {
                    val tab = tabLayout.newTab().setText(data.title)
                    tab.tag = data.categoryId

                    tab.view.setOnClickListener(object : PerfectClickListener() {
                        override fun onNoDoubleClick(v: View?) {
                            categoryViewModel.getFilter(tab.tag as Int)
                        }
                    })
                    tabLayout.addTab(tab)
                }
            } else {
                tabLayout.addTab(tabLayout.newTab().setText(data.selectCategoryName))
            }

        } else {
            tabLayout.getTabAt(data.selectCategoryLevel)?.text = data.selectCategoryName
        }

        tabLayout.getTabAt(data.selectCategoryLevel!!)?.select()

        /**
         * @if 初始化recyclerView adapter 并且更新 currentLevel
         * @else 更新recyclerView
         */
        if (recyclerView.adapter == null || currentLevel != data.selectCategoryLevel) {
            recyclerView.apply {
                setHasFixedSize(false)
                layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                adapter =
                    CommonAdapter.Builder()
                        .setDatas(data.categoryList as List<*>)
                        .setLayoutId(R.layout.recycler_category_top_item)
                        .bindView(object : CommonAdapter.BindView {
                            override fun onBindView(
                                viewHolder: CommonAdapter.MyViewHolder,
                                data: Any?,
                                position: Int
                            ) {
                                data as Category
                                val view = viewHolder.itemView
                                val text = view.findViewById<TextView>(R.id.category_name)
                                val image =
                                    view.findViewById<ImageView>(R.id.is_check_image)
                                text.text = data.title

                                for (item in categoryViewModel.filter.value?.selectParentCategory!!) {
                                    if (item.categoryLevel == data.categoryLevel && item.categoryId == data.id) {
                                        text.setTextColor(
                                            resources.getColor(
                                                R.color.red,
                                                context.theme
                                            )
                                        )
                                        image.visibility = View.VISIBLE
                                    } else {
                                        text.setTextColor(
                                            resources.getColor(
                                                R.color.text_323232,
                                                context.theme
                                            )
                                        )
                                        image.visibility = View.GONE
                                    }
                                }

                                viewHolder.itemView.setOnClickListener(object : PerfectClickListener() {
                                    override fun onNoDoubleClick(v: View?) {
                                        categoryViewModel.getFilter(data.id)
                                    }
                                })
                            }
                        }).create()
            }
            currentLevel = data.selectCategoryLevel!!
        } else {
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        background.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openOrHideDialog("category")
            }
        })

        findViewById<LinearLayout>(R.id.select_category).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openOrHideDialog("category")
            }
        })

        reset.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                resetCategory()
            }
        })

        done.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                submitCategory()
            }
        })
    }

    /**
     * 初始化sort顶部弹出框
     * @author Zhenqing He
     * @createDate 2021/4/16 12:21
     */
    private fun initSortTopDialog() {
        val recyclerView = findViewById<RecyclerView>(R.id.sort_top_recyclerview)
        val background = findViewById<View>(R.id.sort_background)

        val data = categoryViewModel.filter.value
        val sortTitle = findViewById<TextView>(R.id.select_sort_name)
        sortTitle.text = data?.selectSortName
        recyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setLayoutId(R.layout.recycler_sort_top_item)
                .setDatas(data?.sortList!!)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as Sort
                        val view = viewHolder.itemView
                        val title = view.findViewById<TextView>(R.id.sort_name)
                        val image = view.findViewById<ImageView>(R.id.is_check_image)
                        title.text = data.title

                        if (data.code == categoryViewModel.filter.value?.selectSortCode) {
                            title.setTextColor(resources.getColor(R.color.red, theme))
                            image.visibility = View.VISIBLE
                        } else {
                            title.setTextColor(resources.getColor(R.color.text_323232, theme))
                            image.visibility = View.GONE
                        }

                        view.setOnClickListener(object : PerfectClickListener() {
                            override fun onNoDoubleClick(v: View?) {
                                checkSortCode = data.code
                                categoryViewModel.search(categoryId, data.code, null, null, null)
                                categoryViewModel.filter.value?.selectSortCode = data.code
                                categoryViewModel.filter.value?.selectSortName = data.title
                                sortTitle.text = data.title
                                recyclerView.adapter?.notifyDataSetChanged()
                                openOrHideDialog("sort")
                            }
                        })
                    }
                }).create()
        }

        findViewById<LinearLayout>(R.id.select_sort).setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openOrHideDialog("sort")
            }
        })
        background.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openOrHideDialog("sort")
            }
        })
    }

    /**
     * 初始化 filter dialog所有数据
     * @author Zhenqing He
     * @createDate 2021/4/16 18:02
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initFilterDialog() {
        val data = categoryViewModel.filter.value
        val filter = findViewById<ImageView>(R.id.filter_icon)
        attrRecyclerView = popupView.findViewById<RecyclerView>(R.id.filter_attr_recyclerview)
        val reset = popupView.findViewById<TextView>(R.id.filter_attr_reset)
        val done = popupView.findViewById<TextView>(R.id.filter_attr_done)

        filter.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                openOrHideDialog("filter")
            }
        })

        reset.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                resetFilterAttr()
            }
        })

        done.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                submitFilterAttr()
            }
        })

        attrRecyclerView.apply {
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = CommonAdapter.Builder().setDatas(data?.attrList!!)
                .setLayoutId(R.layout.recycler_filter_attr)
                .bindView(object : CommonAdapter.BindView {
                    override fun onBindView(
                        viewHolder: CommonAdapter.MyViewHolder,
                        data: Any?,
                        position: Int
                    ) {
                        data as Attr
                        val view = viewHolder.itemView
                        val icon = view.findViewById<TextView>(R.id.filter_attr_icon)
                        val attrValueRecyclerView =
                            view.findViewById<RecyclerView>(R.id.filter_attr_value_recyclerview)
                        view.findViewById<TextView>(R.id.filter_attr_title_name).text =
                            data.attrNameAlias

                        /* 设置 sku size 超出屏幕换行布局 */
                        val manager = object :
                            FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        manager.flexWrap = FlexWrap.WRAP
                        manager.flexDirection = FlexDirection.ROW
                        manager.justifyContent = JustifyContent.FLEX_START

                        attrValueRecyclerView.apply {
                            setHasFixedSize(false)
                            layoutManager = manager
                            adapter = CommonAdapter.Builder().setDatas(data.attrValueList)
                                .setLayoutId(R.layout.recycler_filter_attr_value)
                                .bindView(object : CommonAdapter.BindView {
                                    override fun onBindView(
                                        viewHolder: CommonAdapter.MyViewHolder,
                                        data: Any?,
                                        position: Int
                                    ) {
                                        data as AttrValue
                                        val view = viewHolder.itemView
                                        val text =
                                            view.findViewById<TextView>(R.id.filter_attr_value_text)
                                        text.text = data.attrValueAlias
                                        /* 点击选择attr value 处理 */
                                        text.setOnClickListener(object : PerfectClickListener() {
                                            override fun onNoDoubleClick(v: View?) {
                                                /**
                                                 * @if 当 attr value 选中处理
                                                 * @else 当 attr value 未选中的处理
                                                 */
                                                if (text.currentTextColor == resources.getColor(
                                                        R.color.red,
                                                        theme
                                                    )
                                                ) {
                                                    text.setTextColor(
                                                        resources.getColor(
                                                            R.color.text_323232,
                                                            theme
                                                        )
                                                    )
                                                    text.background = resources.getDrawable(
                                                        R.drawable.border_eee_rd6,
                                                        theme
                                                    )
                                                    data.isCheck = false
                                                } else {
                                                    text.setTextColor(
                                                        resources.getColor(
                                                            R.color.red,
                                                            theme
                                                        )
                                                    )
                                                    text.background = resources.getDrawable(
                                                        R.drawable.border_red_rd6,
                                                        theme
                                                    )
                                                    data.isCheck = true
                                                }
                                            }
                                        })

                                        /* 判断 isCheck */
                                        if (data.isCheck) {
                                            text.setTextColor(
                                                resources.getColor(
                                                    R.color.red,
                                                    theme
                                                )
                                            )
                                            text.background = resources.getDrawable(
                                                R.drawable.border_red_rd6,
                                                theme
                                            )
                                        } else {
                                            text.setTextColor(
                                                resources.getColor(
                                                    R.color.text_323232,
                                                    theme
                                                )
                                            )
                                            text.background = resources.getDrawable(
                                                R.drawable.border_eee_rd6,
                                                theme
                                            )
                                        }
                                    }
                                }).create()
                        }

                        /* 显示第一个filter attr 数据 */
                        if (position == 0) {
                            attrValueRecyclerView.visibility = View.VISIBLE
                            icon.text = "-"
                        }

                        /* 点击打开 filter attr value 数据 */
                        view.findViewById<ConstraintLayout>(R.id.filter_attr_title)
                            .setOnClickListener(object : PerfectClickListener() {
                                override fun onNoDoubleClick(v: View?) {
                                    if (attrValueRecyclerView.visibility == View.GONE) {
                                        attrValueRecyclerView.visibility = View.VISIBLE
                                        icon.text = "-"
                                    } else {
                                        attrValueRecyclerView.visibility = View.GONE
                                        icon.text = "+"
                                    }
                                }
                            })
                    }
                }).create()
        }
    }

    /**
     * 关闭或开启category页面中的dialog
     * @param type 操作的dialog
     * @author Zhenqing He
     * @createDate 2021/4/16 17:56
     */
    private fun openOrHideDialog(type: String) {
        val category = findViewById<InterceptTouchConstraintLayout>(R.id.category_top)
        val sort = findViewById<InterceptTouchConstraintLayout>(R.id.sort_top)
        when (type) {
            "category" -> {
                if (sort.visibility == View.VISIBLE) {
                    openOrHideDialog("sort")
                }
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
                val image = findViewById<ImageView>(R.id.select_category_icon)
                val title = findViewById<TextView>(R.id.select_category_name)
                if (category.visibility == View.GONE) {
                    category.visibility = View.VISIBLE
                    title.setTextColor(resources.getColor(R.color.red, theme))
                    GlideApp.with(image)
                        .load(resources.getDrawable(R.drawable.ic_triangle_up_red_24, theme))
                        .into(image)
                } else {
                    category.visibility = View.GONE
                    title.setTextColor(resources.getColor(R.color.text_323232, theme))
                    GlideApp.with(image)
                        .load(resources.getDrawable(R.drawable.ic_triangle_black_down_24, theme))
                        .into(image)
                }
            }
            "sort" -> {
                if (category.visibility == View.VISIBLE) {
                    openOrHideDialog("category")
                }
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
                val image = findViewById<ImageView>(R.id.select_sort_icon)
                val title = findViewById<TextView>(R.id.select_sort_name)

                if (sort.visibility == View.GONE) {
                    sort.visibility = View.VISIBLE
                    title.setTextColor(resources.getColor(R.color.red, theme))
                    GlideApp.with(image)
                        .load(resources.getDrawable(R.drawable.ic_triangle_up_red_24, theme))
                        .into(image)
                } else {
                    sort.visibility = View.GONE
                    title.setTextColor(resources.getColor(R.color.text_323232, theme))
                    GlideApp.with(image)
                        .load(resources.getDrawable(R.drawable.ic_triangle_black_down_24, theme))
                        .into(image)
                }
            }
            "filter" -> {
                if (category.visibility == View.VISIBLE) {
                    openOrHideDialog("category")
                }
                if (sort.visibility == View.VISIBLE) {
                    openOrHideDialog("sort")
                }
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                } else {
                    popupWindow.showAsDropDown(findViewById(R.id.my_toolbar))
                    setBackgroundAlpha(0.4f)
                }
            }
        }
    }

    /**
     * 设置背景的透明度
     * @param bgAlpha 背景透明度 0 - 1
     * @author Zhenqing He
     * @createDate 2021/4/19 11:11
     */
    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = this.window.attributes
        lp.alpha = bgAlpha
        if (bgAlpha == 1f) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) //此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        this.window.attributes = lp
    }

    /**
     * 重置 filter attr 选中值
     * @author Zhenqing He
     * @createDate 2021/4/19 16:14
     */
    private fun resetFilterAttr() {
        val data = categoryViewModel.filter.value?.attrList!!
        for (attr in data) {
            for (attrValue in attr.attrValueList) {
                attrValue.isCheck = false
            }
        }
        checkAttrList = mutableListOf()
        attrRecyclerView.adapter?.notifyDataSetChanged()
    }

    /**
     * 提交 filter attr 选中值，进行搜索
     * @author Zhenqing He
     * @createDate 2021/4/19 16:14
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun submitFilterAttr() {
        val data = categoryViewModel.filter.value?.attrList!!
        for (attr in data) {
            var attrValueString = ""
            for (attrValue in attr.attrValueList) {
                if (attrValue.isCheck) {
                    attrValueString =
                        if (attrValueString == "") attrValue.attrValueId.toString() else "${attrValueString},${attrValue.attrValueId}"
                }
            }
            if (attrValueString != "") {
                val temp = mutableMapOf<String, String>()
                temp.put("attrValue", attrValueString)
                temp["attrKey"] = attr.attrKey.toString()
                if (checkAttrList.find { it["attrKey"] == attr.attrKey.toString() } == null) {
                    checkAttrList.add(temp)
                } else {
                    checkAttrList.find { it["attrKey"] == attr.attrKey.toString() }!!
                        .replace("attrValue", attrValueString)
                }

            }
        }
        categoryViewModel.search(categoryId, null, checkAttrList, null, null)
        openOrHideDialog("filter")
    }

    /**
     * 重置 category 过滤
     * @author Zhenqing He
     * @createDate 2021/4/20 9:37
     */
    private fun resetCategory() {
        checkCategory = categoryId
        categoryViewModel.getFilter(categoryId)
        categoryViewModel.search(categoryId, null, null, null, null)
    }

    /**
     * 提交 category 过滤结果
     * @author Zhenqing He
     * @createDate 2021/4/20 9:38
     */
    private fun submitCategory() {
        categoryViewModel.search(checkCategory, null, null, null, null)
        openOrHideDialog("category")
    }

    /**
     * null
     * @param null
     * @return null
     * @author Zhenqing He
     * @createDate 2021/4/20 11:32
     */
//    private fun searchScrollListener() {
//        categoryProductRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (ScrollUtil().isScrollBottom(categoryProductRecyclerView) && !categoryViewModel.searchResult.value?.lastPage!! && flag) {
//                    flag = false
//                    categoryViewModel.search(
//                        checkCategory,
//                        sortCode = checkSortCode,
//                        attrsList = checkAttrList,
//                        pageNumber = categoryViewModel.searchResult.value?.pageNumber!! + 1,
//                        plusSizeFlag = null
//                    )
//                } else {
//
//                }
//            }
//        })
//    }

    private fun search() {
        if (isFromHome) {
            val params = GetUrlParamsUtil().getAllParamsFromUrl(searchUrl)
            categoryId = params.get("categoryId")?.toInt()!!
            val plusSizeFlag = if (params.get("plusSizeFlag") != null) params.get("plusSizeFlag")
                ?.toInt() else null
            val sortCode = if (params.get("sortCode") != null) params.get("sortCode") else null
            categoryViewModel.search(categoryId, sortCode, null, null, plusSizeFlag)
        } else {
            categoryId = GetUrlParamsUtil().getParamsFromUrl(searchUrl, "categoryFid").toInt()
            checkCategory = categoryId
            categoryViewModel.search(categoryId, null, null, null, null)
        }
    }

    companion object {
        fun actionStart(context: Context, searchUrl: String, isFromHome: Boolean) {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("searchUrl", searchUrl)
            intent.putExtra("isFromHome", isFromHome)
            context.startActivity(intent)
        }
    }
}