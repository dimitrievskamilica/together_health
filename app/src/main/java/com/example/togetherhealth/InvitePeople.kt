package com.example.togetherhealth

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_competition2.*
import kotlinx.android.synthetic.main.activity_invite_people.*
import kotlinx.android.synthetic.main.activity_invite_people.btn_add
import kotlinx.android.synthetic.main.activity_physical_page.*
import kotlinx.android.synthetic.main.activity_physical_page.recyclerView

class InvitePeople : AppCompatActivity() {
    lateinit var app: MyApplication
    private lateinit var adapter: InvitationsAdapter
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_people)
        app = application as MyApplication
        recyclerView.layoutManager = LinearLayoutManager(this)
        if(intent.hasExtra("position")) {
            var pos =intent.getIntExtra("position",-1)
            if(pos!=-1) {

               reg_name.setText(app.competitions[pos].name)

            }

        }

        adapter = InvitationsAdapter(app.users, object:InvitationsAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {


            }

            override fun onClick(p0: View?, pos:Int) {

                    val builder =
                        AlertDialog.Builder(this@InvitePeople) //access context from inner class
                    //set title for alert dialog
                    builder.setTitle("Invitation")
                    builder.setMessage("Do you want to invite ${app.users[pos].name} to ${reg_name.text}?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                        Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


                        // setResult(RESULT_OK, data)
                        //app.saveData()
                        db!!.collection("users").whereEqualTo("Email", app.users[pos].email)
                            .whereEqualTo("Name", app.users[pos].name)
                            .get()
                            .addOnSuccessListener { docs ->
                                for (d in docs) {
                                    d.reference.update("messages", FieldValue.arrayUnion("${app.username} wants to invite you to ${reg_name.text}"))

                                }
                            }


                    }
                    builder.setNeutralButton("Cancel") { dialogInterface, which -> //performing cancel action
                        Toast.makeText(
                            applicationContext,
                            "clicked cancel\n operation cancel",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    builder.setNegativeButton("No") { dialogInterface, which -> //performing negative action
                        Toast.makeText(applicationContext, "clicked No", Toast.LENGTH_LONG).show()
                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
            }



        })
        recyclerView.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    fun goBack(view: android.view.View) {
        finish()
    }
}