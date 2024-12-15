package com.example.quickcare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.quickcare.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.textReg.setOnClickListener{
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener{
            val email = binding.email.text.toString()
            val pass = binding.password.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() ){

                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            Toast.makeText(this,"Login successful" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(this , Home02::class.java)
                            startActivity(intent)

                        }else{
                            Toast.makeText(this,"Login Unsuccessful", Toast.LENGTH_SHORT).show()

                        }
                    }
            }else{
                Toast.makeText(this,"Please fill out empty fields" , Toast.LENGTH_SHORT).show()
            }
        }




    }
}