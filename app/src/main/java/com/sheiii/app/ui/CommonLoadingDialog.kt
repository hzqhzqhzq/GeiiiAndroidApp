package com.sheiii.app.ui

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.widget.ProgressBar
import com.sheiii.app.R

/**
 * @author created by Zhenqing He on  10:23
 * @description
 */
class CommonLoadingDialog private constructor(context: Context, theme: Int) : Dialog(context, theme) {
    companion object {
        private lateinit var mDialog: CommonLoadingDialog
        private lateinit var animDrawable: AnimationDrawable
        fun buildDialog(context: Context): CommonLoadingDialog {
            //根据指定主题样式创建Dialog
            mDialog = CommonLoadingDialog(context, R.style.commonDialogStyle)
            //设置Dialog的布局
            mDialog.setContentView(R.layout.common_loading)
            //点击或按返回键时消失
            mDialog.setCancelable(true)
            //点击对话框外的部分不消失.
            mDialog.setCanceledOnTouchOutside(false)
            //设置对话框居中
            mDialog.window?.attributes?.gravity = Gravity.CENTER
            val lp = mDialog.window?.attributes
            lp?.dimAmount = 0.2f
            //设置属性
            mDialog.window?.attributes = lp
            //获取对话框中的动画
//            val animView = mDialog.findViewById<ProgressBar>(R.id.loadingIv)
//            animDrawable = animView.background as AnimationDrawable
            return mDialog
        }
    }

    //显示加载框
    fun showLoading() {
        super.show()
//        animDrawable?.start()
    }

    //关闭加载框
    fun hideLoading() {
        super.dismiss()
//        animDrawable?.stop()
    }
}