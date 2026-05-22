package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// --- NAVIGATION CONTROLLER STATE ---
enum class SmartMoneyScreen {
    LANDING,
    AUTH,
    HUB_DASHBOARD,
    HUB_JOURNAL,
    HUB_ANALYTICS,
    HUB_COACH,
    HUB_MORE,
    ADMIN_PORTAL
}

@Composable
fun SmartMoneyHub(viewModel: TradeViewModel) {
    var currentScreen by remember { mutableStateOf(SmartMoneyScreen.LANDING) }
    
    val trades by viewModel.uiTrades.collectAsStateWithLifecycle()
    val challenges by viewModel.allChallenges.collectAsStateWithLifecycle()
    val checklistItems by viewModel.allChecklistItems.collectAsStateWithLifecycle()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    // Screen State Router
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Ambient background subtle cyber-glow layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        Brush.radialGradient(
                            colors = listOf(ElectricCyan.copy(alpha = 0.08f), Color.Transparent)
                        ),
                        radius = size.width,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                    drawCircle(
                        Brush.radialGradient(
                            colors = listOf(ElectricPurple.copy(alpha = 0.06f), Color.Transparent)
                        ),
                        radius = size.width * 1.2f,
                        center = Offset(0f, size.height * 0.8f)
                    )
                }
        )

        when (currentScreen) {
            SmartMoneyScreen.LANDING -> {
                LandingPage(
                    onEnter = {
                        if (isUserLoggedIn) {
                            currentScreen = SmartMoneyScreen.HUB_DASHBOARD
                        } else {
                            currentScreen = SmartMoneyScreen.AUTH
                        }
                    }
                )
            }
            SmartMoneyScreen.AUTH -> {
                AuthPage(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        currentScreen = SmartMoneyScreen.HUB_DASHBOARD
                    },
                    onBackToLanding = {
                        currentScreen = SmartMoneyScreen.LANDING
                    }
                )
            }
            // Master Hub Navigation with persistent active indicators
            else -> {
                if (!isUserLoggedIn) {
                    currentScreen = SmartMoneyScreen.AUTH
                } else {
                    ExtendedHubScaffold(
                        currentTab = currentScreen,
                        onTabSelected = { currentScreen = it },
                        viewModel = viewModel,
                        trades = trades,
                        challenges = challenges,
                        checklistItems = checklistItems,
                        userProfile = userProfile
                    )
                }
            }
        }
    }
}

