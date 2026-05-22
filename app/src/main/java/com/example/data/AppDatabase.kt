package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Trade::class,
        Challenge::class,
        ChecklistItem::class,
        User::class,
        ActivityLog::class,
        Notification::class,
        SubscriptionPlan::class,
        SystemSetting::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smartmoney_db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration() // Handle easy recreation during development updates
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.tradeDao())
                }
            }
        }

        suspend fun populateDatabase(dao: TradeDao) {
            // Prepopulate Checklist Items
            dao.insertChecklistItem(ChecklistItem(title = "HTF Bias Checked (4H / Daily)", category = "Pre-Trade"))
            dao.insertChecklistItem(ChecklistItem(title = "Liquidity Pool Swept", category = "Pre-Trade"))
            dao.insertChecklistItem(ChecklistItem(title = "Market Structure Shift / CHoCH", category = "Confirmation"))
            dao.insertChecklistItem(ChecklistItem(title = "Order Block / FVG Position Tapped", category = "Confirmation"))
            dao.insertChecklistItem(ChecklistItem(title = "Risk-Reward calculated correctly (> 1:3)", category = "Risk Management"))
            dao.insertChecklistItem(ChecklistItem(title = "Emotion Level Checked (No Fear/Greed)", category = "Risk Management"))

            // Prepopulate Sample Challenge
            dao.insertChallenge(
                Challenge(
                    name = "FTMO $100K Challenge (Phase 1)",
                    startingBalance = 100000.00,
                    currentBalance = 102450.00,
                    targetProfitPercent = 8.0,
                    isActive = true
                )
            )

            // Seed Subscription Plans
            dao.insertSubscriptionPlan(
                SubscriptionPlan(
                    planId = "FREE",
                    name = "Free Tier",
                    price = 0.0,
                    limitTrades = 5,
                    features = "Basic Trade Journal,SMC Dynamic Charts,Emotional Coach (1 request/day)"
                )
            )
            dao.insertSubscriptionPlan(
                SubscriptionPlan(
                    planId = "PREMIUM",
                    name = "Premium Pro",
                    price = 29.99,
                    limitTrades = 9999,
                    features = "Unlimited Trade Logs,Unlimited AI Psychological Dialogues,Advanced Heatmaps,Advanced SMC Formations"
                )
            )

            // Seed System Settings
            dao.insertSystemSetting(SystemSetting("AI_COACH_ENABLED", "true"))
            dao.insertSystemSetting(SystemSetting("AI_COACH_TONE", "Empathetic Professional")) // e.g. "Direct Coach" or "Empathetic Professional"
            dao.insertSystemSetting(SystemSetting("DEMO_MODE_ACTIVE", "false"))
            dao.insertSystemSetting(SystemSetting("GLOBAL_NOTIFICATIONS_ENABLED", "true"))

            // Seed Users
            // Password: SecureSMCAdmin!2026 (hashed represented plain for quick simulation)
            dao.insertUser(
                User(
                    email = "admin@smartmoney.com",
                    passwordHash = "SecureSMCAdmin!2026",
                    role = "ADMIN",
                    isVerified = true,
                    displayName = "Apex Admin Manager",
                    subscriptionType = "PREMIUM"
                )
            )
            dao.insertUser(
                User(
                    email = "trader@smartmoney.com",
                    passwordHash = "password123",
                    role = "TRADER",
                    isVerified = true,
                    displayName = "SMC Elite Trader",
                    subscriptionType = "FREE"
                )
            )
            dao.insertUser(
                User(
                    email = "demo@smartmoney.com",
                    passwordHash = "password123",
                    role = "DEMO",
                    isVerified = false,
                    displayName = "Simulator Account",
                    subscriptionType = "FREE"
                )
            )

            // Seed System Announcements/Notifications
            dao.insertNotification(
                Notification(
                    title = "System Anniversary Launch",
                    message = "Smart Money Hub is officially updated to modern M3 UI. Premium subscription plans are now live with automated trial capabilities.",
                    type = "ANNOUNCEMENT"
                )
            )
            dao.insertNotification(
                Notification(
                    title = "AI Model Upgraded",
                    message = "Your AI Dynamic Psychological helper is now operating on Gemini 1.5 Flash. Fast, customized prompts for high-emotion sessions.",
                    type = "SYSTEM"
                )
            )

            // Prepopulate Sample Trades (All owned by trader@smartmoney.com)
            val now = System.currentTimeMillis()
            dao.insertTrade(
                Trade(
                    pair = "EURUSD",
                    isBuy = true,
                    entryPrice = 1.0854,
                    stopLoss = 1.0842,
                    takeProfit = 1.0890,
                    lotSize = 5.0,
                    riskPercent = 1.0,
                    resultType = "WIN",
                    profitResult = 1800.0,
                    timestamp = now - 3 * 3600 * 1000, // 3 hours ago
                    session = "London",
                    smcTags = "BOS,Order Block,Liquidity Sweep",
                    preTradeNotes = "London liquidity sweep of Asian Session low followed by a clear CHoCH on 5m chart.",
                    postTradeNotes = "Executed beautifully. Price hit TP within 45 minutes of New York Open.",
                    emotion = "Calm",
                    strategyUsed = "Liquidity Sweep",
                    userId = "trader@smartmoney.com"
                )
            )
            dao.insertTrade(
                Trade(
                    pair = "BTCUSD",
                    isBuy = false,
                    entryPrice = 64500.0,
                    stopLoss = 65100.0,
                    takeProfit = 62500.0,
                    lotSize = 0.5,
                    riskPercent = 1.0,
                    resultType = "LOSS",
                    profitResult = -300.0,
                    timestamp = now - 24 * 3600 * 1000, // 1 day ago
                    session = "New York",
                    smcTags = "FVG,Order Block",
                    preTradeNotes = "Re-entry short on BTC FVG tap on high time frame supply block.",
                    postTradeNotes = "Price aggressively pushed through supply. Stop loss hit. Possible over-leverage or bad timing.",
                    emotion = "Anxious",
                    strategyUsed = "FVG Mitigation",
                    userId = "trader@smartmoney.com"
                )
            )
            dao.insertTrade(
                Trade(
                    pair = "GBPUSD",
                    isBuy = true,
                    entryPrice = 1.2640,
                    stopLoss = 1.2610,
                    takeProfit = 1.2730,
                    lotSize = 3.0,
                    riskPercent = 1.0,
                    resultType = "WIN",
                    profitResult = 950.0,
                    timestamp = now - 48 * 3600 * 1000, // 2 days ago
                    session = "London",
                    smcTags = "CHoCH,OTE Entry",
                    preTradeNotes = "Retracement to 61.8% fib level, overlapping with London Open MSS.",
                    postTradeNotes = "Smooth movement. Set and forget. Emotion was highly disciplined.",
                    emotion = "Disciplined",
                    strategyUsed = "OTE Entry",
                    userId = "trader@smartmoney.com"
                )
            )

            // Insert system seed admin log
            dao.insertActivityLog(
                ActivityLog(
                    email = "admin@smartmoney.com",
                    action = "SYSTEM_INITIALIZED",
                    details = "Seed roles and subscription plan matrices prepopulated successfully into database."
                )
            )
        }
    }
}
