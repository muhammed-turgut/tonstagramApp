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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.databinding.ActivityEditProfileBinding
import com.squareup.picasso.Picasso
import java.util.UUID

class EditProfile : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var acvtivtyResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var db: FirebaseFirestore
    private  var photoChange:Boolean =false
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLanchuer()

        auth=Firebase.auth
        db=Firebase.firestore


        profileInformation()

    }

     fun profileInformation(){
         val email=auth.currentUser!!.email.toString()
         val userCollection=db.collection("Users")

        userCollection.whereEqualTo("email",email).get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    val document=querySnapshot.documents[0]
                    val documentId=document.id

                    val docRef=userCollection.document(documentId)
                    docRef.get().addOnSuccessListener { document ->
                        if(document != null && document.exists()){

                            val imageProfileUrl=document.get("downloadUrl").toString()

                            if(imageProfileUrl != null){

                                Picasso.get()
                                    .load(imageProfileUrl)
                                    .transform(SquareTransformation())
                                    .into(binding.editProfilPhoto)
                            }
                            else{
                                println("veri Okunamadi")
                            }

                            binding.editTextName.setText(document.get("name").toString()+" "+document.get("sureName").toString())
                            binding.editTextTextUsername.setText(document.get("userName").toString())
                            binding.emailEditProfil.text=document.get("email").toString()
                            binding.editTextTextAbout.setText(document.get("about").toString())
                            binding.editTextTextWebSite.setText(document.get("webSite").toString())
                            binding.phoneEditProfil.text=document.get("Phone").toString()
                            binding.genderEditProfil.text=document.get("Gender").toString()

                        }

                    }
                }

            }

    }




    fun doneEditProfil(view: View){
        println("Hata")
        val email=auth.currentUser!!.email.toString()
        val userCollection=db.collection("Users")

        val name=binding.editTextName.text.toString()
        val username=binding.editTextTextUsername.text.toString()
        val webSite=binding.editTextTextWebSite.text.toString()
        val about=binding.editTextTextAbout.text.toString()

        userCollection.whereEqualTo("email",email).get()
            .addOnSuccessListener { querySnapshot ->
                if (photoChange == true) {

                    if (name.isNotEmpty() && username.isNotEmpty() && webSite.isNotEmpty() && about.isNotEmpty() && selectedPicture != null) {
                        if(!querySnapshot.isEmpty){
                            val document = querySnapshot.documents[0]
                            val userId = document.id
                            userCollection.document(userId).update(
                                mapOf(
                                    "name" to name,
                                    "userName" to username,
                                    "webSite" to webSite,
                                    "about" to about,
                                    "downloadUrl" to selectedPicture
                                )
                            ).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener { e ->
                                // Güncelleme sırasında hata oluşursa yapılacak işlemler
                                println("Kullanıcı bilgileri güncellenirken hata oluştu: ${e.message}")
                            }

                        }
                    } else {
                        Toast.makeText(
                            this@EditProfile,
                            "Lütfen Gerkli alanları doldurun",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else {
                    if (name.isNotEmpty() && username.isNotEmpty() && webSite.isNotEmpty() && about.isNotEmpty()) {
                        if(!querySnapshot.isEmpty){
                            val document = querySnapshot.documents[0]
                            val userId = document.id
                            userCollection.document(userId).update(
                                mapOf(
                                    "name" to name,
                                    "userName" to username,
                                    "webSite" to webSite,
                                    "about" to about
                                )
                            ).addOnSuccessListener {
                                finish()
                            }.addOnFailureListener { e ->
                                // Güncelleme sırasında hata oluşursa yapılacak işlemler
                                println("Kullanıcı bilgileri güncellenirken hata oluştu: ${e.message}")
                            }

                        }
                    }
                    }
                }


    }

    fun cancelEditProfil(view:View){
        //Edit profile sayfasında güncelleme yapılmak istenmese yapılacakları söylüyor.
        finish()

    }

    fun changeProfilPhoto(view: View){
        photoChange=true
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
