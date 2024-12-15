package com.example.quickcare

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickcare.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var user: User
    private lateinit var uid: String
    private lateinit var dialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupButtons()

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid ?: ""

        if (uid.isNotEmpty()) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            storageReference = FirebaseStorage.getInstance().getReference("Users/$uid/profile.jpg")
            getUserData()
            displayUserEmail()
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData() {
        showProgressBar()
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java) ?: return
                binding.displayName.setText(user.name)
                binding.displayAddress.setText(user.address)
                binding.displaypPhone.setText(user.phone)
                getUserProfile()
            }

            override fun onCancelled(error: DatabaseError) {
                hideProgressBar()
                Toast.makeText(this@Profile, "Failed to get user profile data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfile() {
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.imageView2.setImageBitmap(bitmap)
            hideProgressBar()
        }.addOnFailureListener {
            hideProgressBar()
            Toast.makeText(this@Profile, "Failed to retrieve image: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUserEmail() {
        val email = auth.currentUser?.email
        binding.displayEmail.text = email
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupButtons() {
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(this, Editprofile::class.java)
            startActivity(intent)
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, Home02::class.java)
            startActivity(intent)
        }
        binding.logoutButton.setOnClickListener{
            Firebase.auth.signOut()

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.DeleteButton.setOnClickListener{
            auth.currentUser?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(this,"Delete Successful",Toast.LENGTH_SHORT).show()
                    val mainIntent = Intent(this,MainActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainIntent)
                    finish()

                }?.addOnFailureListener {
                    it.printStackTrace()
                }

        }
    }


    private fun showProgressBar(){
        dialog = Dialog(this@Profile)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }
}
