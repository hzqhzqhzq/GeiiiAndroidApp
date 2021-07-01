package com.sheiii.app.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.R
import com.sheiii.app.adapter.AreaFragmentAdapter
import com.sheiii.app.model.All
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.view.address.AreaFragment
import com.sheiii.app.viewmodel.AddressViewModel

/**
 * @author created by Zhenqing He on  13:25
 * @description
 */
class CountryPickerDialog(addressViewModel: AddressViewModel) : DialogFragment() {
    private val addressViewModel = addressViewModel
    private val allList: MutableList<AreaListModel> = mutableListOf()
    private lateinit var searchLayout: TextInputLayout
    private lateinit var searchEditText: TextInputEditText
    private lateinit var area: ViewPager2

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
        val root = inflater.inflate(R.layout.dialog_country_picker, container, false)

        searchLayout = root.findViewById(R.id.area_search)
        searchEditText = root.findViewById(R.id.area_textInputEdit)

        area = root.findViewById(R.id.area_viewpager)

//        realTimeSearch()

        return root
    }

    override fun onResume() {
        super.onResume()
        addressViewModel.countryList.observe(this, Observer {
            if (addressViewModel.countryList.value != null) {
                allList.add(addressViewModel.countryList.value!!)
                initData()
            }
        })
    }


    /**
     * 初始化地址数据
     * @param showPosition 数据在allList中的index
     * @param isExistance 数据是否存在，false为不存在，则需执行添加tab；true为存在，则执行更新viewpager操作
     * @author Zhenqing He
     * @createDate 2021/4/22 10:13
     */
    private fun initData() {
        area.isSaveEnabled = false
        area.adapter = AreaFragmentAdapter(childFragmentManager, lifecycle, allList)
    }

    /**
     * 实时搜索
     * @author Zhenqing He
     * @createDate 2021/4/22 10:14
     */
//    private fun realTimeSearch() {
//        searchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // 如果输入不为空，则进行搜索判断，如果为空，则返回所有area信息
//                if (s?.isNotEmpty() == true) {
//                    // 用来存储搜索结果
//                    val searchResultList =
//                        AreaListModel(
//                            allList = mutableListOf(),
//                            allTitle = areaList.allTitle,
//                            title = areaList.title,
//                            field = areaList.field
//                        )
//                    // 遍历 arealist 找到相匹配的地址，放入存储的搜索结果
//                    for (list in areaList.allList) {
//                        val allResult = All(firstLetter = list.firstLetter, list = mutableListOf())
//                        for ((index2, item) in list.list.withIndex()) {
//                            if (item.name.contains(s)) {
//                                allResult.list.add(item)
//                            }
//                        }
//                        if (allResult.list.size > 0) {
//                            searchResultList.allList.add(allResult)
//                        }
//                    }
//                    // 更新搜索结果
//                    for ((index, item) in allList.withIndex()) {
//                        if (item.field == searchResultList.field) {
//                            allList[index] = searchResultList.clone()
//                            initData()
//                            break
//                        }
//                    }
//                } else {
//                    for ((index, item) in allList.withIndex()) {
//                        if (item.field == areaList.field) {
//                            allList[index] = areaList.clone()
//                            initData()
//                            break
//                        }
//                    }
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//
//        })
//    }
}