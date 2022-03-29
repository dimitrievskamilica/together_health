package com.example.togetherhealth

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MyApplication: Application() {
    lateinit var auth: FirebaseAuth
     var username =""
    override fun onCreate() {
        super.onCreate()
        auth=FirebaseAuth.getInstance()
    }
}