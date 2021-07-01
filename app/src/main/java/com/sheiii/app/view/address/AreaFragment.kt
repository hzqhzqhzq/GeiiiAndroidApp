package com.sheiii.app.view.address

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sheiii.app.R
import com.sheiii.app.adapter.AreaAdapter
import com.sheiii.app.adapter.CommonAdapter
import com.sheiii.app.model.AddressFieldModel
import com.sheiii.app.model.AreaListModel
import com.sheiii.app.model.SubList
import com.sheiii.app.ui.QuickLocationBar
import com.sheiii.app.viewmodel.AddressViewModel
import java.text.FieldPosition
import java.util.*

class AreaFragment(data: AreaListModel, position: Int) : Fragment() {
    private val data = data
    private lateinit var areaRecycler: RecyclerView
    private lateinit var areaAdapter: AreaAdapter
    private val position = position
    private val addressViewModel: AddressViewModel by activityViewModels()
    private lateinit var quickLocationBar: QuickLocationBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_area, container, false)
        areaRecycler = root.findViewById(R.id.area_all_recycler)
        quickLocationBar = root.findViewById(R.id.qlb_letter)

        // 设置 字母 导航
        val letterList: ArrayList<String> = ArrayList()
        for (item in data.allList) {
            letterList.add(item.firstLetter)
        }
        quickLocationBar.setCharacters(letterList, false)
        quickLocationBar.setOnTouchLitterChangedListener(object : QuickLocationBar.OnTouchLetterChangedListener {
            override fun touchLetterChanged(s: String?) {
                var position =  0
                for ((index, item) in data.allList.withIndex()) {
                    if (item.firstLetter == s) {
                        position = index
                    }
                }
                val layoutManager = areaRecycler.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(position, 0)
            }
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        areaAdapter = AreaAdapter(data)

        areaRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
            adapter = areaAdapter
        }

        areaAdapter.areaSubItemClick(object : AreaAdapter.ClickInterface {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun areaSubItemClick(view: View, position: Int, data: SubList, type: String?) {
                var myType = type
                /**
                 * 用选中的值替换当前输入的值
                 * @if field为country的时候
                 * @else field为province或者city的时候
                 */
                if (myType == null) {
                    myType = "country"
                    addressViewModel.getAddressFieldList(data.name)
                }

                addressViewModel.addressInput.value?.replace(
                    myType,
                    data.name
                )

                for (field in addressViewModel.addressFieldList.value!!) {
                    if (field.fieldName == myType) {
                        field.inputValue = data.name
                    }
                }

                // 点击监听时，如果是最后一层，则执行回调，如果还有子层，则继续显示
                if (myType != "country" && data.hasChilds) {
//                    addressViewModel.nameList.value = "${nameList}@${data.name}"
                    addressViewModel.getAreaList(data.nextType)
                } else {
                    addressViewModel.cityPickerCallBack.value = true
                }
            }
        })
    }

    private fun setTitle() {

    }

    fun update() {
        areaAdapter.notifyDataSetChanged()
    }
}