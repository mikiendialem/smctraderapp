package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,                  // e.g. "FTMO $100K Challenge"
    val startingBalance: Double,       // e.g. 100000.00
    val targetProfitPercent: Double = 8.0, // Target e.g., 8.0 % ($8000)
    val maxDailyLossPercent: Double = 5.0,  // Max daily drawdown limit
    val maxOverallLossPercent: Double = 10.0, // Max total drawdown limit
    val currentBalance: Double,        // e.g., 102500.00
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
