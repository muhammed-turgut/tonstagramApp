package com.muhammedturgut.tonstagram.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var acvtivtyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLanchuer()

        auth=Firebase.auth
        db=Firebase.firestore

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("Users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val surename = document.getString("sureName")
                    val userName = document.getString("userName")
                    val phone = document.getString("Phone")
                    val email = document.getString("email")
                    val gender = document.getString("Gender")
                    val profileImageUrl = document.getString("downloadUrl")

                    binding.editTextName.setText("$name $surename")
                    binding.editTextTextUsername.setText(userName)
                    binding.phoneEditProfil.setText(phone)
                    binding.emailEditProfil.setText(email)
                    binding.genderEditProfil.setText(gender)

                    // Profil resmini yükle
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .into(binding.editProfilPhoto)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Veritabanı hatası: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadUserProfile() {

    }



    fun doneEditProfil(view: View){

// Firestore bağlantısını kur
        db = FirebaseFirestore.getInstance()

    }

    fun cancelEditProfil(view:View){

        val Name=binding.editTextName.text.toString()
        val username=binding.editTextTextUsername.text.toString()
        val webSite=binding.editTextTextWebSite.text.toString()
        val about=binding.editTextTextAbout.text.toString()
        val image=binding.editProfilPhoto.imageAlpha


        finish()


    }

    fun changeProfilPhoto(view: View){

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallerry", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }
            else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            acvtivtyResultLauncher.launch(intentToGallery)

        }
    }


    private fun registerLanchuer(){

        acvtivtyResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult=result.data
                if(intentFromResult != null){
                    selectedPicture= intentFromResult.data
                    selectedPicture.let {
                        binding.editProfilPhoto.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                //permission granted
                val intentToGallery=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                acvtivtyResultLauncher.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@EditProfile,"Permission needed", Toast.LENGTH_LONG).show()
            }
        }

    }


}