package com.example.togetherhealth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore



class MainActivity : AppCompatActivity(), View.OnClickListener,OnCompleteListener<AuthResult> {
    lateinit var app: MyApplication
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as MyApplication
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()
        btn_login.setOnClickListener {
            if (txt_email.getText().toString() == "") {
            Toast.makeText(this@MainActivity, "Please type an email", Toast.LENGTH_SHORT).show()
        } else if (txt_pwd.getText().toString() == "") {
            Toast.makeText(this@MainActivity, "Please type a password", Toast.LENGTH_SHORT).show()
        }else {
                app.auth.signInWithEmailAndPassword(
                    txt_email.text.toString(),
                    txt_pwd.text.toString()
                ).addOnCompleteListener(this)
            }
        }
        btn_register.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_register -> {
                val register_view = Intent(this@MainActivity, Register::class.java)
                startActivity(register_view)
            }
        }
    }
    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val a1 = txt_email!!.text.toString().trim { it <= ' ' }
            val b1 = txt_pwd!!.text.toString().trim { it <= ' ' }
            db!!.collection("users").whereEqualTo("Email", a1).whereEqualTo("Password", b1)
                .get()
                .addOnSuccessListener { documents ->
                    var name:String=""
                    for (document in documents) {
                        app.username= document.data.get("Name").toString()

                    }
                   Toast.makeText(
                        this@MainActivity,
                        "Logged In",
                        Toast.LENGTH_SHORT
                    ).show()
                    val home = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(home)

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MainActivity,
                        "Wrong username or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        else {
            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }
}

