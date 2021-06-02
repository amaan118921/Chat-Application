package com.example.chatapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapplication.databinding.FragmentChatBinding
import com.example.chatapplication.viewModel.ChatViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.util.*


class ChatFragment : Fragment() {

    private lateinit var binding : FragmentChatBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var chatReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var senderRoom:String
    private lateinit var receiverRoom: String
    private lateinit var senderId: String
    private lateinit var receiverId:String

        companion object {
             lateinit var userImage: String
             lateinit var receiverImage: String
        }

    private val Model: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentChatBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val chatData: MutableList<ChatHolder> = mutableListOf()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("user").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userImage = snapshot.child("imageUri").value.toString()
                receiverImage = Model.getImage()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        senderId = auth.uid.toString()
        receiverId = Model.getUid()

        binding.chatName.text = Model.getName()
        Picasso.get().load(Model.getImage().toUri()).into(binding.chatImage)
        senderRoom = senderId + receiverId
        receiverRoom = receiverId + senderId

        binding.send.setOnClickListener {
            val message = binding.edtMsg.text.toString()
            if(message.isEmpty()) {
                binding.edtMsg.error
            }
            else {
                binding.edtMsg.setText("")
                val date = Date()
                val msg = ChatHolder(message, auth.uid!!, date.time)
                reference = database.reference.child("chats").child(senderRoom).child("messages")
                reference.push().setValue(msg).addOnCompleteListener(object: OnCompleteListener<Void>{
                    override fun onComplete(task: Task<Void>) {
                        reference = database.reference.child("chats").child(receiverRoom).child("messages")
                        reference.push().setValue(msg)
                    }

                })
            }

        }

        chatReference = database.reference.child("chats").child(senderRoom).child("messages")


        chatReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatData.clear()
                for(dataSnapshot: DataSnapshot in snapshot.children) {
                    val message : ChatHolder? = dataSnapshot.getValue(ChatHolder::class.java)
                    chatData.add(message!!)
                }
                binding.chatRecyclerView.adapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.chatRecyclerView.adapter = ChatAdapter(this.requireActivity(), chatData)
        val layoutManager = LinearLayoutManager(this.requireActivity())
        layoutManager.stackFromEnd = true
        layoutManager.stackFromEnd = true

        binding.chatRecyclerView.layoutManager = layoutManager





    }
}