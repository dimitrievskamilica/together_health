package com.example.togetherhealth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_competition2.*

class AddCompetitionActivity2 : AppCompatActivity() {
    lateinit var app: MyApplication
    var db = FirebaseFirestore.getInstance()
    private var pos : Int = -1
    private var documentId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_competition2)
        app = application as MyApplication
        if(intent.hasExtra("position")) {
            pos =intent.getIntExtra("position",-1)
            if(pos!=-1) {
                btn_add.text = "Update"
                competition_name.setText(app.competitions[pos].name)
                date_of_competition.setText(app.competitions[pos].date)
                time_of_competition.setText(app.competitions[pos].time)
                btn_add.setTextKeepState("Update")
                var ref = db!!.collection("competitions")
                    .whereEqualTo("name", app.competitions[pos].name)
                    .whereEqualTo("date", app.competitions[pos].date)
                    .whereEqualTo("time", app.competitions[pos].time)
                    .get()
                ref.addOnSuccessListener { docs ->

                    for (d in docs) {
                        documentId = d.id

                    }
                }
            }

        }
    }

    fun addCompetition(view: android.view.View) {
        if (pos == -1) {
            val intent = Intent()
            app.competitions.add(
                Competition(
                    competition_name.text.toString(),
                    date_of_competition.text.toString(),
                    time_of_competition.text.toString(),
                    app.userID.toString()
                )
            )
            val reg_entry: MutableMap<String, Any> =
                HashMap()
            reg_entry["name"] = competition_name.text.toString()
            reg_entry["date"] = date_of_competition.text.toString()
            reg_entry["time"] = time_of_competition.text.toString()

            db!!.collection("competitions").add(reg_entry)
            intent.putExtra("position", app.competitions.size-1)
            setResult(RESULT_OK,intent)
            finish()
        } else {
            val intent = Intent()
            app.competitions[pos].name = competition_name.text.toString()
            app.competitions[pos].date = date_of_competition.text.toString()
            app.competitions[pos].time = time_of_competition.text.toString()
            val reg_entry: MutableMap<String, Any> =
                HashMap()
            reg_entry["name"] = competition_name.text.toString()
            reg_entry["date"] = date_of_competition.text.toString()
            reg_entry["time"] = time_of_competition.text.toString()
            db!!.collection("competitions").document(documentId).set(reg_entry)
            intent.putExtra("position", pos.toString().toInt())
            setResult(RESULT_OK,intent)
            finish()

        }
     }

    fun back_add_competition(view: android.view.View) {
        finish()
    }

}