package com.muhammedturgut.tonstagram.Fragments

import ProfilAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.View.EditProfile
import com.muhammedturgut.tonstagram.adapter.ProfilStorisAdapter
import com.muhammedturgut.tonstagram.databinding.FragmentProfilBinding
import com.muhammedturgut.tonstagram.model.ProfilModel
import com.muhammedturgut.tonstagram.model.ProfilStorisModel
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profilPostArrayList: ArrayList<ProfilModel>
    private lateinit var profilAdapter: ProfilAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null

    private lateinit var profilStoryArrayList: ArrayList<ProfilStorisModel>
    private lateinit var storyAdapter: ProfilStorisAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ActivityResultLauncher ve PermissionLauncher'ı başlat
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profilPostArrayList = ArrayList()
        profilStoryArrayList = ArrayList() // Profil story listesi başlatıldı

        binding.editProfilButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }

        profileInformation()

        binding.recyclerViewProfilPost.layoutManager = GridLayoutManager(context, 3)
        profilAdapter = ProfilAdapter(profilPostArrayList)
        binding.recyclerViewProfilPost.adapter = profilAdapter

        binding.recyclerViewProfilStoris.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        storyAdapter = ProfilStorisAdapter(profilStoryArrayList)
        binding.recyclerViewProfilStoris.adapter = storyAdapter

        getDataPosts()
        getDataStoris() // getDataStoris fonksiyonunu çağır

        binding.editProfilPhoto.setOnClickListener {
            binding.editProfilPhoto.setBackgroundResource(R.drawable.storis_look_bg)
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
        }

        return view
    }

    private fun uploadStories() {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val storageReference = storage.reference
        val imageReference = storageReference.child("images/$imageName")

        selectedPicture?.let {
            imageReference.putFile(it)
                .addOnSuccessListener {
                    imageReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveImageUrlToFirestore(imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val userEmail = auth.currentUser?.email ?: return

        // Kullanıcı adını almak için Firestore sorgusu
        db.collection("Users").whereEqualTo("email", userEmail).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val userName = document.getString("name") ?: ""

                    val storyMap = hashMapOf(
                        "userId" to userId,
                        "userName" to userName,
                        "imageUrl" to imageUrl,
                        "timestamp" to System.currentTimeMillis()
                    )

                    db.collection("Users").document(userId)
                        .collection("Stories")
                        .add(storyMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to save image URL: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
    }

    private fun profileInformation() {
        val email = auth.currentUser?.email ?: return
        val userCollection = db.collection("Users")

        userCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    val docRef = userCollection.document(documentId)
                    docRef.get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val imageProfileUrl = document.getString("downloadUrl")

                            imageProfileUrl?.let {
                                Picasso.get()
                                    .load(it)
                                    .transform(SquareTransformation())
                                    .into(binding.editProfilPhoto)
                            }

                            binding.editTextName.setText(document.getString("name"))
                            binding.aboutTextView.text = document.getString("about")
                        }
                    }
                }
            }
    }

    private fun getDataPosts() {
        val userEmail = auth.currentUser?.email ?: return
        db.collection("Posts")
            .whereEqualTo("email", userEmail)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println("Error: ${error.message}")
                } else {
                    value?.let {
                        if (!it.isEmpty) {
                            val documents = it.documents
                            profilPostArrayList.clear()
                            for (document in documents) {
                                val downloadUrl = document.getString("downloadUrl") ?: ""
                                val post = ProfilModel(downloadUrl)
                                profilPostArrayList.add(post)
                            }
                            profilAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }

    private fun getDataStoris() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("Users").document(userId).collection("Stories")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println("Error: ${error.message}")
                } else {
                    value?.let {
                        val documents = it.documents
                        profilStoryArrayList.clear()
                        for (document in documents) {
                            val downloadUrl = document.getString("imageUrl") ?: ""
                            val story = ProfilStorisModel(downloadUrl)
                            profilStoryArrayList.add(story)
                        }
                        storyAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                intentFromResult?.data?.let {
                    selectedPicture = it
                    binding.editProfilPhoto.setImageURI(it)
                    uploadStories()  // Resmi seçtikten sonra yükleme işlemini başlat
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
            }
        }
    }

}