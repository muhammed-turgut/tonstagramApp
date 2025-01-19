package com.muhammedturgut.tonstagram.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.databinding.ActivitySignUpScreenBinding
import java.util.UUID

class SignUpScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpScreenBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var gender: String = "Male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLaunchers()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setupSpinner()

        //güncel kullanıcı
        val currentUser=auth.currentUser
        if(currentUser!= null){
            val intent=Intent(this@SignUpScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSpinner() {
        val items = resources.getStringArray(R.array.spinner_items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = adapter

        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                gender = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Hiçbir şey seçilmediğinde yapılacak işlemler
            }
        }
    }

    fun signUpClick(view: View) {

        val name = binding.nameEditText.text.toString()
        val surename = binding.surenameEditText.text.toString()
        val userName = binding.userNameEditText.text.toString()
        val phoneNumber = binding.phoneNumberEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (name.isNotEmpty() && surename.isNotEmpty() && userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && selectedPicture != null) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uuid = UUID.randomUUID()
                    val imageName = "$uuid.jpg"
                    val storageRef = storage.reference.child("User").child(imageName)

                    storageRef.putFile(selectedPicture!!).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()

                            val postMap = hashMapOf<String, Any>(
                                "downloadUrl" to downloadUrl,
                                "name" to name,
                                "sureName" to surename,
                                "userName" to userName,
                                "Phone" to phoneNumber,
                                "Gender" to gender,
                                "email" to email,
                                "password" to password,
                                "date" to Timestamp.now()
                            )

                            firestore.collection("Users").add(postMap).addOnSuccessListener {
                                val intent = Intent(this@SignUpScreen, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Tüm alanları doldurduğunuzdan ve profil fotoğrafı seçtiğinizden emin olun!", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeProfilPhoto(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.profilPhoto.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                // Permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(this@SignUpScreen, "Permission needed", Toast.LENGTH_LONG).show()
            }
        }
    }
}