package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscription_plans")
data class SubscriptionPlan(
    @PrimaryKey val planId: String, // "FREE", "PREMIUM", "PRO"
    val name: String,
    val price: Double,
    val limitTrades: Int, // Max allowed trades
    val features: String // Comma-separated features string
)
