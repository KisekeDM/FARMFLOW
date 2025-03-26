package com.example.farmflow

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private var CurrentProgress = 0
    lateinit var go: Button
    lateinit var mail: EditText
    lateinit var spin: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        go = findViewById(R.id.push)
        mail = findViewById(R.id.email3)
        spin = findViewById(R.id.progress)
        auth = FirebaseAuth.getInstance()

        go.setOnClickListener {
            send()
        }


    }

    private fun send() {
        val mill = mail.text.toString()

        if (mill.isBlank()) {
            Toast.makeText(this, " Email Is required", Toast.LENGTH_LONG).show()
            CurrentProgress += 50
            spin.max=100
            return

        } else if (!Patterns.EMAIL_ADDRESS.matcher(mill).matches()) {
            Toast.makeText(this, "Email Incorrect Or Do not match", Toast.LENGTH_LONG).show()
            CurrentProgress += 50
            spin.max=100
            return
        }
        auth.sendPasswordResetEmail(mill).addOnCompleteListener(this){
            if (it.isSuccessful){
                Toast.makeText(this,"Successfully Changed Your password. Kindly Check Your inbox",
                    Toast.LENGTH_LONG).show()
                val me = Intent(this,LoginActivity::class.java)
                startActivity(me)
                CurrentProgress +=100
                spin.progress = CurrentProgress
                spin.max=100
            }else{
                Toast.makeText(this,"Something Went Wrong.Try Again", Toast.LENGTH_LONG).show()
                CurrentProgress += 0
                spin.max=100

            }
        }



    }
}