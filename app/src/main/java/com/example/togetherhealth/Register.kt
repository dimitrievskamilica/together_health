package com.example.togetherhealth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class Register : AppCompatActivity() ,OnCompleteListener<AuthResult> {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseFirestore: FirebaseFirestore? = null
    var ref: DocumentReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseFirestore = FirebaseFirestore.getInstance()
        ref = firebaseFirestore!!.collection("users").document()
        btn_add.setOnClickListener {
            if (txt_name.getText().toString() == "") {
                Toast.makeText(this@Register, "Please type a username", Toast.LENGTH_SHORT).show()
            } else if (email.getText().toString() == "") {
                Toast.makeText(this@Register, "Please type an email", Toast.LENGTH_SHORT).show()
            } else if (pwd.getText().toString() == "") {
                Toast.makeText(this@Register, "Please type a password", Toast.LENGTH_SHORT).show()
            } else if (confirmPass.getText().toString() != pwd.getText().toString()) {
                Toast.makeText(this@Register, "Password mismatch", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(
                    email.text.toString(),
                    pwd.text.toString(),

                    ).addOnCompleteListener(this)
            }
        }

    }

    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {

                ref!!.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        Toast.makeText(
                            this@Register,
                            "Sorry,this user exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val reg_entry: MutableMap<String, Any> =
                            HashMap()
                        reg_entry["Name"] = txt_name.getText().toString()
                        reg_entry["Email"] = email.getText().toString()
                        reg_entry["Password"] = pwd.getText().toString()

                        firebaseFirestore!!.collection("users")
                            .add(reg_entry)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@Register,
                                    "Successfully added",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@Register,
                                    "Failed to register",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }

        } else {
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }

    }

    fun backButton(view: android.view.View) {
        finish()
    }

}