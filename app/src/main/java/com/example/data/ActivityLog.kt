package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val action: String, // "LOGIN_SUCCESS", "LOGIN_FAILED", "SIGNUP", "PASSWORD_RESET", "BAN_USER", "UNBAN_USER", "TRADE_CREATE", "TRADE_DELETE", "PREMIUM_PURCHASE", "ANNOUNCEMENT_SENT"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
