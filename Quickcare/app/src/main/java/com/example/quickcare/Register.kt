package com.example.quickcare

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.quickcare.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
     binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView01.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.button3.setOnClickListener{
            val email = binding.EmailAddress.text.toString()
            val pass = binding.Password.text.toString()
            val conPass = binding.conPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && conPass.isNotEmpty()){
                if (pass == conPass){

                        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this,"Register successful" , Toast.LENGTH_SHORT).show()
                                val intent = Intent(this , MainActivity::class.java)

                                startActivity(intent)
                            }else{
                                Toast.makeText(this,"Register Unsuccessful" , Toast.LENGTH_SHORT).show()
                                Toast.makeText(this,it.exception.toString() , Toast.LENGTH_SHORT).show()

                            }
                        }
                    }else{
                    Toast.makeText(this,"Password is not matching" , Toast.LENGTH_SHORT).show()

                }
                }else{
                Toast.makeText(this,"Please fill out empty fields" ,Toast.LENGTH_SHORT).show()
            }
        }




    }
}