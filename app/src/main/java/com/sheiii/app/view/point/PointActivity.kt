package com.sheiii.app.view.point

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sheiii.app.R

class PointActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point)
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, PointActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }
    }
}