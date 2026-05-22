package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pair: String,              // e.g. "EURUSD", "BTCUSD", "GBPUSD"
    val isBuy: Boolean,            // true = BUY, false = SELL
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val lotSize: Double,
    val riskPercent: Double,       // e.g., 1.0% or 0.5%
    val resultType: String,        // "WIN", "LOSS", "BREAKEVEN"
    val profitResult: Double,      // PnL amount (e.g. +250.00, -100.0)
    val timestamp: Long,           // Date and time of trade
    val session: String,          // "London", "New York", "Asia"
    val smcTags: String = "",      // Comma-separated SMC tags (e.g. "BOS,Order Block,FVG")
    val preTradeNotes: String = "",
    val postTradeNotes: String = "",
    val emotion: String = "Calm",  // "Calm", "Fear", "Greed", "Anxious", "Disciplined"
    val strategyUsed: String = "Order Block Tap", // e.g. "Order Block Tap", "Liquidity Sweep", "FVG Mitigation"
    val screenshotUrl: String = "", // visual marker or diagram reference
    val userId: String = "trader@smartmoney.com" // Link trade to a specific user
)
