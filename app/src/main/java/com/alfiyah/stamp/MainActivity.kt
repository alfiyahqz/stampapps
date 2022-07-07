package com.alfiyah.stamp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val joinBtn: Button = findViewById(R.id.join_btn)
        joinBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id){
            R.id.join_btn ->{
                val joinIntent  = Intent(this@MainActivity, JoinActivity::class.java)
                startActivity(joinIntent)
            }
        }
    }
}