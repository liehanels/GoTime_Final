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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Calendar

class CreateProject : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_project)

        //Initialising FirebaseAuth
        auth = FirebaseAuth.getInstance()

        //Getting current logged in user's UID
        val uid = auth.currentUser?.uid
        val uidStr = uid.toString()

        //Setting reference path to where the data must be stored in database tree
        // Users/current user UID/new folder called Projects
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+ uid.toString()+"/Projects/")

        //declaring global variables

        var minHours = 2
        var maxHours = 8
        var imgUri = ""
        var currentHours = 3


        var categories = mutableListOf("Chores", "Work", "Studies")//pull firebase categories here !!!


        //UI controls
        val seekerMin = findViewById<SeekBar>(R.id.sbMin)
        seekerMin.progress = minHours
        val seekerMax = findViewById<SeekBar>(R.id.sbMax)
        seekerMax.progress = maxHours
        val seekerTvMin = findViewById<TextView>(R.id.tvMin)
        seekerTvMin.text = minHours.toString()
        val seekerTvMax = findViewById<TextView>(R.id.tvMax)
        seekerTvMax.text = maxHours.toString()

        //All Buttons
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val spinner = findViewById<Spinner>(R.id.spnCategories)
        val imgProject = findViewById<ImageView>(R.id.ImgProject)
        val btnAddProject = findViewById<Button>(R.id.btnAddProject)
        val btnStartDate = findViewById<Button>(R.id.edtStartDate)
        val btnEndDate = findViewById<Button>(R.id.edtEndDate)

        //declaring global adapters
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //sliders for hours on project
        //min hours slider
        seekerMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekerTvMin.text = progress.toString()
                minHours = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (minHours > maxHours){
                    Toast.makeText(this@CreateProject, "Min hours can't be more than max hours !", Toast.LENGTH_SHORT).show()
                    minHours = maxHours - 1
                    seekBar?.progress = minHours
                }
            }
        })

        //max hours slider
        seekerMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekerTvMax.text = progress.toString()
                maxHours = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (maxHours < minHours){
                    Toast.makeText(this@CreateProject, "Max hours can't be less than min hours !", Toast.LENGTH_SHORT).show()
                    maxHours = minHours + 1
                    seekBar?.progress = maxHours
                }
            }
        })

        // Access the spinner
        if (spinner != null) {
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }//spinner

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
                Toast.makeText(this@CreateProject, "Added new Category : $text",
                    Toast.LENGTH_SHORT).show()
                //update firebase categories here !!!
                adapter.notifyDataSetChanged()
                spinner.setSelection(adapter.count)
            }
            builder.setNegativeButton("Cancel") { dialog, which
            ->
                dialog.cancel()
            }
            builder.show()
        }//addCategory

        //Add image button
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the returned Uri, for example:
            imgProject.setImageURI(uri)
            imgUri = uri.toString()
        }

        //add image
        val addImg = findViewById<Button>(R.id.btnAddImg)
        addImg.setOnClickListener {
            pickImage.launch("image/*")
        }

        //btn Start Date
        btnStartDate.setOnClickListener{
            val c = Calendar.getInstance () // get the current date
            val year = c.get (Calendar.YEAR)
            val month = c.get (Calendar.MONTH)
            val day = c.get (Calendar.DAY_OF_MONTH)

// create the date picker dialog
            val dpd = DatePickerDialog (this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // do something with the selected date
                btnStartDate.text = "$dayOfMonth/$monthOfYear/$year" // display it on the button
            }, year, month, day)

// show the dialog
            dpd.show ()
        }//btn Start date


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

//add project button
        btnAddProject.setOnClickListener {
            showProgressBar()

            val name = findViewById<EditText>(R.id.edtProjectName).text.toString()
            val description = findViewById<EditText>(R.id.edtProjectDescription).text.toString()
            val category = spinner.selectedItem.toString()
            val startDate = btnStartDate.text.toString()
            val endDate = btnEndDate.text.toString()
            currentHours = 0

            val newProject = Project(name,category,description,startDate,endDate,minHours,maxHours,currentHours)
            if (uid != null){

                databaseReference.child(newProject.Name.toString()).setValue(newProject).addOnCompleteListener {
                    if(it.isSuccessful){
                        uploadProjectPic(imgUri, uidStr, name)
                        val intent = Intent(this, Menu::class.java)
                        startActivity(intent)
                    }
                    else{
                        hideProgressBar()
                        Toast.makeText(this@CreateProject, "Failed to upload project to profile", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                hideProgressBar()
                Toast.makeText(this@CreateProject, "Failed to upload project to profile", Toast.LENGTH_SHORT).show()
            }
        }

        //cancel button
        btnCancel.setOnClickListener {
            showProgressBar()
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }
    }

    private fun uploadProjectPic(imgUri : String, uidStr : String, name : String) {
        imageUri = Uri.parse(imgUri)
        storageReference = FirebaseStorage.getInstance().getReference("Users/$uidStr/Projects/$name")
        storageReference.putFile(imageUri).addOnSuccessListener {

            Toast.makeText(this@CreateProject, "Project saved successfully!", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener(){

            hideProgressBar()
            Toast.makeText(this@CreateProject, "Failed to upload project image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@CreateProject)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){
        dialog.dismiss()
    }
}