package com.example.farmflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    lateinit var signupemail: EditText
    lateinit var name: EditText
    lateinit var signuppassword: EditText
    lateinit var confpassword: EditText
    lateinit var signup: Button
    lateinit var login: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        signupemail=findViewById(R.id.email)
        name=findViewById(R.id.name)
        signuppassword=findViewById(R.id.signuppass)
        confpassword=findViewById(R.id.confpass)
        signup=findViewById(R.id.signupbtn)
        login=findViewById(R.id.loginbtn)
        auth= Firebase.auth

        login.setOnClickListener{
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        signup.setOnClickListener{

            SignUpUser()
        }


    }

    private fun SignUpUser(){
        val clientemail=signupemail.text.toString()
        val clientpass=signuppassword.text.toString()
        val confirmpass=confpassword.text.toString()

        if (clientemail.isBlank() || clientpass.isBlank() || confirmpass.isBlank()){
            Toast.makeText(this,"Please Email and password cant be blank", Toast.LENGTH_LONG).show()
            return

        }  else if (clientpass != confirmpass){
            Toast.makeText(this,"Password do not match", Toast.LENGTH_LONG).show()
            return

        }

        auth.createUserWithEmailAndPassword(clientemail,clientpass).addOnCompleteListener(this) {
            if (it.isSuccessful){
                Toast.makeText(this,"Signed up successfully", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }else{
                Toast.makeText(this,"Failed to create", Toast.LENGTH_LONG).show()
            }

        }

    }
}