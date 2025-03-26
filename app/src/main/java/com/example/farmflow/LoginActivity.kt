package com.example.farmflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var signupbtn: Button
    lateinit var loginemail: EditText
    lateinit var login1: Button
    lateinit var loginpassword: EditText
    lateinit var retrive: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)



        signupbtn = findViewById(R.id.signbtn2)
        loginemail = findViewById(R.id.loginemail)
        loginpassword = findViewById(R.id.pass2)
        login1 = findViewById(R.id.loginbtn2)
        retrive=findViewById(R.id.resetpwd)

        auth = FirebaseAuth.getInstance()

        retrive.setOnClickListener {
            val get = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(get)
        }

        signupbtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        login1.setOnClickListener {
            Login()
        }
    }

    private fun Login() {
        val loginemail = loginemail.text.toString()
        val pass2 = loginpassword.text.toString()

        auth.signInWithEmailAndPassword(loginemail, pass2).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }



    }
}