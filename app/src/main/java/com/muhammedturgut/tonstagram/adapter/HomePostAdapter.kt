package com.muhammedturgut.tonstagram.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammedturgut.tonstagram.databinding.RecyclerRow2Binding

import com.muhammedturgut.tonstagram.model.HomeModel
import com.squareup.picasso.Picasso

class HomePostAdapter(val postHomeList: ArrayList<HomeModel>):RecyclerView.Adapter<HomePostAdapter.HomePostHolder>() {
    class HomePostHolder(val binding: RecyclerRow2Binding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostHolder {
    val binding=RecyclerRow2Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomePostHolder(binding)
    }

    override fun getItemCount(): Int {
        //recyclerView içerisinde gösterilecek veri saysını belirliyor.
        return postHomeList.size
    }

    override fun onBindViewHolder(holder: HomePostHolder, position: Int) {
       holder.binding.userNameMainPage.text=postHomeList.get(position).userName
        holder.binding.userNameCommentMainPage.text=postHomeList.get(position).userName
        holder.binding.commentMainPage.text=postHomeList.get(position).comment

        Picasso.get().load(postHomeList.get(position).profilPhoto).into(holder.binding.profilPhotoMainPage)
        Picasso.get().load(postHomeList.get(position).postImage).into(holder.binding.recyclerRowImageMainPage)

    }
}