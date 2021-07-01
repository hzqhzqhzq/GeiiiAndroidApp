package com.sheiii.app.view.four

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FourViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is four Fragment"
    }
    val text: LiveData<String> = _text
}