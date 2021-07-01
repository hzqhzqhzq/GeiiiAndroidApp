package com.sheiii.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.sheiii.app.util.PerfectClickListener

/**
 * @author created by Zhenqing He on  15:49
 * @description
 */
class WelcomeActivity : Activity() {
    private lateinit var ads: TextView
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        ads = findViewById(R.id.tv_ads)

        countDownTimer = CountDownTimer(10000, 1000, ads, this)
        countDownTimer.start()

        ads.setOnClickListener( object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                countDownTimer.cancel()
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    class CountDownTimer(millisInFuture: Long, countDownInterval: Long, ads: TextView, context: Context) : android.os.CountDownTimer(millisInFuture, countDownInterval) {
        private val ads = ads
        private val context: Activity = context as Activity
        override fun onTick(millisUntilFinished: Long) {
            ads.text = "跳过广告" + (millisUntilFinished / 1000) + "秒"
        }

        override fun onFinish() {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            context.finish()
        }
    }
}