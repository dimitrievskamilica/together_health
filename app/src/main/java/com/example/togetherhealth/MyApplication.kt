package com.example.togetherhealth

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User

class MyApplication: Application() {
    lateinit var auth: FirebaseAuth
     var username =""
     var userID =""
    lateinit var physicalActivities:MutableList<PhysicalActivity>
    lateinit var competitions:MutableList<Competition>
    lateinit var users:MutableList<Userr>
    lateinit var msgs:MutableList<String>
    override fun onCreate() {
        super.onCreate()
        auth=FirebaseAuth.getInstance()
        physicalActivities = mutableListOf<PhysicalActivity>()
        competitions = mutableListOf<Competition>()
        users= mutableListOf<Userr>()
        msgs= mutableListOf()
    }
}