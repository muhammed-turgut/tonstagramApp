package com.muhammedturgut.tonstagram.Fragments


import ProfilAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.View.EditProfile
import com.muhammedturgut.tonstagram.databinding.FragmentProfilBinding
import com.muhammedturgut.tonstagram.model.ProfilModel
import com.squareup.picasso.Picasso


class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profilPostArrayList: ArrayList<ProfilModel>
    private lateinit var profilAdapter: ProfilAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()



        profilPostArrayList = ArrayList()

        binding.editProfilButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }

        profileInfirmation()

        binding.recyclerViewProfilPost.layoutManager = GridLayoutManager(context, 3)
        profilAdapter = ProfilAdapter(profilPostArrayList)
        binding.recyclerViewProfilPost.adapter = profilAdapter

        getData()

        return view
    }

    private fun profileInfirmation() {
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
                            binding.aboutTextView.setText(document.get("about").toString())


                        }

                    }
                }

            }
    }

    private fun getData() {
        val userEmail = auth.currentUser?.email.toString() // Mevcut kullanıcının e-posta adresini al
        db.collection("Posts")
            .whereEqualTo("email", userEmail)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->

                if (error != null) {
                    println("Hata mesaje 1")

                } else {
                    if (value != null) {
                        if (!value.isEmpty) {

                            val documents = value.documents

                            profilPostArrayList.clear()
                            for (document in documents) {


                                val downloadUrl = document.get("downloadUrl") as? String ?: ""

                                val post = ProfilModel(downloadUrl)
                                profilPostArrayList.add(post)
                            }

                            // Güncelleme olursa güncellemeyi yapması için
                            profilAdapter.notifyDataSetChanged()

                        }
                    }
                }
            }
    }
}