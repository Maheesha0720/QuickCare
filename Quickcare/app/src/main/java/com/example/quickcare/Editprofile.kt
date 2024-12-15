package com.example.quickcare

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickcare.databinding.ActivityEditprofileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Editprofile : AppCompatActivity() {

    private lateinit var binding: ActivityEditprofileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
   private lateinit var dialog: Dialog
    private val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupButtons()

        imageView = binding.imageView

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
            storageReference = FirebaseStorage.getInstance().getReference("Users/$uid/profile.jpg")
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        binding.button5.setOnClickListener {

            showProgressBar()
            val name = binding.autoCompleteTextView01.text.toString().trim()
            val address = binding.autoCompleteTextView03.text.toString().trim()
            val phone = binding.autoCompleteTextView04.text.toString().trim()

            if (name.isEmpty() ||address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name, address, phone)
            databaseReference.setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (imageUri != null) {
                        uploadProfilePic()
                    } else {
                        hideProgressBar()
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageUri = uri
                    imageView.setImageURI(uri)
                }
            } else {
                Toast.makeText(this, "Image pick cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openImagePicker()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        requestPermissions()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupButtons() {
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        binding.cam4.setOnClickListener {
            if (allPermissionsGranted()) {
                openImagePicker()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestPermissions() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun allPermissionsGranted() = permission.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun uploadProfilePic() {
        imageUri?.let {
            val fileRef = storageReference
            fileRef.putFile(it).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    databaseReference.child("profileImageUrl").setValue(uri.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                hideProgressBar()
                                Toast.makeText(this, "Profile updated with image", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Failed to update profile image URL: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }.addOnFailureListener { e ->
                    hideProgressBar()
                    Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressBar(){
        dialog = Dialog(this@Editprofile)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }
}
