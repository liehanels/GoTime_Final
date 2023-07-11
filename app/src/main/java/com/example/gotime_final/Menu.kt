package com.example.gotime_final

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Menu : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_page)

        auth = FirebaseAuth.getInstance()

        var btnNewProject : Button = findViewById(R.id.btnNewProject)
        var btnViewProjects : Button = findViewById(R.id.btnViewProjects)
        var tvUsername : TextView = findViewById(R.id.tvMenuUserName)
        var uid = auth.currentUser?.uid.toString()
        var profilePic : ImageView = findViewById(R.id.ivProfilePic)
        val btnChangePP : Button = findViewById(R.id.btnChangeProfilePic)
        var imgUri = ""
        val localfile = File.createTempFile("tempImage", "png")
        val storageRef = FirebaseStorage.getInstance().reference.child("Users/$uid/Profile/$uid.png")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            profilePic.setImageBitmap(bitmap)
        }
        btnChangePP.setOnClickListener{

            //Add image button
            val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                // Handle the returned Uri, for example:
                profilePic.setImageURI(uri)
                imgUri = uri.toString()
                profilePic.setImageURI(uri)
                //need to check later
            }
        }

        //Getting username from db and setting to tvUsername
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val name = it.child("firstName").value
                tvUsername.text = name.toString()
            }
        }

        btnNewProject.setOnClickListener(){
            showProgressBar()
            val intent = Intent(this, CreateProject::class.java)
            startActivity (intent)
        }

        btnViewProjects.setOnClickListener(){
            showProgressBar()
            val intent = Intent(this, ViewProjects::class.java)
            startActivity (intent)
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@Menu)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}