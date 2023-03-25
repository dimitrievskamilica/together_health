package com.example.togetherhealth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_competitions.*
import kotlinx.android.synthetic.main.activity_physical_page.*
import kotlinx.android.synthetic.main.activity_physical_page.recyclerView

class CompetitionsActivity : AppCompatActivity() {
    lateinit var app: MyApplication
    private lateinit var adapter: CompetitionsAdapter
    var db = FirebaseFirestore.getInstance()
    var POSITION: Int =-1
    var getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        adapter.notifyItemChanged(pos)
                    }

                }
            }
        }
    var getContent2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val pos = data.getIntExtra("position", -1)
                    if(pos!=-1) {
                        adapter.notifyItemInserted(pos)
                    }

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitions)
        app = application as MyApplication
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CompetitionsAdapter(app.competitions, object:CompetitionsAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {
                //Timber.d("Here code comes ${pos}.")
                // val data = intent
                // data.putExtra("SELECTED_ID", app.thriftShops[pos].id)
                val builder =
                    AlertDialog.Builder(this@CompetitionsActivity) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Delete")
                builder.setMessage("Competition ${app.competitions[pos].name}")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                    Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


                    // setResult(RESULT_OK, data)
                    //app.saveData()
                    var ref= db!!.collection("competitions")
                        .whereEqualTo("name", app.competitions[pos].name)
                        .whereEqualTo("date", app.competitions[pos].date)
                        .whereEqualTo("time", app.competitions[pos].time)
                        .get()
                    ref.addOnSuccessListener { docs->

                        for (d in docs) {
                            d.reference.delete()

                        }
                        app.competitions.remove(app.competitions[pos])
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
                POSITION=pos.toString().toInt()
                if(app.competitions[pos.toString().toInt()].creator==app.userID) {
                    var addIntent =
                        Intent(getApplicationContext(), AddCompetitionActivity2::class.java)
                    addIntent.putExtra("position", pos.toString().toInt())

                    setResult(RESULT_OK, addIntent)
                    getContent.launch(addIntent)
                }else{
                    val builder =
                        AlertDialog.Builder(this@CompetitionsActivity) //access context from inner class
                    //set title for alert dialog
                    builder.setTitle("Sign up for the competition")
                    builder.setMessage("Competition: ${app.competitions[pos].name}")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                        Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()


                        // setResult(RESULT_OK, data)
                        //app.saveData()
                        var ref= db!!.collection("competitions")
                            .whereEqualTo("name", app.competitions[pos].name)
                            .whereEqualTo("date", app.competitions[pos].date)
                            .whereEqualTo("time", app.competitions[pos].time)
                            .get()
                        ref.addOnSuccessListener { docs->

                            for (d in docs) {
                                d.reference.update("competitors", FieldValue.arrayUnion(app.userID))

                            }
                           //// app.competitions.remove(app.competitions[pos])
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

            }

        })
        recyclerView.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    fun add_competition(view: android.view.View) {
        var addIntent= Intent(getApplicationContext(), AddCompetitionActivity2::class.java)

        setResult(RESULT_OK, addIntent)
        getContent2.launch(addIntent)
    }

    fun search_competitions(view: android.view.View) {
        var templist= app.competitions.filter { x-> x.name.contains(editTextTextPersonName.text.toString()) }
        app.competitions.clear()
        for(tempcompetition in templist){
           app.competitions.add(tempcompetition)
        }
        adapter.notifyDataSetChanged()
    }

    fun reset_competition_searches(view: android.view.View) {
        app.competitions.clear()
        db!!.collection("competitions").get().addOnSuccessListener { snapshot->
            for (d in snapshot) {
                app.competitions.add(
                    Competition(d.data.get("name").toString(),
                        d.data.get("date").toString(),
                        d.data.get("time").toString(),
                        d.data.get("creator").toString()))
            }
            adapter.notifyDataSetChanged()
        }

    }

    fun openActivityInvite(view: android.view.View) {
        val open_activity = Intent(this, InvitePeople::class.java)
        open_activity.putExtra("position",POSITION)
        startActivity(open_activity)
    }

    fun back_competition(view: android.view.View) {
        finish()
    }
}