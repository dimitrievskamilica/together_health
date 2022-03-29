package com.example.togetherhealth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var app: MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        app = application as MyApplication
        helloUser.text= "Hello "+app.username
        SignOut.setOnClickListener(this )
    }
    override fun onClick(v: View) {
        app.auth.signOut()
        Toast.makeText(this@HomeActivity, "Signed out", Toast.LENGTH_SHORT).show()
        finish()
    }
}