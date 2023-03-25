package com.example.togetherhealth

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


class MainActivity : AppCompatActivity(), View.OnClickListener,OnCompleteListener<AuthResult> {
    lateinit var app: MyApplication
    var db: FirebaseFirestore? = null
    var email =""
    var pass = ""
    var getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //Log.i("INFO","woo")
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val inputStream = contentResolver.openInputStream(data.data!!)
                    val bmp=BitmapFactory.decodeStream(inputStream)
                    Toast.makeText(this@MainActivity, "ja zima slikata", Toast.LENGTH_SHORT).show()
                    qrRecognition(bmp)

                }
            }
        }
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
                email =txt_email.text.toString()
                pass=txt_pwd.text.toString()
                app.auth.signInWithEmailAndPassword(
                    txt_email.text.toString(),
                    txt_pwd.text.toString()
                ).addOnCompleteListener(this)
            }
        }
        btn_register.setOnClickListener(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                200);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                200);
        }
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_register -> {
                val register_view = Intent(this@MainActivity, Register::class.java)
                startActivity(register_view)
            }
        }
    }
    private fun qrRecognition(bmp: Bitmap) {
        val image = InputImage.fromBitmap(bmp, 0)
        val scanner = BarcodeScanning.getClient()
        Log.i("INFO","woo")
        val result = scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    val rawValueList = rawValue!!.split(" ")
                    val emaill = rawValueList[0]
                    val passs = rawValueList[1]
                    email=emaill
                    pass=passs
                    app.auth.signInWithEmailAndPassword(
                        emaill,
                        passs
                    ).addOnCompleteListener(this)

                    Toast.makeText(this@MainActivity, rawValue, Toast.LENGTH_SHORT).show()
                    Log.i("INFO","success")
                }
                Toast.makeText(this@MainActivity, "there isn't a QR code", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "fail", Toast.LENGTH_SHORT).show()
                Log.i("INFO","fail")
            }

    }
    override fun onComplete(task: Task<AuthResult>) {
        if (task.isSuccessful) {
            val a1 = txt_email!!.text.toString().trim { it <= ' ' }
            val b1 = txt_pwd!!.text.toString().trim { it <= ' ' }
            db!!.collection("users").get().addOnSuccessListener { snapshot->
                for (d in snapshot) {
                    app.users.add(
                        Userr(d.data.get("Name").toString(),
                            d.data.get("Email").toString()))

                }
            }
            db!!.collection("users").whereEqualTo("Email", email).whereEqualTo("Password", pass)
                .get()
                .addOnSuccessListener { documents ->
                    var name:String=""
                    for (document in documents) {
                        app.username= document.data.get("Name").toString()
                        app.userID=document.id
                        var temp= arrayOf(document.data.get("messages"))
                        for(t in temp){
                            var tempMsg=t.toString().substring(1,t.toString().length-1)
                            app.msgs.add(tempMsg)
                            Log.i("laalala",t.toString())
                        }

                    }
                    db!!.collection("users").document(app.userID).collection("activities").get()
                        .addOnSuccessListener { docs->

                        for (d in docs) {
                            app.physicalActivities.add(PhysicalActivity(d.data.get("name").toString(),
                                d.data.get("durationMin").toString().toInt(),
                                d.data.get("sets").toString().toInt(),
                                d.data.get("achievment").toString().toInt()))
                        }

                    }
                    db!!.collection("competitions").get().addOnSuccessListener { snapshot->
                        for (d in snapshot) {
                            app.competitions.add(
                                Competition(d.data.get("name").toString(),
                                d.data.get("date").toString(),
                                d.data.get("time").toString(),d.data.get("creator").toString()))

                        }
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
            Log.i("lalla",app.userID)

        }
        else {
            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
        }
    }
    fun scanQR(view: android.view.View) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                200);
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*")
        setResult(RESULT_OK, intent)
        getContent.launch(Intent.createChooser(intent,"Pick qr code"))

    }
}

