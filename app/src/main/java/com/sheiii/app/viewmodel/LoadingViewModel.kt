package com.sheiii.app.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author created by Zhenqing He on  15:56
 * @description
 */
class LoadingViewModel : ViewModel() {
    var isLoading: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    var homeTabChildLoading = MutableLiveData(View.VISIBLE)
}