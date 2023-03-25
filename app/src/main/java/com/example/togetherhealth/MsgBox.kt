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
import kotlinx.android.synthetic.main.activity_invite_people.*
import kotlinx.android.synthetic.main.activity_physical_page.*
import kotlinx.android.synthetic.main.activity_physical_page.recyclerView

class MsgBox : AppCompatActivity() {
    lateinit var app: MyApplication
    private lateinit var adapter: MsgBoxAdapter
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msg_box)
        app = application as MyApplication
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MsgBoxAdapter(app.msgs, object:MsgBoxAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {

                val builder =
                    AlertDialog.Builder(this@MsgBox) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Delete")
                builder.setMessage("Message: ${app.msgs[pos]}")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                    Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


                    // setResult(RESULT_OK, data)
                    //app.saveData()
                    db!!.collection("users").document(app.userID)
                        .get()
                        .addOnSuccessListener { d ->
                            d.reference.update("messages", FieldValue.arrayRemove(app.msgs[pos]))
                            app.msgs.remove(app.msgs[pos])
                            adapter.notifyItemRemoved(pos)
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

            override fun onClick(p0: View?, pos:Int) {
                var competition = app.msgs[pos].substringAfter("wants to invite you to ",app.msgs[pos])
                val builder =
                        AlertDialog.Builder(this@MsgBox) //access context from inner class
                    //set title for alert dialog
                    builder.setTitle("Accept the invitation")
                    builder.setMessage("Competition: ${competition}")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                        Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


                        var ref= db!!.collection("competitions")
                            .whereEqualTo("name", competition)
                            .get()
                        ref.addOnSuccessListener { docs->

                            for (d in docs) {
                                d.reference.update("competitors", FieldValue.arrayUnion(app.userID))

                            }
                            // app.msgs.remove(app.msgs[pos])
                           // adapter.notifyItemRemoved(pos)
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
    }

    fun backMsgBox(view: android.view.View) {
        finish()
    }
}