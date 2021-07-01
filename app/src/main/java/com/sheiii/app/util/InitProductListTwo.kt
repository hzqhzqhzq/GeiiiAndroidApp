package com.sheiii.app.util

import com.sheiii.app.model.ProductModel

/**
 * @author created by Zhenqing He on  18:23
 * @description
 */
class InitProductListTwo {
    fun initList(old: MutableList<ProductModel>): MutableList<MutableList<ProductModel>> {
        val result = mutableListOf<MutableList<ProductModel>>()

        if (old.size != 0) {
            for (i in 0 until old.size / 2) {
//                Log.d("all-size", old.size.toString())
//                Log.d("all-size/2", (old.size / 2).toString())
//                Log.d("i*2", (i * 2).toString())
                val temp = mutableListOf<ProductModel>()
                temp.add(old[i * 2])
                if ((i * 2 + 1) <= old.size) {
                    temp.add(old[i * 2 + 1])
                }
                result.add(temp)
            }
        }

        return result
    }
}