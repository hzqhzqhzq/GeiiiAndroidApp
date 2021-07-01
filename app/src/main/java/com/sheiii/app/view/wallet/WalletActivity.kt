package com.sheiii.app.view.wallet

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sheiii.app.R

class WalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
    }

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, WalletActivity::class.java)
//            intent.putExtra("searchUrl", searchUrl)
            context.startActivity(intent)
        }
    }
}