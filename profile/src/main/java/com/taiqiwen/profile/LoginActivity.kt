package com.taiqiwen.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.taiqiwen.base_framework.ui.LoadingDialog
import com.test.account_api.AccountServiceUtil
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "登录失败，请检查您的用户名或者密码填写是否正确", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        loadingDialog?.dismiss()
        super.onDestroy()
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }

    }
}