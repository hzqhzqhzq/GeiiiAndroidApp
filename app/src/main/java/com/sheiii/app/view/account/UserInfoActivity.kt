package com.sheiii.app.view.account

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sheiii.app.R
import com.sheiii.app.databinding.ActivityUserInfoBinding
import com.sheiii.app.model.Profile
import com.sheiii.app.util.MyApplication
import com.sheiii.app.util.PerfectClickListener
import com.sheiii.app.util.RealPathFromUriUtils
import com.sheiii.app.viewmodel.AccountViewModel
import com.soundcloud.android.crop.Crop
import com.zyyoona7.picker.DatePickerView
import com.zyyoona7.picker.base.BaseDatePickerView
import com.zyyoona7.picker.listener.OnDateSelectedListener
import com.zyyoona7.wheel.WheelView
import org.w3c.dom.Text
import java.io.File
import java.security.Key
import java.util.*


class UserInfoActivity : AppCompatActivity() {
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var outputUri: Uri
    private lateinit var changeGenderDialog: BottomSheetDialog
    private lateinit var changeBirthDialog: BottomSheetDialog

    private lateinit var male: String
    private lateinit var female: String

    // ????????????
    private lateinit var userIcon: ImageView
    private lateinit var userName: TextInputLayout
    private lateinit var userNameTxt: TextInputEditText
    private lateinit var userGenderText: TextView
    private lateinit var userGender: ConstraintLayout
    private lateinit var userBirth: ConstraintLayout
    private lateinit var userBirthText: TextView
    private lateinit var userPhone: TextInputLayout
    private lateinit var userPhoneTxt: TextInputEditText
    private lateinit var userEmail: TextInputLayout
    private lateinit var userEmailTxt: TextInputEditText

