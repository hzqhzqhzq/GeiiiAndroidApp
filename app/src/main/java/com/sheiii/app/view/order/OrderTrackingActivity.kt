package com.sheiii.app.view.order

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sheiii.app.R

class OrderTrackingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, OrderTrackingActivity::class.java)
//            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }
}