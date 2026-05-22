package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class TradeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = TradeRepository(db.tradeDao())

    // --- Core Master Database Flows ---
    val allChallenges: StateFlow<List<Challenge>> = repository.allChallenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChecklistItems: StateFlow<List<ChecklistItem>> = repository.allChecklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allActivityLogs: StateFlow<List<ActivityLog>> = repository.allActivityLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubscriptionPlans: StateFlow<List<SubscriptionPlan>> = repository.allSubscriptionPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Session-specific Trade Flows (Secure filtering) ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val isUserLoggedIn: StateFlow<Boolean> = _currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userProfile: StateFlow<UserProfile> = _currentUser.map { user ->
        UserProfile(
            username = user?.displayName ?: "Guest Trader",
            email = user?.email ?: "guest@smartmoney.com",
            balance = 100000.0,
            bio = "Role: ${user?.role ?: "GUEST"} | Tier: ${user?.subscriptionType ?: "FREE"}"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile("SMC_Trader_2026", "guest@smartmoney.com", 10000.0, ""))

    val selectedTraderEmailForAdminView = MutableStateFlow<String>("All")

    // Dynamic reactive trades matching Role based access permissions!
    val uiTrades: StateFlow<List<Trade>> = combine(
        repository.allTrades,
        currentUser,
        selectedTraderEmailForAdminView
    ) { allTrades, user, selectedEmail ->
        if (user == null) {
            emptyList()
        } else if (user.role == "ADMIN") {
            // Admin sees everything or filters per user selection
            if (selectedEmail == "All") {
                allTrades
            } else {
                allTrades.filter { it.userId == selectedEmail }
            }
        } else {
            // Normal users only see their own logged trades
            allTrades.filter { it.userId == user.email }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Interactive Security & OTP States ---
    private val _otpNotification = MutableStateFlow<String?>(null)
    val otpNotification: StateFlow<String?> = _otpNotification.asStateFlow()

    private val _forgotPasswordNotification = MutableStateFlow<String?>(null)
    val forgotPasswordNotification: StateFlow<String?> = _forgotPasswordNotification.asStateFlow()

    private val _loginAttempts = MutableStateFlow(0)
    private val _lockoutTimeRemaining = MutableStateFlow(0) // Remaining seconds

    // --- Globally Synchronized System Control States ---
    private val _aiCoachEnabled = MutableStateFlow(true)
    val aiCoachEnabled: StateFlow<Boolean> = _aiCoachEnabled.asStateFlow()

    private val _aiCoachTone = MutableStateFlow("Empathetic Professional")
    val aiCoachTone: StateFlow<String> = _aiCoachTone.asStateFlow()

    private val _demoModeActive = MutableStateFlow(false)
    val demoModeActive: StateFlow<Boolean> = _demoModeActive.asStateFlow()

    private val _globalNotificationsEnabled = MutableStateFlow(true)
    val globalNotificationsEnabled: StateFlow<Boolean> = _globalNotificationsEnabled.asStateFlow()

    // --- Economic Calendar / News ---
    val economicNews = listOf(
        EconomicEvent("FOMC Interest Rate Decision", "High", "USD", "20:00", "+0.25% expected", "N/A"),
        EconomicEvent("US Core CPI MoM", "High", "USD", "13:30", "0.3%", "0.2%"),
        EconomicEvent("UK Claimant Count Change", "Medium", "GBP", "07:00", "12.5K", "8.9K"),
        EconomicEvent("EU ECB Monetary Policy Statement", "High", "EUR", "12:45", "N/A", "N/A"),
        EconomicEvent("USD Building Permits", "Low", "USD", "14:30", "1.42M", "1.45M")
    )

    // --- AI Assistant / Coaching Chat Flow ---
    private val _aiAdvice = MutableStateFlow<String?>(null)
    val aiAdvice: StateFlow<String?> = _aiAdvice.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiConversation = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("SmartMoney Coach", "Welcome back, Trader! I am loaded with SMC insights. Let me know if you would like to analyze your latest trading pattern, check for risk violations, or detect overtrading loops.", isUser = false)
    ))
    val aiConversation: StateFlow<List<ChatMessage>> = _aiConversation.asStateFlow()

    init {
        syncSystemSettings()
    }

    private fun syncSystemSettings() {
        viewModelScope.launch {
            val coachEnabled = repository.getSystemSetting("AI_COACH_ENABLED")?.value ?: "true"
            _aiCoachEnabled.value = coachEnabled.toBoolean()

            val toneSetting = repository.getSystemSetting("AI_COACH_TONE")?.value ?: "Empathetic Professional"
            _aiCoachTone.value = toneSetting

            val demoActive = repository.getSystemSetting("DEMO_MODE_ACTIVE")?.value ?: "false"
            _demoModeActive.value = demoActive.toBoolean()

            val globalNotif = repository.getSystemSetting("GLOBAL_NOTIFICATIONS_ENABLED")?.value ?: "true"
            _globalNotificationsEnabled.value = globalNotif.toBoolean()
        }
    }

    // --- Core Database Operation Actions ---
    fun insertTrade(trade: Trade, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) = viewModelScope.launch {
        val user = _currentUser.value ?: return@launch
        
        // Premium Limit Validation Checks
        if (user.subscriptionType == "FREE") {
            val userTradesCount = uiTrades.value.size
            if (userTradesCount >= 5) {
                onError("Free Tier account is limited to 5 trades. Upgrade to Premium Pro for unlimited logs!")
                return@launch
            }
        }

        // Attach user ownership to trade
        val userTrade = trade.copy(userId = user.email)
        repository.insertTrade(userTrade)
        repository.logActivity(user.email, "TRADE_CREATE", "Logged new trade for ${userTrade.pair} (${if(userTrade.isBuy) "BUY" else "SELL"}) with profit: $$${userTrade.profitResult}")
        updateActiveChallengesPnL(userTrade.profitResult)
        onSuccess()
    }

    fun updateTrade(trade: Trade) = viewModelScope.launch {
        val user = _currentUser.value ?: return@launch
        repository.updateTrade(trade)
        repository.logActivity(user.email, "TRADE_UPDATE", "Updated trade ID ${trade.id} details on ${trade.pair}")
    }

    fun deleteTrade(trade: Trade) = viewModelScope.launch {
        val user = _currentUser.value ?: return@launch
        repository.deleteTrade(trade)
        repository.logActivity(user.email, "TRADE_DELETE", "Deleted trade ID ${trade.id} from ledger")
        updateActiveChallengesPnL(-trade.profitResult) // reverse
    }

    fun insertChallenge(challenge: Challenge) = viewModelScope.launch {
        repository.insertChallenge(challenge)
    }

    fun updateChallenge(challenge: Challenge) = viewModelScope.launch {
        repository.updateChallenge(challenge)
    }

    fun deleteChallenge(challenge: Challenge) = viewModelScope.launch {
        repository.deleteChallenge(challenge)
    }

    fun toggleChecklistItem(item: ChecklistItem) = viewModelScope.launch {
        repository.updateChecklistItem(item.copy(isChecked = !item.isChecked))
    }

    fun addChecklistItem(title: String, category: String) = viewModelScope.launch {
        repository.insertChecklistItem(ChecklistItem(title = title, category = category))
    }

    fun deleteChecklistItem(item: ChecklistItem) = viewModelScope.launch {
        repository.deleteChecklistItem(item)
    }

    // --- Authentication System Logic ---

    fun loginUser(email: String, pass: String, onFinished: (String?) -> Unit) {
        viewModelScope.launch {
            // 1. Check Rate Limiting Lock State
            if (_lockoutTimeRemaining.value > 0) {
                onFinished("Secure authentication is locked. Please try again in ${_lockoutTimeRemaining.value} seconds.")
                return@launch
            }

            // 2. Query user by email
            val user = repository.getUserByEmail(email)
            if (user == null) {
                handleFailedLogin("Unknown email or password pattern", email, onFinished)
                return@launch
            }

            // 3. Confirm password
            if (user.passwordHash != pass) {
                handleFailedLogin("Incorrect password credentials", email, onFinished)
                return@launch
            }

            // 4. Check Ban Status
            if (user.isBanned) {
                repository.logActivity(email, "LOGIN_BLOCKED_BANNED", "Attempted login with a banned user credential")
                onFinished("Your account with email $email has been banned due to terms violations.")
                return@launch
            }

            // 5. Successful Login
            _loginAttempts.value = 0
            val mockJWTToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${UUID.randomUUID()}"
            val sessionUser = user.copy(sessionToken = mockJWTToken)
            repository.updateUser(sessionUser)

            _currentUser.value = sessionUser
            repository.logActivity(email, "LOGIN_SUCCESS", "Successfully authenticated session with mock token ending in ${mockJWTToken.takeLast(6)}")
            onFinished(null)
        }
    }

    private suspend fun handleFailedLogin(reason: String, email: String, onFinished: (String?) -> Unit) {
        _loginAttempts.update { it + 1 }
        repository.logActivity(email, "LOGIN_FAILED", "Failed authentication attempt: $reason")
        
        if (_loginAttempts.value >= 3) {
            triggerSecureRateLimit()
            onFinished("Authentication failed. Too consecutive invalid logins. Secure lockout enabled for 30s.")
        } else {
            onFinished(reason)
        }
    }

    private fun triggerSecureRateLimit() {
        _lockoutTimeRemaining.value = 30
        viewModelScope.launch {
            while (_lockoutTimeRemaining.value > 0) {
                kotlinx.coroutines.delay(1000)
                _lockoutTimeRemaining.update { it - 1 }
            }
        }
    }

    fun signUpUser(email: String, displayName: String, pass: String, role: String, adminPasscode: String = "", onFinished: (String?) -> Unit) {
        viewModelScope.launch {
            if (role == "ADMIN" && adminPasscode != "SMC_ADMIN_SECURE_2026") {
                onFinished("Access Denied: Invalid Admin Security Passcode.")
                return@launch
            }
            if (email.trim().isEmpty() || displayName.trim().isEmpty() || pass.trim().isEmpty()) {
                onFinished("Fields cannot be blank. Input validation failed.")
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                onFinished("Invalid email address formatting.")
                return@launch
            }
            if (pass.length < 6) {
                onFinished("Password is insecure. Must exceed 5 characters.")
                return@launch
            }

            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                onFinished("A user account is already registered with this email address.")
                return@launch
            }

            val newUser = User(
                email = email,
                displayName = displayName,
                passwordHash = pass,
                role = role,
                isVerified = false,
                subscriptionType = "FREE"
            )
            repository.insertUser(newUser)
            repository.logActivity(email, "SIGNUP", "Account created successfully with role $role and displayName $displayName")
            
            // Auto authenticate on signup
            val mockJWTToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.${UUID.randomUUID()}"
            val sessionUser = newUser.copy(sessionToken = mockJWTToken)
            repository.updateUser(sessionUser)
            _currentUser.value = sessionUser

            onFinished(null)
        }
    }

    fun simulateGoogleLogin(googleEmail: String, displayName: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserByEmail(googleEmail)
            if (existing != null) {
                if (existing.isBanned) {
                    repository.logActivity(googleEmail, "LOGIN_BLOCKED_BANNED", "Attempted Google login with banned user profile")
                    return@launch
                }
                val mockJWTToken = "google-jwt-${UUID.randomUUID()}"
                val sessionUser = existing.copy(isVerified = true, sessionToken = mockJWTToken)
                repository.updateUser(sessionUser)
                _currentUser.value = sessionUser
                repository.logActivity(googleEmail, "GOOGLE_LOGIN_SUCCESS", "Successfully matched Google account link")
            } else {
                val newUser = User(
                    email = googleEmail,
                    displayName = displayName,
                    passwordHash = "google-secret-prov",
                    role = "TRADER",
                    isVerified = true, // Google accounts automatically verified
                    subscriptionType = "FREE"
                )
                repository.insertUser(newUser)
                
                val mockJWTToken = "google-jwt-${UUID.randomUUID()}"
                val sessionUser = newUser.copy(sessionToken = mockJWTToken)
                repository.updateUser(sessionUser)
                _currentUser.value = sessionUser
                repository.logActivity(googleEmail, "GOOGLE_SIGNUP_SUCCESS", "Logged and registered new Google user profile")
            }
            onFinished()
        }
    }

    fun logoutUser() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.logActivity(user.email, "LOGOUT", "Terminated secure user session token safely")
            val resetUser = user.copy(sessionToken = null)
            repository.updateUser(resetUser)
            _currentUser.value = null
            selectedTraderEmailForAdminView.value = "All"
        }
    }

    fun requestEmailVerification() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val code = (1000 + (Math.random() * 9000).toInt()).toString()
            _otpNotification.value = code
            val notif = Notification(
                title = "Email Verification Pin",
                message = "The Verification PIN for your SmartMoney session (${user.email}) is: $code. Please enter it inside the widget.",
                type = "ALERT"
            )
            repository.insertNotification(notif)
            repository.logActivity(user.email, "EMAIL_VERIFICATION_OTP_SENT", "Simulated delivery of email verification code")
        }
    }

    fun submitEmailOTP(codeEntered: String): Boolean {
        val otp = _otpNotification.value
        val user = _currentUser.value
        if (user != null && otp != null && codeEntered == otp) {
            viewModelScope.launch {
                val updated = user.copy(isVerified = true)
                repository.updateUser(updated)
                _currentUser.value = updated
                _otpNotification.value = null
                repository.logActivity(user.email, "EMAIL_VERIFIED", "Successfully completed self email activation flow")
            }
            return true
        }
        return false
    }

    fun forgotPasswordUser(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                val tempPass = "SMC_Temp_${(100+ (Math.random()*899).toInt())}"
                _forgotPasswordNotification.value = "A recovery notification pin has been directed to the announcements. Your simulated temporary password is: $tempPass"
                
                val secureUser = user.copy(passwordHash = tempPass)
                repository.updateUser(secureUser)

                val announcement = Notification(
                    title = "Recovery PIN [SmartMoney Care]",
                    message = "Simulated security recovery mail for $email. Temporary login password sets to: $tempPass. Please modify upon authenticating.",
                    type = "SYSTEM"
                )
                repository.insertNotification(announcement)
                repository.logActivity(email, "PASSWORD_RESET_OTP_TRIGGER", "Simulated security instructions sent for account recovery")
            } else {
                _forgotPasswordNotification.value = "No account found matching $email credentials."
            }
        }
    }

    fun clearForgotPasswordNotification() {
        _forgotPasswordNotification.value = null
    }

    // --- Premium Upgrades Payments Simulation ---

    fun upgradeCurrentUserPremium(cardNumber: String, expiry: String, cvc: String, onFinished: (String?) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (cardNumber.length < 16) {
                onFinished("Invalid card number size. Security validation failed.")
                return@launch
            }
            if (expiry.length < 5) {
                onFinished("Invalid expiry date pattern.")
                return@launch
            }
            if (cvc.length < 3) {
                onFinished("Invalid CVC security security digits.")
                return@launch
            }

            // Successfully processed!
            val updatedUser = user.copy(subscriptionType = "PREMIUM")
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser

            repository.logActivity(user.email, "PREMIUM_PURCHASE", "Upgraded account safely to Premium Pro. Mock receipt: receipt_sm_${UUID.randomUUID().toString().take(8)}")
            
            val confirmationNotif = Notification(
                title = "Payment Processed successfully!",
                message = "Congratulations, ${user.displayName}! Your premium membership of $29.99/mo is now fully operational. Limits are completely unlocked.",
                type = "ALERT"
            )
            repository.insertNotification(confirmationNotif)
            onFinished(null)
        }
    }

    fun restoreFreeUser() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(subscriptionType = "FREE")
            repository.updateUser(updated)
            _currentUser.value = updated
            repository.logActivity(user.email, "RESTORE_FREE", "Downgraded account back to Free tier for development check")
        }
    }

    // --- Admin Dashboard Operation Actions ---

    fun toggleUserBanState(user: User) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch
            if (user.role == "ADMIN") return@launch // Cannot ban other admins

            val updatedUser = user.copy(isBanned = !user.isBanned)
            repository.updateUser(updatedUser)
            repository.logActivity(currentAdmin.email, "USER_BAN_TOGGLE", "Toggled ban status for ${user.email}. New banned value is ${updatedUser.isBanned}")

            // If we banned the active logged in user, logout them instantly or let live check clear session
            if (updatedUser.isBanned && _currentUser.value?.email == user.email) {
                _currentUser.value = null
            }
        }
    }

    fun deleteUserByAdmin(user: User) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch
            if (user.role == "ADMIN") return@launch // Prevent self delete

            repository.deleteUser(user)
            repository.logActivity(currentAdmin.email, "USER_DELETE", "Admin deleted user record for ${user.email} from registry cascade.")
            
            // Clean up files/trades if desired, or let Room cascading take care or default handle it
            // We can delete their trades as well!
            uiTrades.value.filter { it.userId == user.email }.forEach {
                repository.deleteTrade(it)
            }
        }
    }

    fun editUserByAdmin(user: User, displayName: String, role: String, subscriptionType: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch

            val updatedUser = user.copy(
                displayName = displayName,
                role = role,
                subscriptionType = subscriptionType
            )
            repository.updateUser(updatedUser)
            repository.logActivity(currentAdmin.email, "USER_PROFILE_EDIT", "Admin customized user options for ${user.email}. Details: Role=$role, Sub=$subscriptionType")
            
            // If editing active user, update session
            if (_currentUser.value?.email == user.email) {
                _currentUser.value = updatedUser
            }
        }
    }

    fun resetUserPasswordByAdmin(user: User, newPass: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch

            val updatedUser = user.copy(passwordHash = newPass)
            repository.updateUser(updatedUser)
            repository.logActivity(currentAdmin.email, "USER_PASSWORD_RESET", "Admin reset account password securely for ${user.email}")
        }
    }

    fun sendAnnouncementByAdmin(title: String, message: String, typeString: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch

            val notif = Notification(
                title = title,
                message = message,
                type = typeString
            )
            repository.insertNotification(notif)
            repository.logActivity(currentAdmin.email, "ANNOUNCEMENT_SENT", "Published administrative announcement: $title")
        }
    }

    fun toggleSystemSettingByAdmin(key: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch

            val currentSetting = repository.getSystemSetting(key)
            val newValue = if (currentSetting?.value == "true") "false" else "true"
            repository.insertSystemSetting(key, newValue)

            repository.logActivity(currentAdmin.email, "SYSTEM_SETTING_TOGGLE", "Toggled system state for $key key to $newValue")
            syncSystemSettings()
        }
    }

    fun updateAICoachToneByAdmin(tone: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            if (currentAdmin.role != "ADMIN") return@launch

            repository.insertSystemSetting("AI_COACH_TONE", tone)
            repository.logActivity(currentAdmin.email, "SYSTEM_SETTING_AI_TONE", "Customized chatbot professional tone to $tone")
            syncSystemSettings()
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            repository.deleteNotification(notification)
        }
    }

    // --- Chat with AI Assistant (Gemini) ---
    fun sendMessageToAI(text: String) {
        if (text.trim().isEmpty()) return
        val userMsg = ChatMessage("User", text, isUser = true)
        _aiConversation.update { it + userMsg }

        viewModelScope.launch {
            _aiLoading.value = true
            
            // Verify AI advisor setting
            if (!_aiCoachEnabled.value) {
                val errorMsg = ChatMessage("SmartMoney Coach", "This AI Coaching resource is currently powered off by system administrators.", isUser = false)
                _aiConversation.update { it + errorMsg }
                _aiLoading.value = false
                return@launch
            }

            // Grab some context about user trades to make coaching super-relevant!
            val trades = uiTrades.value
            val contextString = if (trades.isNotEmpty()) {
                val total = trades.size
                val wins = trades.count { it.resultType == "WIN" }
                val losses = trades.count { it.resultType == "LOSS" }
                val net = trades.sumOf { it.profitResult }
                val lastEmotions = trades.take(4).map { t -> t.emotion }.joinToString(", ")
                "A trader with ${total} trades total logged, ${wins} wins, and ${losses} losses. Total active PnL is $$net. Last trading emotions logged: $lastEmotions."
            } else {
                "No trades logged yet in the database."
            }

            val systemInstruction = """
                You are SmartMoney Coach, a professional Forex and Crypto trading mentor specializing in Smart Money Concepts (SMC): Break of Structure (BOS), Change of Character (CHoCH), Order Blocks, Fair Value Gaps (FVGs), liquidity sweeps, and OTE entry.
                Tone of dialogue requested: ${_aiCoachTone.value}
                Give professional, actionable feedback. Use trading terminology correctly. Highlight flaws like overtrading, risk violations (exceeding 2% per trade), or emotional trading based on the notes / emotions provided.
                Limit replies to 4 bullet points where possible to be easily readable on mobile.
                Active state: $contextString
            """.trimIndent()

            val aiResult = askGemini(text, systemInstruction)
            val coachMsg = ChatMessage("SmartMoney Coach", aiResult, isUser = false)
            _aiConversation.update { it + coachMsg }
            _aiLoading.value = false
        }
    }

    fun runGeneralAIPatternAudit() {
        val trades = uiTrades.value
        if (trades.isEmpty()) {
            _aiAdvice.value = "SmartMoney Coach currently has no data. Log at least 2 trades in your Journal, and our AI will conduct a full SMC strategy audit."
            return
        }
        viewModelScope.launch {
            _aiLoading.value = true
            
            if (!_aiCoachEnabled.value) {
                _aiAdvice.value = "This AI auditing resource is disabled globally by system administrators."
                _aiLoading.value = false
                return@launch
            }

            val tradeSummary = trades.take(10).mapIndexed { i, t ->
                "Trade #${i+1}: Symbol ${t.pair}, ${if(t.isBuy) "BUY" else "SELL"}, Result ${t.resultType}, Profit $${t.profitResult}, Session ${t.session}, Tags: ${t.smcTags}, Strategy: ${t.strategyUsed}, Emotion: ${t.emotion}, Risk: ${t.riskPercent}%"
            }.joinToString("\n")

            val prompt = """
                Perform an advanced statistical and behavioral audit on my last 10 trading logs:
                $tradeSummary
                
                Please determine:
                1. Best trading setup and session.
                2. Potential overtrading or revenge trading patterns (e.g., losing consecutive trades close together or under negative emotions).
                3. Risk percentage discipline breaches.
                4. Actionable tips to improve performance under tone: ${_aiCoachTone.value}.
            """.trimIndent()

            val aiResult = askGemini(prompt, "You are an elite SMC risk-officer.")
            _aiAdvice.value = aiResult
            _aiLoading.value = false
        }
    }

    // --- Helper to export CSV data ---
    fun getCsvData(): String {
        val trades = uiTrades.value
        val sb = StringBuilder()
        sb.append("ID,Date,Pair,Direction,Entry,SL,TP,LotSize,Risk%,Result,ProfitAmount,Session,SMC_Tags,Emotion,Strategy,Notes\n")
        trades.forEach { t ->
            sb.append("${t.id},${t.timestamp},${t.pair},${if (t.isBuy) "BUY" else "SELL"},${t.entryPrice},${t.stopLoss},${t.takeProfit},${t.lotSize},${t.riskPercent},${t.resultType},${t.profitResult},${t.session},\"${t.smcTags}\",${t.emotion},\"${t.strategyUsed}\",\"${t.preTradeNotes}\"\n")
        }
        return sb.toString()
    }

    private fun updateActiveChallengesPnL(pnl: Double) {
        viewModelScope.launch {
            val challenges = allChallenges.value
            challenges.filter { it.isActive }.forEach { challenge ->
                val updatedBalance = challenge.currentBalance + pnl
                repository.updateChallenge(challenge.copy(currentBalance = updatedBalance))
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TradeViewModel::class.java)) {
                    return TradeViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
