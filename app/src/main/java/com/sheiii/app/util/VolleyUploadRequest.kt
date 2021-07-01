package com.sheiii.app.util

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.sheiii.app.R
import com.sheiii.app.constants.BASE_URL
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.StringBuilder
import java.nio.charset.Charset

/**
 * @author created by Zhenqing He on  09:12
 * @description
 */
class VolleyUploadRequest(
    url: String,              // 地址
    val fileName: String,     // 文件名
    val file: File,           // 文件
    val fileMime: String,     // 文件类型
    val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(Method.POST, BASE_URL + url, errorListener) {

    companion object {
        private const val boundary = "*****"
    }

    init {
        setShouldCache(false)
        retryPolicy = DefaultRetryPolicy(
            6000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            val str = String(
                response?.data ?: byteArrayOf(),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers ?: emptyMap()))
            )
            Response.success(str, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: String) {
        listener.onResponse(response)
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers: MutableMap<String, String> = mutableMapOf()
        headers.putAll(super.getHeaders())
        headers["siteCode"] = MyApplication.getInstance().getString(R.string.countryCode)
//            headers["Connection"] = "close"

        /* 设置Cookie注意builder最后一个不需要加; */
        val baseConfig = MyApplication.getBaseConfig()
        val builder: StringBuilder = StringBuilder()
        if (!baseConfig?.memberId.isNullOrEmpty()) {
            builder.append("wt_member_id=${baseConfig?.memberId};")
        } else {
            builder.append("wt_member_id=;")
        }
        if (!baseConfig?.uuid.isNullOrEmpty()) {
            builder.append("wt_uuid=${baseConfig?.uuid};")
        } else {
            builder.append("wt_uuid=;")
        }
        if (!baseConfig?.sessionId.isNullOrEmpty()) {
            builder.append("wt_session_id=${baseConfig?.sessionId};")
        } else {
            builder.append("wt_session_id=;")
        }
        builder.append("wt_currency=${baseConfig?.currencyCode};")
        builder.append("wt_language=${baseConfig?.languageCode};")
        if (headers.containsKey("Cookie")) {
            builder.append("; ");
            builder.append(headers["Cookie"])
        }
        Log.d("Cookies", builder.toString())
        headers["Cookie"] = builder.toString()

        return headers
    }

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val buffer = StringBuffer()
        /** 第一行 */
        // --boundary
        buffer.append("--$boundary\r\n")
        /** 第二行 */
        // Content-Disposition: form-data; name="fileName"; filename="xxx.png";
        buffer.append("Content-Disposition: form-data; ")
        buffer.append("name=\"$fileName\"; ")
        buffer.append("filename=\"${file.name}\";\r\n")
        /** 第三行 */
        // Content-Type: image/png
        buffer.append("Content-Type: $fileMime;\r\n")


        /** 第四行 */
        // \r\n
        buffer.append("\r\n")
        // 写入
        bos.write(buffer.toString().toByteArray(Charset.forName("utf-8")))
        /** 第五行 */
        // 文件二进制数据
        val fis = FileInputStream(file)
        val bytes = ByteArray(1024)
        var length = fis.read(bytes)
        while (length >= 0) {
            bos.write(bytes, 0, length)
            length = fis.read(bytes)
        }
        bos.write("\r\n".toByteArray(Charset.forName("utf-8")))
        fis.close()
        /** 第六行 */
        // 结尾
        bos.write("--$boundary--\r\n".toByteArray(Charset.forName("utf-8")))
        return bos.toByteArray()
    }
}