package com.example.togetherhealth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_competitions.*
import kotlinx.android.synthetic.main.activity_physical_page.*
import kotlinx.android.synthetic.main.activity_physical_page.recyclerView

class PhysicalActivityPage : AppCompatActivity() {
    lateinit var app: MyApplication
    private lateinit var adapter: ActivitiesAdapter
    var db = FirebaseFirestore.getInstance()
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
        setContentView(R.layout.activity_physical_page)
        app = application as MyApplication
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActivitiesAdapter(app.physicalActivities, object:ActivitiesAdapter.MyOnClick{
            override fun onLongClick(p0: View?, pos: Int) {

                val builder =
                    AlertDialog.Builder(this@PhysicalActivityPage) //access context from inner class
                //set title for alert dialog
                builder.setTitle("Delete")
                builder.setMessage("Thrift shop ${app.physicalActivities[pos].toString()}")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes") { dialogInterface, which -> //performing positive action
                    Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()

                   var ref= db!!.collection("users").document(app.userID).collection("activities")
                        .whereEqualTo("name", app.physicalActivities[pos].name)
                        .whereEqualTo("sets", app.physicalActivities[pos].sets)
                        .whereEqualTo("durationMin", app.physicalActivities[pos].durationInMin)
                        .whereEqualTo("achievment", app.physicalActivities[pos].goal).get()
                    ref.addOnSuccessListener { docs->

                        for (d in docs) {

                            d.reference.delete()

                        }
                        app.physicalActivities.remove(app.physicalActivities[pos])
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
               // Timber.d("Here code comes ${pos}.")
               var addIntent= Intent(getApplicationContext(), AddActivity::class.java)
               addIntent.putExtra("position", pos.toString().toInt())

               setResult(RESULT_OK, addIntent)
               getContent.launch(addIntent)

            }

        })
        recyclerView.adapter = adapter
    }

    fun add_acc(view: android.view.View) {
        var addIntent= Intent(getApplicationContext(), AddActivity::class.java)
        // addIntent.putExtra("position", pos.toString().toInt())

        setResult(RESULT_OK, addIntent)
        getContent2.launch(addIntent)
    }

    fun search_activity(view: android.view.View) {
        var templist= app.physicalActivities.filter { x-> x.name.contains(editTextSearchActivity.text.toString()) }
        app.physicalActivities.clear()
        for(tempactivity in templist){
            app.physicalActivities.add(tempactivity)
        }
        adapter.notifyDataSetChanged()
    }
    fun reset_activity_searches(view: android.view.View) {
        app.physicalActivities.clear()
        db!!.collection("users").document(app.userID).collection("activities").get().addOnSuccessListener { docs->

            for (d in docs) {
                app.physicalActivities.add(PhysicalActivity(d.data.get("name").toString(),
                    d.data.get("durationMin").toString().toInt(),
                    d.data.get("sets").toString().toInt(),
                    d.data.get("achievment").toString().toInt()))
            }
            adapter.notifyDataSetChanged()
        }
    }

    fun sortByName(view: android.view.View) {
        if ((view as CheckBox).isChecked) {
            var templist= app.physicalActivities.sortedBy { it.name }
            app.physicalActivities.clear()
            for(tempactivity in templist){
                app.physicalActivities.add(tempactivity)
            }
            adapter.notifyDataSetChanged()
        }
    }
    fun backFunction(view: android.view.View) {
        finish()
    }

    fun sortByAchievment(view: android.view.View) {
        if ((view as CheckBox).isChecked) {
            var templist= app.physicalActivities.sortedBy { it.goal }
            app.physicalActivities.clear()
            for(tempactivity in templist){
                app.physicalActivities.add(tempactivity)
            }
            adapter.notifyDataSetChanged()
        }
    }
}