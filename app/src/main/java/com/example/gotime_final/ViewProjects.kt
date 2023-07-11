package com.example.gotime_final

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values
import com.google.firebase.storage.StorageReference
//import com.google.gson.Gson
import java.net.URL
import java.util.Date
import java.util.concurrent.Executors

class ViewProjects : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri
    lateinit var projectAdapter : ProjectAdapter
    private lateinit var dialog: Dialog
    public override fun onCreate(savedInstanceState: Bundle?)
    {
        //Initialising FirebaseAuth
        auth = FirebaseAuth.getInstance()

        //Getting current logged in user's UID
        val uid = auth.currentUser?.uid.toString()
        var projectList = mutableListOf<Project>()

        //call layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_projects)

        //Recycler View
        val recyclerView = findViewById<RecyclerView>(R.id.RvProjects)
        projectAdapter = ProjectAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewProjects)
            adapter = projectAdapter
            //recycle view
        }
        //handle db data
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
        projectList.add(Project("OPSC",
            "Varsity College",
            "Do a semester's work in 5 days",
            "2023/07/03","2023/07/11",
            2,10, 4,
            "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
            uid))
        projectList.add(Project("Mental Health",
            "Health",
            "I need to take regular breaks and also my anti-depressants",
            "2023/07/03","2023/07/11",
            1,10, 2,
            "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                            uid))
            projectList.add(Project("Dishes",
                "Chores",
                "There are too many dishes.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Make the bed",
                "Chores",
                "You slept in it, make it neat now.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Groceries",
                "Chores",
                "We need milk and eggs",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Presentation for OPSC",
                "Varsity College",
                "Make a presentation good enough to blow minds.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("OPSC 2",
                "Varsity College",
                "3 man team, yas",
                "2023/07/03","2023/07/11",
                2,10, 4,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Physical Health",
                "Health",
                "I need to run more",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Dishes again",
                "Chores",
                "There are still too many dishes.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Make lunch",
                "Chores",
                "Eating is good for you.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("More groceries",
                "Chores",
                "We need bread and butter now.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
            projectList.add(Project("Finalise presentation for OPSC",
                "Varsity College",
                "Make a presentation good enough to blow minds.",
                "2023/07/03","2023/07/11",
                1,10, 2,
                "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
                uid))
                }
                Handler(Looper.getMainLooper()).post {
                    projectAdapter.submitList(projectList)
                }
        //back button
        var btnBack : Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener(){
            val intent = Intent(this, Menu::class.java)
            startActivity (intent)
        }
    }
    //private lateinit var dialog: Dialog
    private fun showProgressBar(){
        dialog = Dialog(this@ViewProjects)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}