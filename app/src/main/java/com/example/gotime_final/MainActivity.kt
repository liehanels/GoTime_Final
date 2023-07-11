package com.example.gotime_final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.database.DatabaseReference
import java.util.concurrent.Executors
//import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    //variables
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var gSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //private lateinit var database: DatabaseReference

    //methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        var btnLogin = findViewById<Button>(R.id.btnRegister)
        var btnRegister = findViewById<Button>(R.id.btnRegisterLogin)

        oneTapClient = Identity.getSignInClient(this);

        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build();


        //localSignInButton
        btnLogin.setOnClickListener {

            val email = findViewById<EditText>(R.id.et_emailReg).text.toString()
            val password = findViewById<EditText>(R.id.et_passwordReg).text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this, com.example.gotime_final.Menu::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this , it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this , "Some fields are empty!", Toast.LENGTH_SHORT).show()
            }
        }


        //RegisterButton
        btnRegister.setOnClickListener {
            val intent = Intent(this, com.example.gotime_final.Register::class.java)
            startActivity(intent)
        }



        //GoogleSignInButton
        var googleSbtn = findViewById<ImageButton>(R.id.googlesignin)
        googleSbtn.setOnClickListener {
            try {
                Log.d("GOOGLE : ", "Google Button Clicked")
                googleSignIn()
            } catch (e: Exception) {
                Log.d("MAIN ACTIVITY : ", e.message.toString() + " :: " + e.printStackTrace())
                Toast.makeText(this, "Something went wrong :( -> ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("664057598826-cjdqsnp7q4dh2949eqjvc8rcbjc6ak37.apps.googleusercontent.com")
            .requestEmail()
            .build()

        gSignInClient = GoogleSignIn.getClient(this, gso)
        Log.d("GOOGLE : ", "Google Initialised Successfully")
    }//onCreate

    // [START on_start_check_user]
    /*public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, com.example.gotime_final.Menu:: class.java)
            startActivity(intent)
        }
    }*/
    // [END on_start_check_user]

    private fun googleSignIn() {
        Log.d("GOOGLE : ", "Google sign in method")
        val signIntent = gSignInClient.signInIntent
        launcher.launch(signIntent)
    }//googleSignIn



    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }//launcher



    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val acc: GoogleSignInAccount? = task.result
            Log.d("GOOGLE : ", task.result.displayName.toString())
            if (acc != null) {
                Log.d("GOOGLE : ", "acc is not null")
                updateUI(acc)
            }
            else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }//handleResults



    private fun updateUI(acc: GoogleSignInAccount){
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            Log.d("GOOGLE : ", "updateUI")
            val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
            auth = FirebaseAuth.getInstance()
            auth.signInWithCredential(credential).addOnCompleteListener{
                if (it.isSuccessful){
                    Handler(Looper.getMainLooper()).post {
                        Log.d("GOOGLE : ", "Get db access and change UI")

                        startActivity(Intent(this, Menu::class.java))
                    }
                }
            }
        }
    }
}//mainAct