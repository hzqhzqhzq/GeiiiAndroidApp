package com.sheiii.app.model

data class CategoryTop(
    val id: Int,
    val title: String
)

data class CategorySub(
    val childs: List<Child>,
    val h5Url: String,
    val id: Int,
    val image: String,
    val title: String
)

data class Child(
    val h5Url: String,
    val id: Int,
    val image: String,
    val title: String
)


