package com.muhammedturgut.tonstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammedturgut.tonstagram.SquareTransformation
import com.muhammedturgut.tonstagram.databinding.RecyclerRow4Binding
import com.muhammedturgut.tonstagram.model.HomeStorisModel
import com.squareup.picasso.Picasso

class HomeStrorisAdapter(val homeStorisList:ArrayList<HomeStorisModel>):RecyclerView.Adapter<HomeStrorisAdapter.homeStorisHolder>() {
    class homeStorisHolder(val binding:RecyclerRow4Binding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): homeStorisHolder {
        val binding=RecyclerRow4Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return homeStorisHolder(binding)
    }

    override fun getItemCount(): Int {
       return homeStorisList.size
    }

    override fun onBindViewHolder(holder: homeStorisHolder, position: Int) {
        holder.binding.userName.text=homeStorisList.get(position).userName
        Picasso.get().load(homeStorisList.get(position).dowlandUrl).transform(SquareTransformation()).into(holder.binding.storisPhoto)
    }
}