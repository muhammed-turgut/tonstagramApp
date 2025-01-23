package com.muhammedturgut.tonstagram.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.adapter.HomePostAdapter
import com.muhammedturgut.tonstagram.databinding.FragmentHomeBinding
import com.muhammedturgut.tonstagram.model.HomeModel
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var homePostArrayList: ArrayList<HomeModel>
    private lateinit var homePostAdapter: HomePostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        homePostArrayList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // RecyclerView ve Adapter ayarları
        binding.recyclerViewHomePosts.layoutManager = LinearLayoutManager(requireContext())
        homePostAdapter = HomePostAdapter(homePostArrayList)
        binding.recyclerViewHomePosts.adapter = homePostAdapter

        // Firestore'dan verileri çekme fonksiyonunu çağır
        getData()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getData() {

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val documents = value.documents
                homePostArrayList.clear()
                for (document in documents) {
                    val comment = document.getString("comment") ?: ""
                    val userName = document.getString("userName") ?: ""
                    val profilPhoto = document.getString("profilPhoto") ?: ""
                    val postsHome = document.getString("downloadUrl") ?: ""



                    val post = HomeModel(userName, comment, profilPhoto, postsHome)
                    homePostArrayList.add(post)
                }
                homePostAdapter.notifyDataSetChanged()
            }
        }
    }
}