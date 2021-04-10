package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.taiqiwen.base_framework.EventBusWrapper
import com.taiqiwen.base_framework.LocalStorageHelper
import com.taiqiwen.base_framework.ToastHelper
import com.taiqiwen.base_framework.ui.LoadingDialog
import com.taiqiwen.profile_api.event.LoginSuccessEvent
import com.test.account_api.AccountServiceUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        EventBusWrapper.register(this)
        loginBtn = findViewById(R.id.btn_login)
        loginBtn.setOnClickListener {
            loadingDialog = LoadingDialog(this).apply {
                show()
            }
            val userName = findViewById<EditText>(R.id.et_userName).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()
             AccountServiceUtil.getSerVice().checkLogin(userName, password) { result ->
               loadingDialog?.dismiss()
                if (result) {
                    ToastHelper.showToast("登录成功")
                    LocalStorageHelper.saveUserInfo(this, AccountServiceUtil.getSerVice().getCurUser())
                    EventBusWrapper.post(LoginSuccessEvent())
                    finish()
                } else {
                    ToastHelper.showToast("登录失败，请检查您的用户名或者密码填写是否正确")
                }
            }
        }
    }

    override fun onDestroy() {
        loadingDialog?.dismiss()
        EventBusWrapper.unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logInSuccessEvent(event: LoginSuccessEvent) { }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

    }
}