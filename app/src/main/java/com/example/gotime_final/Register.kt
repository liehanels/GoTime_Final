package com.example.gotime_final

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog: Dialog

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_user)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        var btnRegister: Button = findViewById(R.id.btnRegister)


        btnRegister.setOnClickListener() {

            showProgressBar()
            val firstName = findViewById<EditText>(R.id.et_firstNameReg).text.toString()
            val email = findViewById<EditText>(R.id.et_emailReg).text.toString()
            val password = findViewById<EditText>(R.id.et_passwordReg).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.et_confirmPasswordReg).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    if (password.length >= 6) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                val intent = Intent(
                                                    this,
                                                    com.example.gotime_final.MainActivity::class.java
                                                )
                                                startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    it.exception.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    val uid = auth.currentUser?.uid
                                    val newUser = User(firstName, email, password, uid)
                                    if (uid != null) {
                                        databaseReference.child(uid).setValue(newUser)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    val intent =
                                                        Intent(this, MainActivity::class.java)
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "Failed to register user in RTDB",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    hideProgressBar()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(this, "UID is null!", Toast.LENGTH_SHORT)
                                            .show()
                                        hideProgressBar()
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Auth User Record was not processed properly",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    hideProgressBar()
                                }
                            }
                    }
                    else{
                        hideProgressBar()
                        Toast.makeText(this, "Password cannot be less than 6 characters!", Toast.LENGTH_SHORT).show()
                    }
                }
                 else {
                     hideProgressBar()
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                 }
            } else {
                hideProgressBar()
                Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show()
            }
        }//btnRegisterOnClickListener
    }//OnCreate

    private fun showProgressBar(){

        dialog = Dialog(this@Register)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.loading_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){

        dialog.dismiss()
    }
}

