package com.sheiii.app.view.account

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.cropbitmap.LikeQQCropView
import com.sheiii.app.R

class ImageCutActivity : AppCompatActivity() {
    private lateinit var likeQQCropView: LikeQQCropView

    private val REQUEST_CODE = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_cut)

        val pathName = intent.getStringExtra("pathName")



        likeQQCropView = findViewById(R.id.likeView)

        likeQQCropView.setBitmapForWidth(pathName, Resources.getSystem().displayMetrics.widthPixels)
    }

    companion object {
        fun actionStart(context: Context, pathName: String) {
            val intent = Intent(context, ImageCutActivity::class.java)
            intent.putExtra("pathName", pathName)
            context.startActivity(intent)
        }
    }
}