package com.sheiii.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.R
import com.sheiii.app.adapter.AreaFragmentAdapter
import com.sheiii.app.model.All
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.viewmodel.AddressViewModel

/**
 * @author created by Zhenqing He on  15:18
 * @description
 */
class CityPickerDialog(addressViewModel: AddressViewModel) : DialogFragment() {
    private val addressViewModel = addressViewModel
    private var height = 0
    lateinit var tab: TabLayout
    private lateinit var area: ViewPager2
    private val allList: MutableList<AreaListModel> = mutableListOf()
    private lateinit var searchLayout: TextInputLayout
    private lateinit var searchEditText: TextInputEditText

    // allList的镜像，用于恢复allList完整数据
    private val tempAllList: MutableList<AreaListModel> = mutableListOf()

    // 暂存 addressviewmode 中的 arealist 数据
    private lateinit var areaList: AreaListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Address_Area_Dialog)
    }

    // 启动的时候 全屏
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        dialog?.window?.setBackgroundDrawable(ColorDrawable(0xffffff))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setDimAmount(1f)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_citypicker, container, false)

        tab = root.findViewById(R.id.area_picker_tab)
        area = root.findViewById(R.id.area_viewpager)

        searchLayout = root.findViewById(R.id.area_search)
        searchEditText = root.findViewById(R.id.area_textInputEdit)

        addAreaList()

        realTimeSearch()

        return root
    }

    override fun onResume() {
        super.onResume()
        // 左右滑动监听，当出现滑动的时候，更改 arealist 为当前 field 数据
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                for ((index, item) in allList.withIndex()) {
                    if (item.field == tab?.tag) {
                        areaList = tempAllList[index].clone()
//                        allList[index] = areaList.clone()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    /**
     * 添加地址数据
     * @author Zhenqing He
     * @createDate 2021/4/22 10:18
     */
    fun addAreaList() {
        areaList = addressViewModel.areaList.value!!.clone()
        var position = 0

        // 判断是否存在, 存在则替换，不存在则添加
        var isExistance = false
        for ((p, item) in allList.withIndex()) {
            if (item.field == areaList.field) {
                allList[p] = areaList.clone()
                tempAllList[p] = areaList.clone()
                position = p
                isExistance = true
                break
            }
        }

        if (!isExistance) {
            allList.add(areaList.clone())
            tempAllList.add(areaList.clone())
            position = allList.size - 1
        }
        initData(position, isExistance)
    }

    /**
     * 初始化地址数据
     * @param showPosition 数据在allList中的index
     * @param isExistance 数据是否存在，false为不存在，则需执行添加tab；true为存在，则执行更新viewpager操作
     * @author Zhenqing He
     * @createDate 2021/4/22 10:13
     */
    private fun initData(showPosition: Int, isExistance: Boolean) {
        var textTitle: MutableMap<String, String> = mutableMapOf()

        // 设置tablayout 的 title, 如果inputValue为空，则title提示，请输入
        for ((index, item) in allList.withIndex()) {
            for (field in addressViewModel.addressFieldList.value!!) {

                if (item.field == field.fieldName) {
                    textTitle[item.field] = if (field.inputValue == "") {
                        getString(R.string.plz_select)
                    } else {
                        field.inputValue
                    }
                }
            }
        }

        area.isSaveEnabled = false
        // 配置 tablayout 和 viewpager 之前的联动
        area.adapter = AreaFragmentAdapter(childFragmentManager, lifecycle, allList)
//        if (!isExistance) {

        TabLayoutMediator(tab, area) { tab, position ->
            tab.text = textTitle[allList[position].field]
            tab.tag = allList[position].field
        }.attach()
        tab.getTabAt(showPosition)?.select()
        area.currentItem = showPosition
//        } else {
//            area.adapter?.notifyItemChanged(showPosition)
//            tab.getTabAt(showPosition)?.select()
//            area.currentItem = showPosition
//            for (i in 0 until tab.tabCount) {
//                for ()
//                tab.getTabAt(i).text =
//            }
//        }
    }

    /**
     * 实时搜索
     * @author Zhenqing He
     * @createDate 2021/4/22 10:14
     */
    private fun realTimeSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 如果输入不为空，则进行搜索判断，如果为空，则返回所有area信息
                if (s?.isNotEmpty() == true) {
                    // 用来存储搜索结果
                    val searchResultList =
                        AreaListModel(
                            allList = mutableListOf(),
                            allTitle = areaList.allTitle,
                            title = areaList.title,
                            field = areaList.field
                        )
                    // 遍历 arealist 找到相匹配的地址，放入存储的搜索结果
                    for (list in areaList.allList) {
                        val allResult = All(firstLetter = list.firstLetter, list = mutableListOf())
                        for ((index2, item) in list.list.withIndex()) {
                            if (item.name.contains(s)) {
                                allResult.list.add(item)
                            }
                        }
                        if (allResult.list.size > 0) {
                            searchResultList.allList.add(allResult)
                        }
                    }
                    // 更新搜索结果
                    for ((index, item) in allList.withIndex()) {
                        if (item.field == searchResultList.field) {
                            allList[index] = searchResultList.clone()
                            initData(index, true)
                            break
                        }
                    }
                } else {
                    for ((index, item) in allList.withIndex()) {
                        if (item.field == areaList.field) {
                            allList[index] = areaList.clone()
                            initData(index, true)
                            break
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}