package com.example.chatapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.ChatFragment.Companion.receiverImage
import com.example.chatapplication.ChatFragment.Companion.userImage
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(val context: Context, val data: MutableList<ChatHolder>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class SenderViewHolder(val view : View): RecyclerView.ViewHolder(view) {

        companion object {

        }
        val senderImage: CircleImageView = view.findViewById(R.id.sender)
        val senderText: TextView = view.findViewById(R.id.sender_msg)

    }
    class ReceiverViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        val receiverImage: CircleImageView = view.findViewById(R.id.receiver)
        val receiverText: TextView = view.findViewById(R.id.receiver_msg)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1) {
            val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.sender_layout, parent, false)
            return SenderViewHolder(adapterLayout)
        }
        else {
            val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.reciever_layout, parent, false)
            return ReceiverViewHolder(adapterLayout)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       val dataMsg = data[position]
        if(holder.javaClass == SenderViewHolder::class.java) {

            val senderHolder : SenderViewHolder = holder as SenderViewHolder
            senderHolder.senderText.text = dataMsg.message
            Picasso.get().load(userImage).into(senderHolder.senderImage)


        } else {
            val receiverHolder : ReceiverViewHolder = holder as ReceiverViewHolder
            receiverHolder.receiverText.text = dataMsg.message
            Picasso.get().load(receiverImage).into(receiverHolder.receiverImage)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        val dataMsg = data[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(dataMsg.senderId)) {
            1
        } else 2
    }


}