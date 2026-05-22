package com.example.data

data class UserProfile(
    val username: String,
    val email: String,
    val balance: Double,
    val bio: String
)

data class EconomicEvent(
    val title: String,
    val impact: String,
    val currency: String,
    val time: String,
    val consensus: String,
    val previous: String
)

data class ChatMessage(
    val sender: String,
    val message: String,
    val isUser: Boolean
)
