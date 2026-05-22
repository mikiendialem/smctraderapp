package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val type: String, // "ANNOUNCEMENT", "ALERT", "SYSTEM"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
