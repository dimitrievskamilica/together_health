package com.example.togetherhealth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_add.btn_add
import kotlinx.android.synthetic.main.activity_add_competition2.*

class AddActivity : AppCompatActivity() {
    lateinit var app: MyApplication
    var db = FirebaseFirestore.getInstance()
    private var pos : Int = -1
    private var documentId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        app = application as MyApplication
        if(intent.hasExtra("position")) {
            pos =intent.getIntExtra("position",-1)
            if(pos!=-1) {
                btn_add.text = "Update"
                activity_name.setText(app.physicalActivities[pos].name)
                duration.setText(app.physicalActivities[pos].durationInMin.toString())
                sets.setText(app.physicalActivities[pos].sets.toString())
                achievment.setText(app.physicalActivities[pos].goal.toString())
                btn_add.setTextKeepState("Update")
                var ref= db!!.collection("users").document(app.userID).collection("activities")
                    .whereEqualTo("name", app.physicalActivities[pos].name)
                    .whereEqualTo("sets", app.physicalActivities[pos].sets)
                    .whereEqualTo("durationMin", app.physicalActivities[pos].durationInMin)
                    .whereEqualTo("achievment", app.physicalActivities[pos].goal).get()
                ref.addOnSuccessListener { docs ->

                    for (d in docs) {
                        documentId = d.id

                    }
                }
            }

        }
    }

    fun addPhysicalActivity(view: android.view.View) {
        if(pos==-1){

            val intent = Intent()
            app.physicalActivities.add(PhysicalActivity(
            activity_name.text.toString(),
            duration.text.toString().toInt(),
            sets.text.toString().toInt(),
            achievment.text.toString().toInt()))
            val reg_entry: MutableMap<String, Any> =
            HashMap()
            reg_entry["name"] = activity_name.text.toString()
            reg_entry["durationMin"] = duration.text.toString().toInt()
            reg_entry["sets"] = sets.text.toString().toInt()
            reg_entry["achievment"] = achievment.text.toString().toInt()
            db!!.collection("users").document(app.userID).collection("activities").add(reg_entry)
            intent.putExtra("position", app.physicalActivities.size-1)
            setResult(RESULT_OK,intent)
            finish()

        }else{

            val intent = Intent()
            app.physicalActivities[pos].name=activity_name.text.toString()
            app.physicalActivities[pos].durationInMin=duration.text.toString().toInt()
            app.physicalActivities[pos].sets= sets.text.toString().toInt()
            app.physicalActivities[pos].goal=achievment.text.toString().toInt()
            val reg_entry: MutableMap<String, Any> =
                HashMap()
            reg_entry["name"] = activity_name.text.toString()
            reg_entry["durationMin"] = duration.text.toString().toInt()
            reg_entry["sets"] = sets.text.toString().toInt()
            reg_entry["achievment"] = achievment.text.toString().toInt()
            db!!.collection("users").document(app.userID).collection("activities").document(documentId)
                .set(reg_entry)
            intent.putExtra("position", pos.toString().toInt())
            setResult(RESULT_OK,intent)
            finish()
        }


    }

    fun backPhysicalActivity(view: android.view.View) {
        finish()
    }
}