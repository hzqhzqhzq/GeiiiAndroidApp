package com.sheiii.app.view.account

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sheiii.app.R

class FeedBackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
    }
    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, FeedBackActivity::class.java)

            context.startActivity(intent)
        }
    }
}