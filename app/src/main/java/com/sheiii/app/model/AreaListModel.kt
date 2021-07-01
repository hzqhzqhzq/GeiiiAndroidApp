package com.sheiii.app.model

/**
 * @author created by Zhenqing He on  13:57
 * @description
 */
data class AreaListModel(
    var allList: MutableList<All>,
    val allTitle: String,
    val title: String,
    var field: String,
    var hasChilds: Boolean = true
) : Cloneable {
    public override fun clone(): AreaListModel {
        val it = super.clone() as AreaListModel
        val temp = mutableListOf<All>()
        for (item in it.allList) {
            temp.add(item.clone())
        }
        it.allList = temp
        return it
    }
}

data class All(
    var firstLetter: String,
    var list: MutableList<SubList>
) : Cloneable {
    public override fun clone(): All {
        val it = super.clone() as All
        val temp = mutableListOf<SubList>()
        for (item in list) {
            temp.add(item.clone())
        }
        it.list = temp
        return it
    }
}

data class SubList(
    val hasChilds: Boolean,
    val id: Int,
    val name: String,
    val nameEn: String,
    val nextType: String,
    val phoneCode: String
) : Cloneable {
    public override fun clone(): SubList {
        return super.clone() as SubList
    }
}