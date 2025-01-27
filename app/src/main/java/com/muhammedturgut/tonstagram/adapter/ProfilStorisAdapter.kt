package com.muhammedturgut.tonstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.databinding.RecyclerRow1Binding
import com.muhammedturgut.tonstagram.databinding.RecyclerRow3Binding
import com.muhammedturgut.tonstagram.model.ProfilModel
import com.muhammedturgut.tonstagram.model.ProfilStorisModel
import com.squareup.picasso.Picasso

class ProfilStorisAdapter(val storisProfilList:ArrayList<ProfilStorisModel>):RecyclerView.Adapter<ProfilStorisAdapter.StorisHolder>() {
    class StorisHolder(val binding: RecyclerRow3Binding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorisHolder {
       val binding=RecyclerRow3Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StorisHolder(binding)
    }

    override fun getItemCount(): Int {
        return storisProfilList.size
    }

    override fun onBindViewHolder(holder: StorisHolder, position: Int) {
        Picasso.get().load(storisProfilList.get(position).imageUrl).transform(SquareTransformation()).into(holder.binding.storisPhoto)
    }
}