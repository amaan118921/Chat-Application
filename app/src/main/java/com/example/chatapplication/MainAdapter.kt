package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chatapplication.viewModel.ChatViewModel

class MainAdapter(private val context: Context, private val fragmentContext: Fragment, private val data:MutableList<DataHolder>, private val viewModel: ChatViewModel): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    class MainViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById(R.id.item_profile_image)
        val name: TextView = view.findViewById(R.id.item_name)
        val status: TextView = view.findViewById(R.id.item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context,).inflate(R.layout.main_item, parent, false)
        return MainViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val userData = data[position]
        holder.name.text = userData.name
        holder.status.text = userData.status
        Glide.with(context).load(userData.imageUri).into(holder.imgView)
        holder.itemView.setOnClickListener{
            viewModel.setImage(userData.imageUri)
            viewModel.setName(userData.name)
            viewModel.setUid(userData.uid)
            fragmentContext.findNavController().navigate(R.id.action_mainScreenFragment_to_chatFragment)
        }


    }

    override fun getItemCount(): Int {
       return data.size
    }
}