    private lateinit var saleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info)
        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        accountViewModel.getMine()

        male = resources.getString(R.string.man)
        female = resources.getString(R.string.woman)

        saleButton = findViewById(R.id.user_info_save)

        accountViewModel.mineInfo.observe(this, Observer {
            if (accountViewModel.mineInfo.value != null) {
                binding.setVariable(BR.user_info, accountViewModel.mineInfo.value?.profile!!)
                // ?????????????????????
                initUserInfo(accountViewModel.mineInfo.value?.profile!!)
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initUserInfo(data: Profile) {
        userIcon = binding.userInfoIcon
        userName = binding.userInfoName
        userNameTxt = binding.userInfoNameText
        userGenderText = binding.userInfoGenderText
        userGender = binding.userInfoGender
        userBirth = binding.userInfoBirth
        userBirthText = binding.userInfoBirthText
        userPhone = binding.userInfoPhone
        userEmail = binding.userInfoEmail
        userPhoneTxt = binding.userInfoPhoneText
        userEmailTxt = binding.userInfoEmailText

        // ????????????
        Glide.with(userIcon).load(data.picture).into(userIcon)

        userName.editText?.setText(data.nickName)

        data.sex.let { it ->
            if (it == 0) {
                userGenderText.text = male
            } else {
                userGenderText.text = female
            }
        }

        data.birthday.let { it ->
            userBirthText.text = it
        }

        data.phone.let { it -> {
            userPhoneTxt.setText(it)
        } }

        // ??????????????????
        userIcon.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                selectImage()
            }
        })
        // ????????????
        userGender.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeGender()
            }
        })
        // ??????????????????
        userBirth.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeBirth()
            }
        })

        saveUserInfo()

        inputListener()
    }

    private fun selectImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, 111)
    }

    private fun changeGender() {
        var gender = accountViewModel.mineInfo.value?.profile?.sex
        val view: View = layoutInflater.inflate(R.layout.dialog_gender_change, null)
        val wheelview: WheelView<String> = view.findViewById(R.id.wheelview)
        changeGenderDialog = BottomSheetDialog(this)
        changeGenderDialog.setContentView(view)

        // ?????????????????????
        val list = mutableListOf<String>(male, female)
        wheelview.data = list
        wheelview.textSize = 48f
        wheelview.setSelectedItemPosition(gender!!, true)

        // ??????????????????
        wheelview.setOnItemSelectedListener { wheelView, data, position ->
            gender = if (data == male) 0 else 1
        }

        // ????????????
        changeGenderDialog.findViewById<TextView>(R.id.cansel)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeGenderDialog.hide()
            }
        })

        // ????????????
        changeGenderDialog.findViewById<TextView>(R.id.confirm)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeGenderDialog.hide()
                accountViewModel.mineInfo.value?.profile?.sex = gender!!
                userGenderText.text =
                    if (accountViewModel.mineInfo.value?.profile?.sex == 0) male else female
            }
        })

        changeGenderDialog.show()
    }

    private fun changeBirth() {
        var birth = accountViewModel.mineInfo.value?.profile?.birthday
        val view: View = layoutInflater.inflate(R.layout.dialog_birth, null)
        val defaultDpv: DatePickerView = view.findViewById(R.id.dpv_default)
        defaultDpv.setTextSize(24f, true)
        defaultDpv.setLabelTextSize(20f)
        changeBirthDialog = BottomSheetDialog(this)
        changeBirthDialog.setContentView(view)

        // ??????????????????date
        if (accountViewModel.mineInfo.value?.profile?.birthday != null) {
            val dateList = accountViewModel.mineInfo.value?.profile?.birthday!!.split("-")
            defaultDpv.selectedYear = dateList[0].toInt()
            defaultDpv.selectedMonth = dateList[1].toInt()
            defaultDpv.selectedDay = dateList[2].toInt()
        }

        // ??????????????????
        defaultDpv.setOnDateSelectedListener { datePickerView, year, month, day, date ->
            Toast.makeText(applicationContext, "??????????????????$year-$month-$day", Toast.LENGTH_SHORT)
                .show()
            birth = "$year-$month-$day"
        }

        // ????????????
        changeBirthDialog.findViewById<TextView>(R.id.cansel)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeBirthDialog.hide()
            }
        })

        // ????????????
        changeBirthDialog.findViewById<TextView>(R.id.confirm)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                changeBirthDialog.hide()
                accountViewModel.mineInfo.value?.profile?.birthday = birth.toString()
                userBirthText.text = accountViewModel.mineInfo.value?.profile?.birthday
            }
        })

        changeBirthDialog.show()
    }

    private fun saveUserInfo() {
        saleButton.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                if (checkInput()) {
                    accountViewModel.updateUserInfo()
                } else {}
            }
        })

        accountViewModel.updateProfile.observe(this, Observer {
            if (accountViewModel.updateProfile.value != null) {
                MyApplication.getLoading().hideLoading()
                if (accountViewModel.updateProfile.value == true) {
                    finish()
                } else {
                    Toast.makeText(applicationContext, accountViewModel.updateProfileMsg.value, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    // ????????????
    private fun checkInput(): Boolean {
        var result = true
        val input = accountViewModel.mineInfo.value?.profile

        when {
            input?.birthday == null -> {
                Toast.makeText(applicationContext, "???????????????", Toast.LENGTH_SHORT)
                    .show()
                result = false
            }
            input.phone == "" -> {
                Toast.makeText(applicationContext, "?????????????????????", Toast.LENGTH_SHORT)
                    .show()
                result = false
            }
            input.nickName == "" -> {
                Toast.makeText(applicationContext, "??????????????????", Toast.LENGTH_SHORT)
                    .show()
                result = false
            }
        }

        return result
    }

    // ????????????
    private fun inputListener() {
        userNameTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.d("afterTextChanged", userNameTxt.text.toString())
                accountViewModel.mineInfo.value?.profile?.nickName = userNameTxt.text.toString()
            }
        })

        userPhoneTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.d("afterTextChanged", userNameTxt.text.toString())
                Log.d("userPhoneTxt", userPhoneTxt.text.toString())
                accountViewModel.mineInfo.value?.profile?.phone = userPhoneTxt.text.toString()
            }
        })

        userEmailTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
//                Log.d("afterTextChanged", userNameTxt.text.toString())
                accountViewModel.mineInfo.value?.profile?.email = userEmailTxt.text.toString()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            111 -> {
                try {
                    val imageUri = data?.data
                    outputUri = Uri.fromFile(File(this.cacheDir, "cropped"))
                    // ??????????????????
                    Crop.of(imageUri, outputUri).asSquare().start(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Crop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    // ????????????uri??????
                    val realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, outputUri)
                    Glide.with(userIcon).load(Crop.getOutput(data)).apply(
                        RequestOptions.bitmapTransform(
                            CircleCrop()
                        )
                    ).into(userIcon)
                    accountViewModel.uploadAvatar(File(realPathFromUri))
                } else {
                    Log.d("REQUEST_CROP", "errrrrrrrrrrr")
                }
            }
        }
    }

//    private fun permissionCheck() {
//        val permissions = arrayOf<String>(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA
//        )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // ?????????????????????????????????
//
//            for (permission in permissions) {
//                //  GRANTED---??????  DINIED---??????
//                if (ContextCompat.checkSelfPermission(
//                        applicationContext,
//                        permission
//                    ) == PackageManager.PERMISSION_DENIED
//                ) {
//                    ActivityCompat.requestPermissions(this, permissions, 10001);
//                }
//            }
//        }
//    }


    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, UserInfoActivity::class.java)

            context.startActivity(intent)
        }
    }

}