// --- LANDING PAGE WITH PREMIUM HERO SECTION & SHOWCASE ---
@Composable
fun LandingPage(onEnter: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Brand Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.QueryStats,
                contentDescription = "SmartMoney Icon",
                tint = ElectricCyan,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SmartMoney Journal",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TradingTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
        
        Spacer(modifier = Modifier.height(36.dp))

        // Large display typography
        Text(
            text = "Track setups like a hedge fund.\nExecute like a machine.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = TradingTextPrimary,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A professional, premium trading journal based on Smart Money Concepts (SMC) architecture. Built to track drawdowns, analyze setups, and run automated AI pattern reviews.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = TradingTextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CTA action
        Button(
            onClick = onEnter,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(52.dp)
        ) {
            Text(
                "Launch SMC Workspace Hub",
                color = CyberBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Interactive Feature Showcase cards
        Text(
            text = "BUILT FOR SMC CONNOISSEURS",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricPurple,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(
            icon = Icons.Filled.Analytics,
            title = "Real-time SMC Tagging",
            description = "Tag trades with BOS, CHoCH, Order Blocks, FVGs, Liquidity Sweeps, and OTE entry ranges to build strategy databases."
        )

        FeatureCard(
            icon = Icons.Filled.Psychology,
            title = "Gemini AI Trading Coach",
            description = "Instant strategy audits which detect revenge trading, overtrading patterns, and emotional risk discipline violations."
        )

        FeatureCard(
            icon = Icons.Filled.Star,
            title = "FTMO Challenge Tracker",
            description = "Input and monitor your prop firm rules and drawdowns dynamically alongside standard gamified badge achievements."
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Pricing Block
        Text(
            text = "PRICING PLANS",
            style = MaterialTheme.typography.labelSmall,
            color = GoldenOrange,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        PricingSection()

        Spacer(modifier = Modifier.height(32.dp))
        
        // Testimonials
        Text(
            text = "TRADER TESTIMONIALS",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        TestimonialCard()

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun FeatureCard(icon: ImageVector, title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElectricCyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = ElectricCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = TradingTextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun PricingSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface)
                .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Standard", fontWeight = FontWeight.SemiBold, color = TradingTextSecondary)
                Text("Free", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ElectricCyan)
                Text("SMC basics\n10 Logs limit", textAlign = TextAlign.Center, fontSize = 11.sp, color = TradingTextMuted, lineHeight = 13.sp)
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberSurface)
                .border(BorderStroke(1.5.dp, ElectricPurple), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Professional", fontWeight = FontWeight.Bold, color = TradingTextPrimary)
                Text("$19.99/mo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ElectricPurple)
                Text("Unlimited system\nAI Assistant audits", textAlign = TextAlign.Center, fontSize = 11.sp, color = TradingTextSecondary, lineHeight = 13.sp)
            }
        }
    }
}

@Composable
fun TestimonialCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "\"SmartMoney Journal is elite. Integrating FVG and Order Blocks with instant reviews of pattern viability enabled me to satisfy my FTMO profit requirements in under 12 trading days!\"",
                style = MaterialTheme.typography.bodySmall,
                color = TradingTextSecondary,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "--- Alex R., Funded Forex Account Executive",
                fontWeight = FontWeight.SemiBold,
                color = ElectricCyan,
                fontSize = 12.sp
            )
        }
    }
}

// --- AUTH PAGE WITH GOOGLE AND CREDENTIAL OPTIONS ---
@Composable
fun AuthPage(
    viewModel: TradeViewModel,
    onAuthSuccess: () -> Unit,
    onBackToLanding: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Dropdown/Filter state for signup role
    var selectedSignupRole by remember { mutableStateOf("TRADER") }
    var adminPasscode by remember { mutableStateOf("") }

    // UI flows
    val otpRequiredCode by viewModel.otpNotification.collectAsStateWithLifecycle()
    val forgotPasswordMsg by viewModel.forgotPasswordNotification.collectAsStateWithLifecycle()

    var enteredOTP by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Identity
        Icon(
            imageVector = Icons.Filled.QueryStats,
            contentDescription = "App Logo",
            tint = ElectricPurple,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "SECURE PORTAL ENTRY",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricPurple,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = "SmartMoney Journal",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = TradingTextPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Simulated Forgot Password Notification Box
        if (forgotPasswordMsg != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                border = BorderStroke(1.dp, GoldenOrange.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("PASSWORD ASSISTANCE RECOVERY:", color = GoldenOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(forgotPasswordMsg!!, color = TradingTextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.clearForgotPasswordNotification() }) {
                        Text("Acknowledge Pin", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Verification OTP Prompt Box
        if (otpRequiredCode != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                border = BorderStroke(1.5.dp, ElectricCyan),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VERIFICATION REQUIRED PIN", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    Text("We have generated a verification OTP code into notifications. Type it below:", color = TradingTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = enteredOTP,
                        onValueChange = { enteredOTP = it },
                        label = { Text("4-Digit PIN") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (viewModel.submitEmailOTP(enteredOTP)) {
                                Toast.makeText(context, "Verification Activated! Access unlocked.", Toast.LENGTH_SHORT).show()
                                onAuthSuccess()
                            } else {
                                Toast.makeText(context, "Insecure verification digits match failed.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                    ) {
                        Text("Verify Email Identity", color = CyberBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Username (for registration)
        if (isRegistering) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Display Username") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = ElectricCyan,
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = CyberBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = ElectricCyan,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Access Key Password") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = ElectricCyan,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CyberBorder
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown Role Picker for registration
        if (isRegistering) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select Default Role Account Type:", fontSize = 12.sp, color = TradingTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TRADER", "ADMIN", "DEMO").forEach { r ->
                        FilterChip(
                            selected = selectedSignupRole == r,
                            onClick = { selectedSignupRole = r },
                            label = { Text(r, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan.copy(alpha = 0.15f),
                                selectedLabelColor = ElectricCyan
                            )
                        )
                    }
                }
            }
        }

        if (isRegistering && selectedSignupRole == "ADMIN") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = adminPasscode,
                onValueChange = { adminPasscode = it },
                label = { Text("Secured Admin Private Key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = ElectricPurple,
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = CyberBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trigger Log In / Register
        Button(
            onClick = {
                if (isRegistering) {
                    viewModel.signUpUser(email, username, password, selectedSignupRole, adminPasscode) { error ->
                        if (error != null) {
                            Toast.makeText(context, "Signup Warning: $error", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        }
                    }
                } else {
                    viewModel.loginUser(email, password) { error ->
                        if (error != null) {
                            Toast.makeText(context, "Log In Alert: $error", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Access Granted!", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                if (isRegistering) "Register Secure Profile" else "Acknowledge Identity",
                color = CyberBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Google Sign-In Simulation Button
        OutlinedButton(
            onClick = {
                viewModel.simulateGoogleLogin(email, username) {
                    Toast.makeText(context, "Google OAuth SSO Activated!", Toast.LENGTH_SHORT).show()
                    onAuthSuccess()
                }
            },
            border = BorderStroke(1.5.dp, CyberBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = "Google SSO logo",
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in with Google Account", color = TradingTextPrimary, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Developer Seed Accounts quick portal
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("DEVELOPER QUICK SEED ACCOUNTS TRAY:", color = ElectricPurple, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            email = "pro@trader.com"
                            password = "trader123"
                            isRegistering = false
                            viewModel.loginUser(email, password) { err ->
                                if (err == null) onAuthSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Trader Port", color = TradingTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            email = "trial@demo.com"
                            password = "demo123"
                            isRegistering = false
                            viewModel.loginUser(email, password) { err ->
                                if (err == null) onAuthSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenOrange.copy(alpha = 0.3f)),
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Demo Port", color = TradingTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle state / Forgot password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { isRegistering = !isRegistering }
            ) {
                Text(
                    text = if (isRegistering) "Sign In instead" else "Sign Up Account",
                    color = ElectricPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = {
                    if (email.contains("@")) {
                        viewModel.forgotPasswordUser(email)
                        Toast.makeText(context, "Recovery simulation active!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Enter email above before hitting recovery", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Forgot Password Key?", color = GoldenOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBackToLanding
        ) {
            Text("Back to Landing Page", color = TradingTextMuted, fontSize = 13.sp)
        }
    }
}

// --- EXTENDED SCAFFOLD PROVIDING PERSISTENT TAB ROUTING & ADAPTIVE COMPONENT SUPPORT ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendedHubScaffold(
    currentTab: SmartMoneyScreen,
    onTabSelected: (SmartMoneyScreen) -> Unit,
    viewModel: TradeViewModel,
    trades: List<Trade>,
    challenges: List<Challenge>,
    checklistItems: List<ChecklistItem>,
    userProfile: UserProfile
) {
    var showAddTradeDialog by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SmartMoney Journal", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            color = TradingTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                        Text(
                            text = when(currentTab) {
                                SmartMoneyScreen.HUB_DASHBOARD -> "Dashboard Overview"
                                SmartMoneyScreen.HUB_JOURNAL -> "Interactive Trade Log"
                                SmartMoneyScreen.HUB_ANALYTICS -> "SMC Strategy Analysis"
                                SmartMoneyScreen.HUB_COACH -> "Gemini Coach Workspace"
                                SmartMoneyScreen.HUB_MORE -> "Tools, Challenges & Badges"
                                SmartMoneyScreen.ADMIN_PORTAL -> "Admin Systems Control"
                                else -> "Control Center"
                            },
                            fontSize = 12.sp,
                            color = if (currentTab == SmartMoneyScreen.ADMIN_PORTAL) ElectricPurple else ElectricCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logoutUser() }) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Log Out", tint = TradingLoss)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberBackground,
                    titleContentColor = TradingTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberBackground,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = currentTab == SmartMoneyScreen.HUB_DASHBOARD,
                    onClick = { onTabSelected(SmartMoneyScreen.HUB_DASHBOARD) },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        unselectedIconColor = TradingTextSecondary,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == SmartMoneyScreen.HUB_JOURNAL,
                    onClick = { onTabSelected(SmartMoneyScreen.HUB_JOURNAL) },
                    icon = { Icon(Icons.Filled.Book, contentDescription = "Journal") },
                    label = { Text("Journal", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        unselectedIconColor = TradingTextSecondary,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == SmartMoneyScreen.HUB_ANALYTICS,
                    onClick = { onTabSelected(SmartMoneyScreen.HUB_ANALYTICS) },
                    icon = { Icon(Icons.Filled.Leaderboard, contentDescription = "Analytics") },
                    label = { Text("Analytics", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        unselectedIconColor = TradingTextSecondary,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == SmartMoneyScreen.HUB_COACH,
                    onClick = { onTabSelected(SmartMoneyScreen.HUB_COACH) },
                    icon = { Icon(Icons.Filled.Psychology, contentDescription = "AI Coach") },
                    label = { Text("AI Coach", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        unselectedIconColor = TradingTextSecondary,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                    )
                )

                NavigationBarItem(
                    selected = currentTab == SmartMoneyScreen.HUB_MORE,
                    onClick = { onTabSelected(SmartMoneyScreen.HUB_MORE) },
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "More") },
                    label = { Text("More", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ElectricCyan,
                        unselectedIconColor = TradingTextSecondary,
                        selectedTextColor = ElectricCyan,
                        indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                    )
                )

                if (currentUser?.role == "ADMIN") {
                    NavigationBarItem(
                        selected = currentTab == SmartMoneyScreen.ADMIN_PORTAL,
                        onClick = { onTabSelected(SmartMoneyScreen.ADMIN_PORTAL) },
                        icon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin Portal") },
                        label = { Text("Admin", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricPurple,
                            unselectedIconColor = TradingTextSecondary,
                            selectedTextColor = ElectricPurple,
                            indicatorColor = ElectricPurple.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentTab == SmartMoneyScreen.HUB_JOURNAL) {
                FloatingActionButton(
                    onClick = { showAddTradeDialog = true },
                    containerColor = ElectricCyan,
                    contentColor = CyberBackground
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Custom Trade")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                SmartMoneyScreen.HUB_DASHBOARD -> HomeScreen(viewModel, trades, challenges)
                SmartMoneyScreen.HUB_JOURNAL -> JournalScreen(viewModel, trades)
                SmartMoneyScreen.HUB_ANALYTICS -> AnalyticsScreen(viewModel, trades)
                SmartMoneyScreen.HUB_COACH -> CoachScreen(viewModel)
                SmartMoneyScreen.HUB_MORE -> MoreToolsScreen(viewModel, challenges, checklistItems, userProfile)
                SmartMoneyScreen.ADMIN_PORTAL -> AdminPortalScreen(viewModel)
                else -> {}
            }
        }
    }

    // Modal to add a new trade log dynamically into Room
    if (showAddTradeDialog) {
        AddTradeDialog(
            onDismiss = { showAddTradeDialog = false },
            onSave = { trade ->
                viewModel.insertTrade(trade)
                showAddTradeDialog = false
            }
        )
    }
}

// --- HOME SCREEN: REACTIVE DRAWDOWN METRICS, BALANCE, NEWS FEED & TRACKER ---
@Composable
fun HomeScreen(
    viewModel: TradeViewModel,
    trades: List<Trade>,
    challenges: List<Challenge>
) {
    val scrollState = rememberScrollState()
    
    // Derived overall metrics computations
    val totalTrades = trades.size
    val wins = trades.count { it.resultType == "WIN" }
    val losses = trades.count { it.resultType == "LOSS" }
    val winRate = if (totalTrades > 0) (wins.toDouble() / totalTrades * 100).toInt() else 0
    val totalProfit = trades.sumOf { it.profitResult }
    
    // Average Risk Reward
    var totalR2Ratio = 0.0
    var countR2 = 0
    trades.forEach {
        val riskDiff = abs(it.entryPrice - it.stopLoss)
        val profitDiff = abs(it.takeProfit - it.entryPrice)
        if (riskDiff > 0) {
            totalR2Ratio += (profitDiff / riskDiff)
            countR2++
        }
    }
    val avgRiskReward = if (countR2 > 0) String.format(Locale.US, "1:%.1f", totalR2Ratio / countR2) else "1:3.0"

    // Consecutive wins/losses
    var streakCount = 0
    var isWinStreak = true
    if (trades.isNotEmpty()) {
        val firstResult = trades.first().resultType
        isWinStreak = firstResult == "WIN"
        for (trade in trades) {
            if (trade.resultType == firstResult) {
                streakCount++
            } else {
                break
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Glowing Premium Account Balance Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(ElectricCyan.copy(alpha = 0.1f), Color.Transparent)
                        )
                    )
                },
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "NET EQUITY BALANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                val activeChallenge = challenges.firstOrNull { it.isActive }
                val startingBalance = activeChallenge?.startingBalance ?: 100000.00
                val currentAggBalance = startingBalance + totalProfit
                Text(
                    text = String.format(Locale.US, "$%,.2f", currentAggBalance),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TradingTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Challenge Status", fontSize = 11.sp, color = TradingTextMuted)
                        Text(
                            activeChallenge?.name ?: "No active Prop Challenge",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TradingTextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (totalProfit >= 0) TradingWin.copy(alpha = 0.15f) else TradingLoss.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%s$%,.2f", if (totalProfit >= 0) "+" else "", totalProfit),
                            fontWeight = FontWeight.Bold,
                            color = if (totalProfit >= 0) TradingWin else TradingLoss,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Live stats grid - Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricWidget(
                modifier = Modifier.weight(1f),
                title = "Total Trades",
                value = totalTrades.toString(),
                colorState = TradingTextPrimary,
                icon = Icons.Filled.ImportContacts
            )
            MetricWidget(
                modifier = Modifier.weight(1f),
                title = "Win Rate %",
                value = "$winRate%",
                colorState = if (winRate >= 50) TradingWin else GoldenOrange,
                icon = Icons.Filled.QueryStats
            )
        }

        // Live stats grid - Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricWidget(
                modifier = Modifier.weight(1f),
                title = "Profit Factor",
                value = if (losses > 0) String.format(Locale.US, "%.1f", wins.toDouble() / losses) else "3.1",
                colorState = ElectricPurple,
                icon = Icons.Filled.MilitaryTech
            )
            MetricWidget(
                modifier = Modifier.weight(1f),
                title = "Avg Risk/Reward",
                value = avgRiskReward,
                colorState = ElectricCyan,
                icon = Icons.Filled.Shield
            )
        }

        // Trading Streak Alert Banner
        if (streakCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWinStreak) TradingWin.copy(alpha = 0.12f) else TradingLoss.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.dp, if (isWinStreak) TradingWin.copy(alpha = 0.4f) else TradingLoss.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isWinStreak) Icons.Filled.LocalFireDepartment else Icons.Filled.Warning,
                        contentDescription = "Streak Icon",
                        tint = if (isWinStreak) TradingWin else TradingLoss,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isWinStreak) "ACTIVE WIN STREAK!" else "LOSS PROTECTION TRIGGERED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isWinStreak) TradingWin else TradingLoss
                        )
                        Text(
                            text = if (isWinStreak) {
                                "You are currently hot with $streakCount consecutive wins. Maintain solid capital risk rules!"
                            } else {
                                "Currently trailing $streakCount technical stops. Limit position size by half to stop revenge impulse loops."
                            },
                            color = TradingTextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Horizontal Weekly Progression visualizer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "DAILY SESSION DISCIPLINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradingTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")
                    days.forEach { d ->
                        val randomChecked = d == "Mon" || d == "Wed" || d == "Thu"
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (randomChecked) TradingWin.copy(alpha = 0.15f) else CyberSurfaceLighter)
                                    .border(
                                        BorderStroke(1.dp, if (randomChecked) TradingWin else TradingTextMuted),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (randomChecked) Icons.Filled.Check else Icons.Filled.Remove,
                                    contentDescription = "Day log status",
                                    tint = if (randomChecked) TradingWin else TradingTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(d, color = TradingTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Live Economic Calendar Section
        Text(
            text = "ECONOMIC DATA CALENDAR",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
            letterSpacing = 1.5.sp
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                viewModel.economicNews.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (event.impact) {
                                            "High" -> TradingLoss.copy(alpha = 0.15f)
                                            "Medium" -> GoldenOrange.copy(alpha = 0.15f)
                                            else -> TradingTextMuted.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = event.impact.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    color = when (event.impact) {
                                        "High" -> TradingLoss
                                        "Medium" -> GoldenOrange
                                        else -> TradingTextSecondary
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    event.title,
                                    fontWeight = FontWeight.Bold,
                                    color = TradingTextPrimary,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.width(180.dp)
                                )
                                Text(
                                    "${event.currency} • ${event.time}",
                                    fontSize = 11.sp,
                                    color = TradingTextSecondary
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Cons: ${event.consensus}",
                                fontSize = 11.sp,
                                color = TradingTextSecondary
                            )
                            Text(
                                "Prev: ${event.previous}",
                                fontSize = 11.sp,
                                color = TradingTextMuted
                            )
                        }
                    }
                    Divider(color = CyberBorder.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun MetricWidget(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    colorState: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(115.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = TradingTextSecondary, fontWeight = FontWeight.SemiBold)
                Icon(imageVector = icon, contentDescription = title, tint = colorState, modifier = Modifier.size(18.dp))
            }
            Text(
                text = value,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorState,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// --- JOURNAL SCREEN: FILTERABLE CHRONOLOGY OF LOGGED POSITIONS WITH CALENDAR VIEW ---
@Composable
fun JournalScreen(
    viewModel: TradeViewModel,
    trades: List<Trade>
) {
    var filterBuySell by remember { mutableStateOf<Boolean?>(null) } // null = All, true = Buy, false = Sell
    var filterWinLoss by remember { mutableStateOf<String?>(null) } // null = All, "WIN", "LOSS", "BREAKEVEN"
    var filterSession by remember { mutableStateOf<String?>(null) } // null = All
    var showAllFilterRow by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val finalTrades = trades.filter { trade ->
        val bsMatch = filterBuySell == null || trade.isBuy == filterBuySell
        val wlMatch = filterWinLoss == null || trade.resultType == filterWinLoss
        val sMatch = filterSession == null || trade.session == filterSession
        bsMatch && wlMatch && sMatch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Toolbar with filters and export CSV action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SMART DECK POSITIONS",
                fontSize = 12.sp,
                color = ElectricPurple,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { showAllFilterRow = !showAllFilterRow },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberSurfaceLighter, RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Filled.FilterList, contentDescription = "Toggle Filters", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                }
                
                // Export CSV action
                IconButton(
                    onClick = {
                        val csv = viewModel.getCsvData()
                        clipboardManager.setText(AnnotatedString(csv))
                        Toast.makeText(context, "Copied Trade Logs as CSV to local Clipboard! Paste into Excel.", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CyberSurfaceLighter, RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = "Export CSV", tint = TradingWin, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Animated Filters Rows
        AnimatedVisibility(visible = showAllFilterRow) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Buy/Sell filter
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Direction:", fontSize = 11.sp, color = TradingTextSecondary, modifier = Modifier.width(62.dp))
                        FilterChip(text = "All", isSelected = filterBuySell == null, onClick = { filterBuySell = null })
                        FilterChip(text = "BUY", isSelected = filterBuySell == true, onClick = { filterBuySell = true })
                        FilterChip(text = "SELL", isSelected = filterBuySell == false, onClick = { filterBuySell = false })
                    }
                    // Win/Loss filter
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Result:", fontSize = 11.sp, color = TradingTextSecondary, modifier = Modifier.width(62.dp))
                        FilterChip(text = "All", isSelected = filterWinLoss == null, onClick = { filterWinLoss = null })
                        FilterChip(text = "WIN", isSelected = filterWinLoss == "WIN", onClick = { filterWinLoss = "WIN" })
                        FilterChip(text = "LOSS", isSelected = filterWinLoss == "LOSS", onClick = { filterWinLoss = "LOSS" })
                        FilterChip(text = "BE", isSelected = filterWinLoss == "BREAKEVEN", onClick = { filterWinLoss = "BREAKEVEN" })
                    }
                    // Session filter
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Session:", fontSize = 11.sp, color = TradingTextSecondary, modifier = Modifier.width(62.dp))
                        FilterChip(text = "All", isSelected = filterSession == null, onClick = { filterSession = null })
                        FilterChip(text = "London", isSelected = filterSession == "London", onClick = { filterSession = "London" })
                        FilterChip(text = "NY", isSelected = filterSession == "New York", onClick = { filterSession = "New York" })
                        FilterChip(text = "Asia", isSelected = filterSession == "Asia", onClick = { filterSession = "Asia" })
                    }
                }
            }
        }

        // Calendar Heatmap section
        Text("CALENDAR PROFIT HEATMAP", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TradingTextSecondary, letterSpacing = 1.sp)
        CalendarHeatmap(trades)

        // Trades list
        if (finalTrades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurface)
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(imageVector = Icons.Filled.Inbox, contentDescription = "Empty", tint = TradingTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No matching Trade Logs found", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 15.sp)
                    Text("Hit the '+' FAB button to record your first SMC trade log position.", textAlign = TextAlign.Center, color = TradingTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(finalTrades) { trade ->
                    TradeListItem(trade = trade, onDelete = { viewModel.deleteTrade(trade) })
                }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) ElectricCyan.copy(alpha = 0.2f) else CyberSurfaceLighter)
            .border(
                BorderStroke(1.dp, if (isSelected) ElectricCyan else TradingTextMuted.copy(alpha = 0.3f)),
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = if (isSelected) ElectricCyan else TradingTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CalendarHeatmap(trades: List<Trade>) {
    // Show a simplified 7-day row heatmap of daily earnings to fit neatly on mobile
    val now = System.currentTimeMillis()
    val dayMs = 24 * 3600 * 1000L
    val sdf = SimpleDateFormat("EE", Locale.US)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (i in 6 downTo 0) {
                val targetDateStart = now - (i * dayMs)
                val dayTrades = trades.filter {
                    val tradeDate = Date(it.timestamp)
                    val targetDate = Date(targetDateStart)
                    tradeDate.date == targetDate.date && tradeDate.month == targetDate.month && tradeDate.year == targetDate.year
                }
                val totalPnL = dayTrades.sumOf { it.profitResult }
                val label = sdf.format(Date(targetDateStart))

                val colorHex = when {
                    dayTrades.isEmpty() -> CyberSurfaceLighter
                    totalPnL > 0 -> TradingWin.copy(alpha = 0.5f + (totalPnL.toFloat() / 2000f).coerceAtMost(0.5f))
                    totalPnL < 0 -> TradingLoss.copy(alpha = 0.5f + (abs(totalPnL).toFloat() / 2000f).coerceAtMost(0.5f))
                    else -> GoldenOrange.copy(alpha = 0.4f)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorHex)
                            .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (dayTrades.isNotEmpty()) dayTrades.size.toString() else "0",
                            fontWeight = FontWeight.Bold,
                            color = if (dayTrades.isNotEmpty()) Color.White else TradingTextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = label, color = TradingTextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TradeListItem(trade: Trade, onDelete: () -> Unit) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(trade.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Direction, Result & PnL, Delete row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (trade.isBuy) ElectricCyan.copy(alpha = 0.15f) else ElectricPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (trade.isBuy) "BUY" else "SELL",
                            fontWeight = FontWeight.Bold,
                            color = if (trade.isBuy) ElectricCyan else ElectricPurple,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trade.pair,
                        fontWeight = FontWeight.ExtraBold,
                        color = TradingTextPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${trade.session} Session",
                        color = TradingTextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (trade.resultType) {
                                    "WIN" -> TradingWin.copy(alpha = 0.15f)
                                    "LOSS" -> TradingLoss.copy(alpha = 0.15f)
                                    else -> GoldenOrange.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = trade.resultType,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (trade.resultType) {
                                "WIN" -> TradingWin
                                "LOSS" -> TradingLoss
                                else -> GoldenOrange
                            },
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TradingLoss.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Entry Price", fontSize = 10.sp, color = TradingTextMuted)
                    Text(trade.entryPrice.toString(), fontSize = 13.sp, color = TradingTextPrimary, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Stop Loss", fontSize = 10.sp, color = TradingTextMuted)
                    Text(trade.stopLoss.toString(), fontSize = 13.sp, color = TradingTextPrimary)
                }
                Column {
                    Text("Take Profit", fontSize = 10.sp, color = TradingTextMuted)
                    Text(trade.takeProfit.toString(), fontSize = 13.sp, color = TradingTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Net PnL", fontSize = 10.sp, color = TradingTextMuted)
                    Text(
                        text = String.format(Locale.US, "$%,.2f", trade.profitResult),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (trade.profitResult >= 0) TradingWin else TradingLoss
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SMC tags list and pre/post-trade disclosures
            if (trade.smcTags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    trade.smcTags.split(",").forEach { tag ->
                        if (tag.trim().isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberSurfaceLighter)
                                    .border(BorderStroke(0.5.dp, ElectricCyan.copy(alpha = 0.4f)), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = tag, color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Emotion label and strategy used
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Strategy: ", fontSize = 11.sp, color = TradingTextMuted)
                    Text(trade.strategyUsed, fontSize = 11.sp, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Emotion: ", fontSize = 11.sp, color = TradingTextMuted)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                when (trade.emotion.lowercase()) {
                                    "calm", "disciplined" -> TradingWin.copy(alpha = 0.15f)
                                    "anxious", "fear" -> TradingLoss.copy(alpha = 0.15f)
                                    else -> GoldenOrange.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trade.emotion,
                            color = when (trade.emotion.lowercase()) {
                                "calm", "disciplined" -> TradingWin
                                "anxious", "fear" -> TradingLoss
                                else -> GoldenOrange
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expanded notes drawer
            if (trade.preTradeNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceLighter)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Pre-Trade Note:", fontSize = 9.sp, color = ElectricPurple, fontWeight = FontWeight.Bold)
                        Text(trade.preTradeNotes, fontSize = 11.sp, color = TradingTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(formattedDate, fontSize = 9.sp, color = TradingTextMuted, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
        }
    }
}

// --- ANALYTICS SCREEN: NATIVE INTERACTIVE CANVAS-DRAWN SMC METRICS ---
@Composable
fun AnalyticsScreen(
    viewModel: TradeViewModel,
    trades: List<Trade>
) {
    val scrollState = rememberScrollState()
    
    val total = trades.size
    val wins = trades.count { it.resultType == "WIN" }
    val losses = trades.count { it.resultType == "LOSS" }
    val breakevens = trades.count { it.resultType == "BREAKEVEN" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Advanced strategy metrics header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "ADVANCED METRIC INSIGHTS",
                fontSize = 12.sp,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Icon(imageVector = Icons.Filled.QueryStats, contentDescription = "Analytics", tint = ElectricCyan)
        }

        if (trades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("Log trades first to populate interactive charts.", color = TradingTextSecondary)
            }
            return
        }

        // Draw Interactive Win/Loss Pie Chart on Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "WIN/LOSS RATIO CHRONOLOGY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradingTextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val totalF = total.toFloat()
                        val winAngle = (wins.toFloat() / totalF) * 360f
                        val lossAngle = (losses.toFloat() / totalF) * 360f
                        val beAngle = (breakevens.toFloat() / totalF) * 360f

                        drawArc(
                            color = TradingWin,
                            startAngle = 0f,
                            sweepAngle = winAngle,
                            useCenter = false,
                            style = Stroke(width = 32f)
                        )
                        drawArc(
                            color = TradingLoss,
                            startAngle = winAngle,
                            sweepAngle = lossAngle,
                            useCenter = false,
                            style = Stroke(width = 32f)
                        )
                        if (beAngle > 0f) {
                            drawArc(
                                color = GoldenOrange,
                                startAngle = winAngle + lossAngle,
                                sweepAngle = beAngle,
                                useCenter = false,
                                style = Stroke(width = 32f)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val percentage = if (total > 0) (wins.toDouble() / total * 100).toInt() else 0
                        Text("$percentage%", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TradingWin, fontFamily = FontFamily.Monospace)
                        Text("Win Rate", fontSize = 10.sp, color = TradingTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    LegendItem(color = TradingWin, label = "Wins ($wins)")
                    LegendItem(color = TradingLoss, label = "Losses ($losses)")
                    LegendItem(color = GoldenOrange, label = "Breakeven ($breakevens)")
                }
            }
        }

        // Cumulative Growth Line Chart on Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CUMULATIVE PERFORMANCE (USD)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradingTextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val pnlPoints = mutableListOf<Float>()
                    var currentPnL = 0f
                    pnlPoints.add(0f)
                    trades.reversed().forEach {
                        currentPnL += it.profitResult.toFloat()
                        pnlPoints.add(currentPnL)
                    }

                    if (pnlPoints.isNotEmpty()) {
                        val minVal = pnlPoints.minOrNull() ?: 0f
                        val maxVal = pnlPoints.maxOrNull() ?: 100f
                        val delta = (maxVal - minVal).coerceAtLeast(1f)

                        val width = size.width
                        val height = size.height
                        val stepX = width / (pnlPoints.size - 1).coerceAtLeast(1)

                        val path = Path()
                        val fillPath = Path()

                        pnlPoints.forEachIndexed { idx, point ->
                            val x = idx * stepX
                            val normalizeY = (point - minVal) / delta
                            val y = height - (normalizeY * height)

                            if (idx == 0) {
                                path.moveTo(x, y)
                                fillPath.moveTo(x, height)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }

                            if (idx == pnlPoints.size - 1) {
                                fillPath.lineTo(x, height)
                                fillPath.close()
                            }
                        }

                        // Draw under region gradient
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(ElectricCyan.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )

                        // Draw trace coordinate line
                        drawPath(
                            path = path,
                            color = ElectricCyan,
                            style = Stroke(width = 6f)
                        )
                    }
                }
            }
        }

        // Analytical summary highlights
        Text("METRIC BREAKDOWNS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TradingTextSecondary, letterSpacing = 1.sp)

        // Highlight best setup & session parameters
        val sessionGroups = trades.groupBy { it.session }
        val bestSession = sessionGroups.maxByOrNull { entry -> entry.value.count { it.resultType == "WIN" } }?.key ?: "London"
        
        val setupGroups = trades.groupBy { it.strategyUsed }
        val bestSetup = setupGroups.maxByOrNull { entry -> entry.value.count { it.resultType == "WIN" } }?.key ?: "Order Block Tap"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Icon(imageVector = Icons.Filled.AccessTime, contentDescription = "Best Session", tint = ElectricPurple, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Best Trading Session", fontSize = 10.sp, color = TradingTextSecondary)
                    Text(bestSession, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ElectricPurple)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Icon(imageVector = Icons.Filled.Adjust, contentDescription = "Best Setup", tint = ElectricCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Most Profitable Setup", fontSize = 10.sp, color = TradingTextSecondary)
                    Text(bestSetup, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ElectricCyan)
                }
            }
        }

        // Risk discipline & emotional behaviors
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("RISK & EMOTION REPORT CARD", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TradingTextPrimary)
                
                // Risk Compliance
                val riskBreaches = trades.count { it.riskPercent > 2.0 }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Risk Rule Compliance (>2% Limit)", fontSize = 12.sp, color = TradingTextSecondary)
                    Text(
                        text = if (riskBreaches == 0) "100% Compliant" else "$riskBreaches Over-Risked",
                        fontWeight = FontWeight.Bold,
                        color = if (riskBreaches == 0) TradingWin else TradingLoss,
                        fontSize = 12.sp
                    )
                }

                // Primary logged state of emotion
                val primaryEmotion = trades.groupBy { it.emotion }.maxByOrNull { it.value.size }?.key ?: "Disciplined"
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dominant Trading Sentiment", fontSize = 12.sp, color = TradingTextSecondary)
                    Text(primaryEmotion, fontWeight = FontWeight.Bold, color = ElectricPurple, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = TradingTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// --- AI COACH SCREEN: STRUCTURED PSYCHOLOGY CHAT WORKSPACE & GENERAL PATTERN AUDITS ---
@Composable
fun CoachScreen(viewModel: TradeViewModel) {
    val aiConversation by viewModel.aiConversation.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val aiAdvice by viewModel.aiAdvice.collectAsStateWithLifecycle()

    var chatText by remember { mutableStateOf("") }
    var currentSubTab by remember { mutableStateOf(0) } // 0 = Chat Coach, 1 = Strategy Pattern Audit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Switch between chat and pattern audits
        TabRow(
            selectedTabIndex = currentSubTab,
            containerColor = CyberSurface,
            contentColor = ElectricCyan
        ) {
            Tab(selected = currentSubTab == 0, onClick = { currentSubTab = 0 }) {
                Text("AI Psychological Coach", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Tab(selected = currentSubTab == 1, onClick = { currentSubTab = 1 }) {
                Text("Strategy audits", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        if (currentSubTab == 0) {
            // Interactive Messaging Workspace
            val isApiKeyConfigured = remember {
                val key = com.example.BuildConfig.GEMINI_API_KEY
                key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.contains("placeholder", ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                    .background(CyberSurfaceLighter)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Show a helpful configuration guide if real API key is not yet set
                if (!isApiKeyConfigured) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TradingLoss.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, TradingLoss.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = "Active Key Missing Warning",
                                        tint = TradingLoss,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "🔑 LOCAL SIMULATION ACTIVE",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TradingLoss
                                    )
                                }
                                Text(
                                    "No configured GEMINI_API_KEY detected in your workspace! Using offline static response rules for now.",
                                    fontSize = 11.sp,
                                    color = TradingTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "How to Activate Real-Time Gemini AI:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = ElectricCyan
                                )
                                Text(
                                    "1. Click on the 🔑 SECRETS tab / panel in the Google AI Studio top-right controls or side settings.\n" +
                                    "2. Add or Edit a secret named \"GEMINI_API_KEY\".\n" +
                                    "3. Paste your live Gemini API Key (generate one at aistudio.google.com/app/apikey).\n" +
                                    "4. Click \"Done\" / Save. The builder will automatically re-verify and compile your app with actual live AI intelligence!",
                                    fontSize = 11.sp,
                                    color = TradingTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TradingWin.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, TradingWin.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Active API Key Status",
                                    tint = TradingWin,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "🟢 Live Gemini AI Engine Connected Successfully!",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = TradingWin
                                )
                            }
                        }
                    }
                }

                items(aiConversation) { chat ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalAlignment = if (chat.isUser) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (chat.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (chat.isUser) 0.dp else 12.dp
                                    )
                                )
                                .background(if (chat.isUser) ElectricPurple.copy(alpha = 0.25f) else CyberSurface)
                                .border(
                                    BorderStroke(1.dp, if (chat.isUser) ElectricPurple else CyberBorder),
                                    RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (chat.isUser) 12.dp else 0.dp,
                                        bottomEnd = if (chat.isUser) 0.dp else 12.dp
                                    )
                                )
                                .padding(12.dp)
                                .widthIn(max = 260.dp)
                        ) {
                            Text(
                                text = chat.message,
                                fontSize = 12.sp,
                                color = TradingTextPrimary,
                                lineHeight = 16.sp,
                                fontFamily = if (chat.isUser) FontFamily.SansSerif else FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (chat.isUser) "You" else "SmartMoney Coach",
                            fontSize = 9.sp,
                            color = TradingTextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (aiLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ElectricCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coach is analyzing strategy profiles...", fontSize = 10.sp, color = TradingTextSecondary, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Input controller
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatText,
                    onValueChange = { chatText = it },
                    placeholder = { Text("Ask about overtrading, fear, or order blocks...") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CyberBorder
                    ),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        viewModel.sendMessageToAI(chatText)
                        chatText = ""
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElectricCyan)
                ) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "Send", tint = CyberBackground)
                }
            }
        } else {
            // General Pattern Audit panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "LAUNCH INSTANT SMC STRATEGY AUDIT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                        Text(
                            "Click below to make the AI Assistant review your latest 10 trades from Room database to audit your stop losses, overtrade alerts, session timings, and emotional consistency scores.",
                            fontSize = 12.sp,
                            color = TradingTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Button(
                            onClick = { viewModel.runGeneralAIPatternAudit() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Generate AI Strategy Audit", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (aiLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }

                aiAdvice?.let { advice ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                        border = BorderStroke(1.dp, CyberBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Done", tint = TradingWin, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("COMPLETED AUDIT REPORT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TradingTextPrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = advice,
                                fontSize = 12.sp,
                                color = TradingTextPrimary,
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- MORE TOOLS SCREEN: CHALLENGE DRAWDOWN LIMITS, DAILY CHECKLISTS, BADGES, ADMIN PANEL ---
@Composable
fun MoreToolsScreen(
    viewModel: TradeViewModel,
    challenges: List<Challenge>,
    checklistItems: List<ChecklistItem>,
    userProfile: UserProfile
) {
    val scrollState = rememberScrollState()
    var currentSubTab by remember { mutableStateOf(0) } // 0 = Profile/Challenge, 1 = Checklists/Badges

    var customCheckItemText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Switch utilities
        TabRow(
            selectedTabIndex = currentSubTab,
            containerColor = CyberSurface,
            contentColor = ElectricCyan
        ) {
            Tab(selected = currentSubTab == 0, onClick = { currentSubTab = 0 }) {
                Text("Challenges & Profile", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Tab(selected = currentSubTab == 1, onClick = { currentSubTab = 1 }) {
                Text("Checklist & Badge Rewards", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (currentSubTab == 0) {
            // Live Profile view
            Text("USER PROFILE DECK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TradingTextSecondary, letterSpacing = 1.sp)
            
            val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
            var showPremiumModal by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (currentUser?.subscriptionType == "PREMIUM") ElectricCyan else ElectricPurple)
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "Profile", tint = CyberBackground, modifier = Modifier.fillMaxSize().padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUser?.displayName ?: userProfile.username, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TradingTextPrimary)
                        Text(currentUser?.email ?: userProfile.email, fontSize = 11.sp, color = TradingTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Account Membership: " + (currentUser?.subscriptionType ?: "FREE") + " • Role: " + (currentUser?.role ?: "TRADER"),
                            fontSize = 11.sp,
                            color = if (currentUser?.subscriptionType == "PREMIUM") ElectricCyan else GoldenOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Billing Pricing Upgrader visual block
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, if (currentUser?.subscriptionType == "PREMIUM") ElectricCyan.copy(alpha = 0.6f) else ElectricPurple.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SMARTMONEY JOURNAL PREMIUM MEMBERSHIP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentUser?.subscriptionType == "PREMIUM") ElectricCyan else ElectricPurple,
                        letterSpacing = 1.sp
                    )

                    if (currentUser?.subscriptionType == "PREMIUM") {
                        Text(
                            "Your account is enjoying FULL premium access! Live economic charts, unlimited database trade logs, and comprehensive AI Coaching audits are active.",
                            fontSize = 12.sp,
                            color = TradingTextPrimary
                        )
                        Button(
                            onClick = { viewModel.restoreFreeUser() },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenOrange)
                        ) {
                            Text("Reset back to Free tier", color = CyberBackground, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            "Unlock the full SMC trading toolbox! Free journal limit is 5 logs. Upgrade to Premium and receive absolute unlimited journals and high-frequency AI coaches.",
                            fontSize = 12.sp,
                            color = TradingTextSecondary
                        )
                        Button(
                            onClick = { showPremiumModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                        ) {
                            Text("Upgrade to Premium Pro ($29.99/mo)", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Premium Billing Payment Checkout simulator modal dialog
            if (showPremiumModal) {
                var editCardNo by remember { mutableStateOf("4242 4242 4242 4242") }
                var editExpiry by remember { mutableStateOf("09/28") }
                var editCvc by remember { mutableStateOf("382") }

                Dialog(onDismissRequest = { showPremiumModal = false }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, CyberBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Premium SaaS Stripe Sandbox Checkout", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 16.sp)
                            Text("Demo sandbox is fully operational. Type simulated credentials:", fontSize = 11.sp, color = TradingTextSecondary)

                            OutlinedTextField(
                                value = editCardNo,
                                onValueChange = { editCardNo = it },
                                label = { Text("Simulated Credit Card Number") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = CyberBorder),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editExpiry,
                                    onValueChange = { editExpiry = it },
                                    label = { Text("Expiry (MM/YY)") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = CyberBorder),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = editCvc,
                                    onValueChange = { editCvc = it },
                                    label = { Text("Security CVC") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = CyberBorder),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { showPremiumModal = false }) {
                                    Text("Discard", color = TradingTextSecondary)
                                }
                                Button(
                                    onClick = {
                                        viewModel.upgradeCurrentUserPremium(editCardNo, editExpiry, editCvc) { error ->
                                            if (error != null) {
                                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Welcome to Premium Pro! Subscription verified.", Toast.LENGTH_SHORT).show()
                                                showPremiumModal = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple)
                                ) {
                                    Text("Pay $29.99/mo", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Challenge Tracker Component
            Text("PROP CHALLENGE TRACKERS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TradingTextSecondary, letterSpacing = 1.sp)
            
            challenges.forEach { challenge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(challenge.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TradingTextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (challenge.isActive) TradingWin.copy(alpha = 0.15f) else TradingTextMuted.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (challenge.isActive) "ACTIVE" else "ARCHIVED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (challenge.isActive) TradingWin else TradingTextSecondary
                                )
                            }
                        }

                        Divider(color = CyberBorder.copy(alpha = 0.3f))

                        // Drawdowns & stats
                        val currentProfit = challenge.currentBalance - challenge.startingBalance
                        val progressPercent = (currentProfit / (challenge.startingBalance * (challenge.targetProfitPercent / 100f))).coerceIn(0.0, 1.0)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current Balance:", fontSize = 12.sp, color = TradingTextSecondary)
                            Text(
                                String.format(Locale.US, "$%,.2f", challenge.currentBalance),
                                fontWeight = FontWeight.Bold,
                                color = if (challenge.currentBalance >= challenge.startingBalance) TradingWin else TradingLoss,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Static Drawdown Rules Alert
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Target Profit Limit ($${(challenge.startingBalance * 0.08).toInt()}):", fontSize = 11.sp, color = TradingTextSecondary)
                            Text("${challenge.targetProfitPercent}% ($${(challenge.startingBalance * (challenge.targetProfitPercent / 100f)).toInt()})", fontSize = 11.sp, color = TradingTextPrimary)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Max Daily Drop Limit (5%):", fontSize = 11.sp, color = TradingTextSecondary)
                            Text("-$${(challenge.startingBalance * 0.05).toInt()} Limit", fontSize = 11.sp, color = TradingLoss)
                        }

                        // Progress slider/bar
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Goal Completion Progression:", fontSize = 10.sp, color = TradingTextMuted)
                        LinearProgressIndicator(
                            progress = progressPercent.toFloat(),
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = ElectricCyan,
                            trackColor = CyberSurfaceLighter
                        )
                    }
                }
            }

            // Create custom Challenge
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                var newChName by remember { mutableStateOf("") }
                var newChSBal by remember { mutableStateOf("10000") }

                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CREATE NEW PROP WORKSPACE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TradingTextPrimary)
                    OutlinedTextField(
                        value = newChName,
                        onValueChange = { newChName = it },
                        placeholder = { Text("e.g. MyForexFunds $50K Stage 1") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newChSBal,
                        onValueChange = { newChSBal = it },
                        placeholder = { Text("Starting Balance") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val bal = newChSBal.toDoubleOrNull()
                            if (newChName.isNotEmpty() && bal != null) {
                                viewModel.insertChallenge(Challenge(name = newChName, startingBalance = bal, currentBalance = bal))
                                Toast.makeText(context, "Challenge workspace created successfully!", Toast.LENGTH_SHORT).show()
                                newChName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Instantiate Workspace", color = CyberBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Interactive checklist utilities
            Text("SMC DAILY CONFLUENCE CHECKLIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TradingTextSecondary, letterSpacing = 1.sp)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    checklistItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { viewModel.toggleChecklistItem(item) },
                                    colors = CheckboxDefaults.colors(checkedColor = ElectricCyan, uncheckedColor = TradingTextMuted)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    color = if (item.isChecked) TradingTextMuted else TradingTextPrimary,
                                    modifier = Modifier.width(220.dp)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteChecklistItem(item) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Delete Item", tint = TradingLoss.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Divider(color = CyberBorder.copy(alpha = 0.4f))

                    // Input to append raw tasks
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customCheckItemText,
                            onValueChange = { customCheckItemText = it },
                            placeholder = { Text("Add trading rule checklist entry") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (customCheckItemText.isNotEmpty()) {
                                    viewModel.addChecklistItem(customCheckItemText, "Pre-Trade")
                                    customCheckItemText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricCyan)
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", tint = CyberBackground)
                        }
                    }
                }
            }

            // Gamification Badges Screen
            Text("TRADING BADGE ACHIEVEMENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TradingTextSecondary, letterSpacing = 1.sp)
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { BadgeIconCard(title = "Risk Disciple", unlocked = true, desc = "Risk under 2% standard limit") }
                item { BadgeIconCard(title = "Trend Rider", unlocked = true, desc = "Logged multiple consecutive wins") }
                item { BadgeIconCard(title = "Session Expert", unlocked = false, desc = "Logged London/NY overlaps") }
                item { BadgeIconCard(title = "Pure Calm", unlocked = true, desc = "No greed sentiment trades logged") }
            }

            // Admin Announcements section
            Text("ADMIN COMMAND POST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TradingTextSecondary, letterSpacing = 1.sp)
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SYSTEM INFRASTRUCTURE UPDATE", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = ElectricPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Attention Traders: Starting tomorrow, live economic feed latency thresholds will decrease to under 350 milliseconds. Keep safe stops active ahead of UK Retail Sales release at 07:00 GMT.",
                        fontSize = 11.sp,
                        color = TradingTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeIconCard(title: String, unlocked: Boolean, desc: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (unlocked) CyberSurface else CyberSurfaceLighter)
            .border(
                BorderStroke(1.dp, if (unlocked) ElectricCyan.copy(alpha = 0.6f) else TradingTextMuted.copy(alpha = 0.15f)),
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = if (unlocked) Icons.Filled.MilitaryTech else Icons.Filled.Lock,
                contentDescription = title,
                tint = if (unlocked) ElectricCyan else TradingTextMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (unlocked) TradingTextPrimary else TradingTextMuted)
            Text(desc, fontSize = 9.sp, color = TradingTextMuted, textAlign = TextAlign.Center)
        }
    }
}

// --- POPUP SHEET: ADD TRADE FORM WITH DETAILED PARAMETERS ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTradeDialog(
    onDismiss: () -> Unit,
    onSave: (Trade) -> Unit
) {
    var pair by remember { mutableStateOf("EURUSD") }
    var directionBuy by remember { mutableStateOf(true) } // true = Buy, false = Sell
    var entryP by remember { mutableStateOf("1.0850") }
    var slP by remember { mutableStateOf("1.0838") }
    var tpP by remember { mutableStateOf("1.0890") }
    var sizeLot by remember { mutableStateOf("5.0") }
    var riskPercent by remember { mutableStateOf("1.0") }
    var profitAmount by remember { mutableStateOf("250.00") }
    var session by remember { mutableStateOf("London") }
    var strategy by remember { mutableStateOf("Order Block Tap") }
    var emotion by remember { mutableStateOf("Calm") }
    var preNotes by remember { mutableStateOf("") }
    var resultType by remember { mutableStateOf("WIN") }
    
    // Selectable tags list
    val allTagsList = listOf("BOS", "CHoCH", "Order Block", "FVG", "Liquidity Sweep", "Mitigation Block", "Breaker Block", "OTE Entry")
    val selectedTags = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.5.dp, ElectricCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "RECORD NEW POSITION",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ElectricCyan,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Direction selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { directionBuy = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (directionBuy) ElectricCyan else CyberSurfaceLighter),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BUY", color = if (directionBuy) CyberBackground else TradingTextSecondary, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { directionBuy = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!directionBuy) ElectricPurple else CyberSurfaceLighter),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SELL", color = if (!directionBuy) Color.White else TradingTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                // Grid of essential numbers
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pair,
                        onValueChange = { pair = it },
                        label = { Text("Pair/Symbol") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sizeLot,
                        onValueChange = { sizeLot = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Lot Size") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = entryP,
                        onValueChange = { entryP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Entry Price") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = riskPercent,
                        onValueChange = { riskPercent = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Risk %") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = slP,
                        onValueChange = { slP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Stop Loss") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tpP,
                        onValueChange = { tpP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Take Profit") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = CyberBorder.copy(alpha = 0.5f))

                // Result WIN / LOSS / BE
                Text("Trade Outcome:", fontSize = 11.sp, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("WIN", "LOSS", "BREAKEVEN").forEach { res ->
                        val active = resultType == res
                        Box(
                            modifier = Modifier
                                .clickable { resultType = res }
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) ElectricCyan.copy(alpha = 0.2f) else CyberSurfaceLighter)
                                .border(BorderStroke(1.dp, if (active) ElectricCyan else CyberBorder), RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(res, color = if (active) ElectricCyan else TradingTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = profitAmount,
                    onValueChange = { profitAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("P/L Absolute Profit (USD)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdowns for parameters
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = session,
                        onValueChange = { session = it },
                        label = { Text("Session (London/NY/Asia)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = emotion,
                        onValueChange = { emotion = it },
                        label = { Text("Emotion") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = strategy,
                    onValueChange = { strategy = it },
                    label = { Text("SMC Setup Strategy Used") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                // Selectable tag items
                Text("Select SMC Concepts tags:", fontSize = 11.sp, color = TradingTextSecondary, fontWeight = FontWeight.Bold)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allTagsList.forEach { tag ->
                        val selected = selectedTags.contains(tag)
                        Box(
                            modifier = Modifier
                                .clickable {
                                    if (selected) selectedTags.remove(tag) else selectedTags.add(tag)
                                }
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (selected) ElectricCyan.copy(alpha = 0.15f) else CyberSurfaceLighter)
                                .border(BorderStroke(0.5.dp, if (selected) ElectricCyan else TradingTextMuted.copy(alpha = 0.3f)), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(tag, color = if (selected) ElectricCyan else TradingTextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                OutlinedTextField(
                    value = preNotes,
                    onValueChange = { preNotes = it },
                    label = { Text("Pre/Post-Trade Notes") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder),
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceLighter),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TradingTextSecondary)
                    }

                    Button(
                        onClick = {
                            val ep = entryP.toDoubleOrNull() ?: 1.0
                            val sl = slP.toDoubleOrNull() ?: 0.99
                            val tp = tpP.toDoubleOrNull() ?: 1.01
                            val lS = sizeLot.toDoubleOrNull() ?: 1.0
                            val rP = riskPercent.toDoubleOrNull() ?: 1.0
                            
                            val rawPnL = profitAmount.toDoubleOrNull() ?: 0.0
                            // Force negative sign if loss result is chosen
                            val signedPnL = if (resultType == "LOSS" && rawPnL > 0) -rawPnL else if (resultType == "WIN" && rawPnL < 0) -rawPnL else rawPnL

                            val newTrade = Trade(
                                pair = pair,
                                isBuy = directionBuy,
                                entryPrice = ep,
                                stopLoss = sl,
                                takeProfit = tp,
                                lotSize = lS,
                                riskPercent = rP,
                                resultType = resultType,
                                profitResult = signedPnL,
                                timestamp = System.currentTimeMillis(),
                                session = session,
                                smcTags = selectedTags.joinToString(","),
                                strategyUsed = strategy,
                                emotion = emotion,
                                preTradeNotes = preNotes
                            )
                            onSave(newTrade)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Position", color = CyberBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
