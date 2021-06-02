package com.example.chatapplication.viewModel

import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    private var chatName: String = ""
    private lateinit var chatImage: String
    private var chatUid: String = ""

     fun setName(name: String) {
         chatName = name
     }
    fun setUid(uid: String) {
        chatUid = uid
    }
    fun setImage(image: String) {
        chatImage = image
    }

    fun getName() : String {
        return chatName
    }
    fun getImage() : String {
        return chatImage
    }
    fun getUid() :String {
        return chatUid
    }
}