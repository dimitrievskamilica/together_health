package com.example.togetherhealth

import android.content.Intent
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
        helloUser.text= app.username
        SignOut.setOnClickListener(this )
    }
    override fun onClick(v: View) {
        app.auth.signOut()
        app.username=""
        app.userID=""
        app.physicalActivities.clear()
        Toast.makeText(this@HomeActivity, "Signed out", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun addActivity(view: android.view.View) {
        val open_activities = Intent(this@HomeActivity, PhysicalActivityPage::class.java)
        startActivity(open_activities)
    }

    fun openCompetitions(view: android.view.View) {
        val open_competitions = Intent(this@HomeActivity, CompetitionsActivity::class.java)
        startActivity(open_competitions)
    }

    fun openMsgs(view: android.view.View) {
        val open_activities = Intent(this@HomeActivity, MsgBox::class.java)
        startActivity(open_activities)
    }
}