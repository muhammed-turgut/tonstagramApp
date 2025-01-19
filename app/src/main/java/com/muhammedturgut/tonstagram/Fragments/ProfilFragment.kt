package com.muhammedturgut.tonstagram.Fragments

import ProfilAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muhammedturgut.tonstagram.R
import com.muhammedturgut.tonstagram.View.EditProfile
import com.muhammedturgut.tonstagram.databinding.FragmentProfilBinding
import com.muhammedturgut.tonstagram.model.ProfilModel


class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profilPostArrayList: ArrayList<ProfilModel>
    private lateinit var profilAdapter: ProfilAdapter

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
        getData()

        binding.editProfilButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfile::class.java)
            startActivity(intent)
        }

        binding.recyclerViewProfilPost.layoutManager = GridLayoutManager(context, 3)
        profilAdapter = ProfilAdapter(profilPostArrayList)
        binding.recyclerViewProfilPost.adapter = profilAdapter

        return view
    }

    private fun getData() {
        // Example Firestore query
        db.collection("Posts")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post = document.toObject(ProfilModel::class.java)
                    profilPostArrayList.add(post)
                }
                profilAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}