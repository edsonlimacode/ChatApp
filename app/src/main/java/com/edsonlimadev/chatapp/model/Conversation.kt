package com.edsonlimadev.chatapp.model

data class Conversation(
    val userSenderId: String = "",
    val userReceiverId: String = "",
    val avatarReceiver: String = "",
    val receiverName: String = "",
    val message: String = ""
)
