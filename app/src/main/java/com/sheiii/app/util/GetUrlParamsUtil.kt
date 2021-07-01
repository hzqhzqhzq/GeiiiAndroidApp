package com.sheiii.app.util

import android.util.Log
import java.util.regex.Pattern

/**
 * @author created by Zhenqing He on  10:11
 * @description
 */
class GetUrlParamsUtil {
    fun getParamsFromUrl(url: String, params: String): String {
        val pattern = "${params}=([^&]*)"
        val matchResult = Regex(pattern).findAll(url, 0)
        var result = ""
        for (item in matchResult) {
            result = item.value.split("=")[1]
        }

        return result
    }

    fun getAllParamsFromUrl(url: String): MutableMap<String, String> {
        val pattern = "([A-z]*)=([^&]*)"
        val matchResult = Regex(pattern).findAll(url, 0)
        val result = mutableMapOf<String, String>()
        for (item in matchResult) {
            result.put(item.value.split("=")[0], item.value.split("=")[1])
        }
        return result
    }
}