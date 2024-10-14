package com.edsonlimadev.chatapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val userSenderId: String = "",
    val message: String = "",
    @ServerTimestamp
    val date: Date? = null
)
