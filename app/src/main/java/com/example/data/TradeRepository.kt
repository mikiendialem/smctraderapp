package com.example.data

import kotlinx.coroutines.flow.Flow

class TradeRepository(private val tradeDao: TradeDao) {

    val allTrades: Flow<List<Trade>> = tradeDao.getAllTrades()
    val allChallenges: Flow<List<Challenge>> = tradeDao.getAllChallenges()
    val allChecklistItems: Flow<List<ChecklistItem>> = tradeDao.getAllChecklistItems()
    val allUsers: Flow<List<User>> = tradeDao.getAllUsers()
    val allActivityLogs: Flow<List<ActivityLog>> = tradeDao.getAllActivityLogs()
    val allNotifications: Flow<List<Notification>> = tradeDao.getAllNotifications()
    val allSubscriptionPlans: Flow<List<SubscriptionPlan>> = tradeDao.getAllSubscriptionPlans()

    fun getTradesByUserId(userId: String): Flow<List<Trade>> = tradeDao.getTradesByUserId(userId)

    suspend fun insertTrade(trade: Trade) {
        tradeDao.insertTrade(trade)
    }

    suspend fun updateTrade(trade: Trade) {
        tradeDao.updateTrade(trade)
    }

    suspend fun deleteTrade(trade: Trade) {
        tradeDao.deleteTrade(trade)
    }

    suspend fun getTradeById(id: Int): Trade? {
        return tradeDao.getTradeById(id)
    }

    suspend fun insertChallenge(challenge: Challenge) {
        tradeDao.insertChallenge(challenge)
    }

    suspend fun updateChallenge(challenge: Challenge) {
        tradeDao.updateChallenge(challenge)
    }

    suspend fun deleteChallenge(challenge: Challenge) {
        tradeDao.deleteChallenge(challenge)
    }

    suspend fun insertChecklistItem(checklistItem: ChecklistItem) {
        tradeDao.insertChecklistItem(checklistItem)
    }

    suspend fun updateChecklistItem(checklistItem: ChecklistItem) {
        tradeDao.updateChecklistItem(checklistItem)
    }

    suspend fun deleteChecklistItem(checklistItem: ChecklistItem) {
        tradeDao.deleteChecklistItem(checklistItem)
    }

    // Users
    suspend fun getUserByEmail(email: String): User? = tradeDao.getUserByEmail(email)
    suspend fun getUserBySessionToken(token: String): User? = tradeDao.getUserBySessionToken(token)
    suspend fun insertUser(user: User): Long = tradeDao.insertUser(user)
    suspend fun updateUser(user: User) = tradeDao.updateUser(user)
    suspend fun deleteUser(user: User) = tradeDao.deleteUser(user)

    // Activity Logs
    suspend fun logActivity(email: String, action: String, details: String) {
        tradeDao.insertActivityLog(ActivityLog(email = email, action = action, details = details))
    }

    // Notifications
    suspend fun insertNotification(notification: Notification) = tradeDao.insertNotification(notification)
    suspend fun updateNotification(notification: Notification) = tradeDao.updateNotification(notification)
    suspend fun deleteNotification(notification: Notification) = tradeDao.deleteNotification(notification)

    // Subscription Plans
    suspend fun insertSubscriptionPlan(plan: SubscriptionPlan) = tradeDao.insertSubscriptionPlan(plan)

    // System Settings
    suspend fun getSystemSetting(key: String): SystemSetting? = tradeDao.getSystemSetting(key)
    suspend fun insertSystemSetting(key: String, value: String) {
        tradeDao.insertSystemSetting(SystemSetting(key, value))
    }
}
