package com.example.gotime_final

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Calendar

class EditProject : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_project)

        //Initialising FirebaseAuth
        auth = FirebaseAuth.getInstance()

        //Getting current logged in user's UID
        val uid = auth.currentUser?.uid
        val uidStr = uid.toString()

        //Setting reference path to where the data must be stored in database tree
        // Users/current user UID/new folder called Projects
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+ uid.toString()+"Projects")

        //declaring global variables

        var minHours = 2
        var maxHours = 8
        var imgUri = ""
        var currentHours = 3


        var categories = mutableListOf("Chores", "Work", "Studies")//pull firebase categories here !!!

        //All Buttons
        var project = Project("OPSC",
            "Varsity College",
            "Do a semester's work in 5 days",
            "2023/07/03","2023/07/11",
            2,10, 4,
            "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png".toUri(),
            uid)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val spnCategories = findViewById<Spinner>(R.id.spnCategories)
        val imgProject = findViewById<ImageView>(R.id.ImgProject)
        val btnAddProject = findViewById<Button>(R.id.btnAddProject)
        val btnEndDate = findViewById<Button>(R.id.edtEndDate)
        //declaring global adapters
        val adapterAddCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapterAddCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Access the categories spinner
        if (spnCategories != null) {
            spnCategories.adapter = adapterAddCategory
            spnCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }//end spinner categories

        //Add Category Button
        btnAddCategory.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            builder.setView(input)
            builder.setTitle("Add a new Category :")
            builder.setPositiveButton("Add") { dialog, which ->
                //gets text from user
                val text = input.text.toString()
                categories.add(text)
                Toast.makeText(this@EditProject, "Added new Category : $text",
                    Toast.LENGTH_SHORT).show()
                //update firebase categories here !!!
                adapterAddCategory.notifyDataSetChanged()
                spnCategories.setSelection(adapterAddCategory.count)
            }
            builder.setNegativeButton("Cancel") { dialog, which
                ->
                dialog.cancel()
            }
            builder.show()
        }//addCategory

        //btn End Date
        btnEndDate.setOnClickListener{
            val c = Calendar.getInstance () // get the current date
            val year = c.get (Calendar.YEAR)
            val month = c.get (Calendar.MONTH)
            val day = c.get (Calendar.DAY_OF_MONTH)

// create the date picker dialog
            val dpd = DatePickerDialog (this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // do something with the selected date
                btnEndDate.text = "$dayOfMonth/$monthOfYear/$year" // display it on the button
            }, year, month, day)

// show the dialog
            dpd.show ()
        }//btn end date

        //Add image button
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the returned Uri, for example:
            imgProject.setImageURI(uri)
            imgUri = uri.toString()
        }

        //add image
        val addImg = findViewById<Button>(R.id.btnAddImg)
        addImg.setOnClickListener {
            showProgressBar()
            pickImage.launch("image/*")
        }


        btnAddProject.setOnClickListener {
            showProgressBar()

            val name = findViewById<EditText>(R.id.edtProjectName).text.toString()
            val description = findViewById<EditText>(R.id.edtProjectDescription).text.toString()
            val category = spnCategories.selectedItem.toString()
            val startDate = findViewById<DatePicker>(R.id.edtStartDate).toString()
            val endDate = findViewById<DatePicker>(R.id.edtEndDate).toString()
            currentHours = 0

            val updateProject = Project(name,description,category,startDate,endDate,minHours,maxHours,currentHours)
            if (uid != null){

                databaseReference.child(uid).setValue(updateProject).addOnCompleteListener {
                    if(it.isSuccessful){
                        uploadProjectPic(imgUri, uidStr)
                        val intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                    }
                    else{
                        hideProgressBar()
                        Toast.makeText(this@EditProject, "Failed to upload project to profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                hideProgressBar()
                Toast.makeText(this@EditProject, "Failed to upload project to profile", Toast.LENGTH_SHORT).show()
            }
        }

        //cancel button
        btnCancel.setOnClickListener {
            showProgressBar()
            val intent = Intent(this, ViewProjects::class.java)
            startActivity(intent)
        }
    }

    private fun uploadProjectPic(imgUri : String, uidStr : String) {
        imageUri = Uri.parse(imgUri)
        storageReference = FirebaseStorage.getInstance().getReference("Users :/$uidStr")
        storageReference.putFile(imageUri).addOnSuccessListener {

            Toast.makeText(this@EditProject, "Project saved successfully!", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener(){

            hideProgressBar()
            Toast.makeText(this@EditProject, "Failed to upload project image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@EditProject)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}