package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    // Trades
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<Trade>>

    @Query("SELECT * FROM trades WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTradesByUserId(userId: String): Flow<List<Trade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: Trade)

    @Update
    suspend fun updateTrade(trade: Trade)

    @Delete
    suspend fun deleteTrade(trade: Trade)

    @Query("SELECT * FROM trades WHERE id = :id")
    suspend fun getTradeById(id: Int): Trade?

    // Challenges
    @Query("SELECT * FROM challenges ORDER BY timestamp DESC")
    fun getAllChallenges(): Flow<List<Challenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: Challenge)

    @Update
    suspend fun updateChallenge(challenge: Challenge)

    @Delete
    suspend fun deleteChallenge(challenge: Challenge)

    // Checklist
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getAllChecklistItems(): Flow<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(checklistItem: ChecklistItem)

    @Update
    suspend fun updateChecklistItem(checklistItem: ChecklistItem)

    @Delete
    suspend fun deleteChecklistItem(checklistItem: ChecklistItem)

    // Users
    @Query("SELECT * FROM users ORDER BY email ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE sessionToken = :token LIMIT 1")
    suspend fun getUserBySessionToken(token: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Update
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)

    // Subscription Plans
    @Query("SELECT * FROM subscription_plans ORDER BY name ASC")
    fun getAllSubscriptionPlans(): Flow<List<SubscriptionPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptionPlan(plan: SubscriptionPlan)

    // System Settings
    @Query("SELECT * FROM system_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSystemSetting(key: String): SystemSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSystemSetting(setting: SystemSetting)
}
