package com.alfiyah.stamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class JoinActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener(this)

        val signupBtn: Button = findViewById(R.id.signup_btn)
        signupBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.login_btn ->{
                val loginIntent  = Intent(this@JoinActivity, LoginActivity::class.java)
                startActivity(loginIntent)

            }

            R.id.signup_btn ->{
                val signupIntent  = Intent(this@JoinActivity, RegisterActivity::class.java)
                startActivity(signupIntent)

            }
        }
    }
}