package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val role: String, // "ADMIN", "TRADER", "DEMO"
    val isBanned: Boolean = false,
    val isVerified: Boolean = false,
    val displayName: String = "",
    val subscriptionType: String = "FREE", // "FREE", "PREMIUM"
    val createdAt: Long = System.currentTimeMillis(),
    val isTrialEligible: Boolean = true,
    val sessionToken: String? = null // Simulated JWT/Session management
)
