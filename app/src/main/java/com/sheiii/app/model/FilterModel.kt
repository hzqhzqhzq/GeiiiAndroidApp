package com.sheiii.app.model

data class FilterModel(
    val attrList: List<Attr>,
    val categoryList: List<Category>,
    val selectCategoryId: Int,
    val selectCategoryLevel: Int,
    val selectCategoryName: String,
    val selectParentCategory: List<SelectParentCategory>,
    var selectSortCode: String,
    var selectSortName: String,
    val sortList: List<Sort>
)

data class Attr(
    val attrKey: Int,
    val attrNameAlias: String,
    val attrValueList: List<AttrValue>
)

data class Category(
    val categoryLevel: Int,
    val id: Int,
    val title: String
)

data class SelectParentCategory(
    val categoryId: Int,
    val categoryLevel: Int,
    val parentCategoryId: Int,
    val title: String
)

data class Sort(
    val code: String,
    val title: String
)

data class AttrValue(
    val attrValueAlias: String,
    val attrValueId: Int,
    var isCheck: Boolean = false
)