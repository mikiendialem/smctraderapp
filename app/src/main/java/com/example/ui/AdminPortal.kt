package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AdminPortalScreen(viewModel: TradeViewModel) {
    var activeAdminTab by remember { mutableStateOf(0) } // 0 = Users, 1 = Trades, 2 = System, 3 = Financials
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Admin Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ElectricPurple.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = "Admin Indicator",
                        tint = ElectricPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ADMIN CONTROL PORTAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricPurple,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Global System & Database Panel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TradingTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Tab Selectors
        ScrollableTabRow(
            selectedTabIndex = activeAdminTab,
            containerColor = Color.Transparent,
            divider = {},
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty() && activeAdminTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeAdminTab]),
                        color = ElectricPurple
                    )
                }
            }
        ) {
            val adminTabs = listOf("Users Management", "All Trade Journals", "System & Flags", "Billing Controls")
            adminTabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeAdminTab == index,
                    onClick = { activeAdminTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (activeAdminTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (activeAdminTab == index) ElectricPurple else TradingTextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Contents Screen Switch
        Box(modifier = Modifier.weight(1f)) {
            when (activeAdminTab) {
                0 -> AdminUsersModule(viewModel)
                1 -> AdminTradesModule(viewModel)
                2 -> AdminSystemControlModule(viewModel)
                3 -> AdminFinancialsModule(viewModel)
            }
        }
    }
}

// --- MODULE 1: ADMIN USERS MODULE ---
@Composable
fun AdminUsersModule(viewModel: TradeViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val logs by viewModel.allActivityLogs.collectAsStateWithLifecycle()
    
    var showEditUserDialog by remember { mutableStateOf<User?>(null) }
    var showResetPassDialog by remember { mutableStateOf<User?>(null) }
    var viewActivityLogsDialog by remember { mutableStateOf<User?>(null) }

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REGISTERED USERS DATABASE (${users.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                letterSpacing = 1.sp
            )
            
            // Stats summary
            Text(
                text = "Active: ${users.count { !it.isBanned }} • Banned: ${users.count { it.isBanned }}",
                fontSize = 11.sp,
                color = TradingTextMuted
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No user accounts found in database.", color = TradingTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (user.isBanned) TradingLoss.copy(alpha = 0.04f) else CyberSurface
                        ),
                        border = BorderStroke(
                            1.dp, 
                            if (user.isBanned) TradingLoss.copy(alpha = 0.6f) else CyberBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (user.role) {
                                            "ADMIN" -> Icons.Filled.AdminPanelSettings
                                            "DEMO" -> Icons.Filled.History
                                            else -> Icons.Filled.Person
                                        },
                                        contentDescription = "Role Logo",
                                        tint = when (user.role) {
                                            "ADMIN" -> ElectricPurple
                                            "DEMO" -> GoldenOrange
                                            else -> ElectricCyan
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = user.displayName.ifBlank { "User ${user.id}" },
                                            fontWeight = FontWeight.Bold,
                                            color = TradingTextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = user.email,
                                            fontSize = 11.sp,
                                            color = TradingTextSecondary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Banned Indicator Badge
                                    if (user.isBanned) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(TradingLoss.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("BANNED", fontWeight = FontWeight.Bold, color = TradingLoss, fontSize = 9.sp)
                                        }
                                    }

                                    // Role Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberSurfaceLighter)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(user.role, fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 9.sp)
                                    }

                                    // Subscription Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (user.subscriptionType == "PREMIUM") ElectricCyan.copy(alpha = 0.15f) else CyberSurfaceLighter)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            user.subscriptionType, 
                                            fontWeight = FontWeight.Bold, 
                                            color = if (user.subscriptionType == "PREMIUM") ElectricCyan else TradingTextMuted, 
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = CyberBorder.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive Row of Admin Actions for this user
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Joined: " + SimpleDateFormat("dd MMM, yyyy", Locale.US).format(Date(user.createdAt)),
                                    fontSize = 11.sp,
                                    color = TradingTextMuted
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { viewActivityLogsDialog = user },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.History, "View Activity logs", tint = TradingTextSecondary, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { showResetPassDialog = user },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.LockReset, "Reset Account Password", tint = GoldenOrange, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { showEditUserDialog = user },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Edit, "Edit permissions", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                    }

                                    // Ban modifier
                                    IconButton(
                                        onClick = {
                                            viewModel.toggleUserBanState(user)
                                            Toast.makeText(context, "User profile updated successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (user.isBanned) Icons.Filled.CheckCircle else Icons.Filled.Block,
                                            contentDescription = "Ban/Unban",
                                            tint = if (user.isBanned) TradingWin else TradingLoss,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Delete user
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteUserByAdmin(user)
                                            Toast.makeText(context, "User records deleted successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, "Delete User records", tint = TradingLoss, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit permissions Dialog
    if (showEditUserDialog != null) {
        val editing = showEditUserDialog!!
        var nameField by remember { mutableStateOf(editing.displayName) }
        var roleField by remember { mutableStateOf(editing.role) }
        var planField by remember { mutableStateOf(editing.subscriptionType) }

        Dialog(onDismissRequest = { showEditUserDialog = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Edit User: ${editing.email}", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 16.sp)
                    
                    OutlinedTextField(
                        value = nameField,
                        onValueChange = { nameField = it },
                        label = { Text("Display Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder)
                    )

                    // Role selection row
                    Column {
                        Text("Assign Role:", fontSize = 12.sp, color = TradingTextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("ADMIN", "TRADER", "DEMO").forEach { r ->
                                FilterChip(
                                    selected = roleField == r,
                                    onClick = { roleField = r },
                                    label = { Text(r, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricPurple.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }

                    // Plan selection row
                    Column {
                        Text("Subscription Pricing Level:", fontSize = 12.sp, color = TradingTextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("FREE", "PREMIUM").forEach { p ->
                                FilterChip(
                                    selected = planField == p,
                                    onClick = { planField = p },
                                    label = { Text(p, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricCyan.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showEditUserDialog = null }) {
                            Text("Cancel", color = TradingTextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.editUserByAdmin(editing, nameField, roleField, planField)
                                Toast.makeText(context, "Saved changes!", Toast.LENGTH_SHORT).show()
                                showEditUserDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Text("Save Configurations", color = CyberBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Reset password dialog
    if (showResetPassDialog != null) {
        val resetting = showResetPassDialog!!
        var passField by remember { mutableStateOf("password123") }

        Dialog(onDismissRequest = { showResetPassDialog = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Force Reset Password", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 16.sp)
                    Text("Type a new access pass for ${resetting.email}:", fontSize = 13.sp, color = TradingTextSecondary)

                    OutlinedTextField(
                        value = passField,
                        onValueChange = { passField = it },
                        label = { Text("New Password") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldenOrange, unfocusedBorderColor = CyberBorder)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showResetPassDialog = null }) {
                            Text("Cancel", color = TradingTextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.resetUserPasswordByAdmin(resetting, passField)
                                Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                showResetPassDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenOrange)
                        ) {
                            Text("Update Access Pass", color = CyberBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // View user specific activity logs dialog
    if (viewActivityLogsDialog != null) {
        val loggingUser = viewActivityLogsDialog!!
        val filteredLogs = logs.filter { it.email == loggingUser.email }

        Dialog(onDismissRequest = { viewActivityLogsDialog = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("User Session Log histories", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 15.sp)
                            Text(loggingUser.email, fontSize = 11.sp, color = TradingTextSecondary)
                        }
                        IconButton(onClick = { viewActivityLogsDialog = null }) {
                            Icon(Icons.Filled.Close, "Close logs tray", tint = TradingTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredLogs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No activity instances logged yet.", color = TradingTextMuted, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredLogs) { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                                    border = BorderStroke(1.dp, CyberBorder)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                item.action, 
                                                fontWeight = FontWeight.Bold, 
                                                fontSize = 12.sp, 
                                                color = if (item.action.contains("FAIL") || item.action.contains("BLOCKED")) TradingLoss else ElectricCyan
                                            )
                                            Text(
                                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(item.timestamp)), 
                                                fontSize = 11.sp, 
                                                color = TradingTextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(item.details, fontSize = 12.sp, color = TradingTextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- MODULE 2: ALL TRADES LEDGER MODULE ---
@Composable
fun AdminTradesModule(viewModel: TradeViewModel) {
    val trades by viewModel.uiTrades.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val selectedTrader by viewModel.selectedTraderEmailForAdminView.collectAsStateWithLifecycle()

    var showEditTradeDialog by remember { mutableStateOf<Trade?>(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GLOBAL TRADE JOURNAL DATABASE (${trades.size} items)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filtering Tray to view select trader only
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Filter by User ID:", fontSize = 12.sp, color = TradingTextSecondary)
            
            // Render basic filter chip horizontal slider row
            Row(
                modifier = Modifier
                    .fillModifierHorizontalScroll()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All").plus(users.map { it.email }).forEach { email ->
                    FilterChip(
                        selected = selectedTrader == email,
                        onClick = { viewModel.selectedTraderEmailForAdminView.value = email },
                        label = {
                            Text(
                                if (email == "All") "Global All" else email.take(15) + "...",
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricPurple.copy(alpha = 0.25f),
                            selectedLabelColor = ElectricPurple
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (trades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No matching trade transactions found.", color = TradingTextSecondary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(trades) { trade ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, CyberBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (trade.isBuy) TradingWin.copy(alpha = 0.15f) else TradingLoss.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (trade.isBuy) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                                            contentDescription = "Trade direction",
                                            tint = if (trade.isBuy) TradingWin else TradingLoss,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            trade.pair, 
                                            fontWeight = FontWeight.Bold, 
                                            color = TradingTextPrimary, 
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            "Owned by: ${trade.userId}", 
                                            fontSize = 11.sp, 
                                            color = ElectricCyan
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (trade.resultType) {
                                                    "WIN" -> TradingWin.copy(alpha = 0.15f)
                                                    "LOSS" -> TradingLoss.copy(alpha = 0.15f)
                                                    else -> GoldenOrange.copy(alpha = 0.15f)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "%s$%,.2f", if (trade.profitResult >= 0) "+" else "", trade.profitResult),
                                            fontWeight = FontWeight.Bold,
                                            color = when (trade.resultType) {
                                                "WIN" -> TradingWin
                                                "LOSS" -> TradingLoss
                                                else -> GoldenOrange
                                            },
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lot Size: ${trade.lotSize} • Risk: ${trade.riskPercent}% • session: ${trade.session} • Strategy: ${trade.strategyUsed}",
                                fontSize = 11.sp,
                                color = TradingTextSecondary
                            )

                            if (trade.preTradeNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: \"${trade.preTradeNotes}\"",
                                    fontSize = 11.sp,
                                    color = TradingTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = CyberBorder.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = SimpleDateFormat("dd MMM, HH:mm", Locale.US).format(Date(trade.timestamp)),
                                    fontSize = 11.sp,
                                    color = TradingTextMuted
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { showEditTradeDialog = trade },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Edit, "Edit Trade record", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteTrade(trade)
                                            Toast.makeText(context, "Trade entry wiped instantly", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Filled.Delete, "Delete Trade record", tint = TradingLoss, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit trade details dialog for Admin Data Overrider
    if (showEditTradeDialog != null) {
        val editing = showEditTradeDialog!!
        var editProfit by remember { mutableStateOf(editing.profitResult.toString()) }
        var editNotes by remember { mutableStateOf(editing.preTradeNotes) }
        var editStrategy by remember { mutableStateOf(editing.strategyUsed) }

        Dialog(onDismissRequest = { showEditTradeDialog = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Modify Trade Log overrides", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 16.sp)
                    Text("Overriding trade logged by ${editing.userId}", fontSize = 11.sp, color = ElectricCyan)

                    OutlinedTextField(
                        value = editProfit,
                        onValueChange = { editProfit = it },
                        label = { Text("Revenue Profit ($ Amount)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder)
                    )

                    OutlinedTextField(
                        value = editStrategy,
                        onValueChange = { editStrategy = it },
                        label = { Text("Strategy Used") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder)
                    )

                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Trade Observations") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricCyan, unfocusedBorderColor = CyberBorder)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showEditTradeDialog = null }) {
                            Text("Cancel", color = TradingTextSecondary)
                        }
                        Button(
                            onClick = {
                                val valProfit = editProfit.toDoubleOrNull() ?: editing.profitResult
                                val updatedTrade = editing.copy(
                                    profitResult = valProfit,
                                    preTradeNotes = editNotes,
                                    strategyUsed = editStrategy,
                                    resultType = if (valProfit > 0) "WIN" else if (valProfit < 0) "LOSS" else "BREAKEVEN"
                                )
                                viewModel.updateTrade(updatedTrade)
                                Toast.makeText(context, "Trade modified!", Toast.LENGTH_SHORT).show()
                                showEditTradeDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Text("Apply Override", color = CyberBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- MODULE 3: SYSTEM CONTROL & FEATURE FLAGS ---
@Composable
fun AdminSystemControlModule(viewModel: TradeViewModel) {
    val logs by viewModel.allActivityLogs.collectAsStateWithLifecycle()
    
    val coachEnabled by viewModel.aiCoachEnabled.collectAsStateWithLifecycle()
    val coachTone by viewModel.aiCoachTone.collectAsStateWithLifecycle()
    val demoModeActive by viewModel.demoModeActive.collectAsStateWithLifecycle()
    val globalNotificationsEnabled by viewModel.globalNotificationsEnabled.collectAsStateWithLifecycle()

    var announceTitle by remember { mutableStateOf("") }
    var announceMsg by remember { mutableStateOf("") }
    var announceType by remember { mutableStateOf("ANNOUNCEMENT") } // ANNOUNCEMENT, SYSTEM, ALERT

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feature flags settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "GLOBAL APPLICATION FEATURE FLAGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricPurple,
                    letterSpacing = 1.sp
                )

                // Row - AI Assistant enabled
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Gemini AI Copilot", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 14.sp)
                        Text("Globally enable/disable AI coach workspace", fontSize = 11.sp, color = TradingTextSecondary)
                    }
                    Switch(
                        checked = coachEnabled,
                        onCheckedChange = { viewModel.toggleSystemSettingByAdmin("AI_COACH_ENABLED") },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.4f))
                    )
                }

                Divider(color = CyberBorder.copy(alpha = 0.4f))

                // Row - Tone selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AI Coach Tone Configuration", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 14.sp)
                    Text("Current active model tone: $coachTone", fontSize = 11.sp, color = ElectricCyan)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        listOf("Empathetic Professional", "Strict Risk Officer", "Encouraging Mate").forEach { tone ->
                            FilterChip(
                                selected = coachTone == tone,
                                onClick = { viewModel.updateAICoachToneByAdmin(tone) },
                                label = { Text(tone, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricCyan.copy(alpha = 0.15f),
                                    selectedLabelColor = ElectricCyan
                                )
                            )
                        }
                    }
                }

                Divider(color = CyberBorder.copy(alpha = 0.4f))

                // Row - Demo Session Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Simulator Demo Mode", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 14.sp)
                        Text("Wipe live filters or simulate instant trial assets", fontSize = 11.sp, color = TradingTextSecondary)
                    }
                    Switch(
                        checked = demoModeActive,
                        onCheckedChange = { viewModel.toggleSystemSettingByAdmin("DEMO_MODE_ACTIVE") },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.4f))
                    )
                }

                Divider(color = CyberBorder.copy(alpha = 0.4f))

                // Row - Global push notices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Global Real-time Notifications", fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 14.sp)
                        Text("Allow OTP code popups and live system alerts", fontSize = 11.sp, color = TradingTextSecondary)
                    }
                    Switch(
                        checked = globalNotificationsEnabled,
                        onCheckedChange = { viewModel.toggleSystemSettingByAdmin("GLOBAL_NOTIFICATIONS_ENABLED") },
                        colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ElectricCyan.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // Announcement Publisher
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "PUBLISH SYSTEM ANNOUNCEMENT BLAST",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricPurple,
                    letterSpacing = 1.sp
                )

                OutlinedTextField(
                    value = announceTitle,
                    onValueChange = { announceTitle = it },
                    label = { Text("Announcement Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = announceMsg,
                    onValueChange = { announceMsg = it },
                    label = { Text("Broadcast Message content") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPurple, unfocusedBorderColor = CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                // Type Chips selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Notice Type: ", fontSize = 11.sp, color = TradingTextSecondary)
                    listOf("ANNOUNCEMENT", "SYSTEM", "ALERT").forEach { type ->
                        FilterChip(
                            selected = announceType == type,
                            onClick = { announceType = type },
                            label = { Text(type, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricPurple.copy(alpha = 0.15f),
                                selectedLabelColor = ElectricPurple
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (announceTitle.isNotBlank() && announceMsg.isNotBlank()) {
                            viewModel.sendAnnouncementByAdmin(announceTitle, announceMsg, announceType)
                            Toast.makeText(context, "Announcement Broadcast Published!", Toast.LENGTH_SHORT).show()
                            announceTitle = ""
                            announceMsg = ""
                        } else {
                            Toast.makeText(context, "Please complete fields before blasting", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Announcement, "announcement")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Announcement notice", fontWeight = FontWeight.Bold)
                }
            }
        }

        // System Activity Logs terminal
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "GLOBAL AUDIT ACTIVITY LOGGER (${logs.size} instances)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBackground)
                        .border(BorderStroke(1.dp, CyberBorder))
                        .padding(8.dp)
                ) {
                    if (logs.isEmpty()) {
                        Text("Logging terminal clear. Ready.", color = TradingTextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(logs) { log ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "[${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp))}] ${log.email}",
                                            color = ElectricCyan,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = log.action,
                                            color = if (log.action.contains("FAIL") || log.action.contains("BLOCKED")) TradingLoss else ElectricPurple,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = log.details,
                                        color = TradingTextPrimary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Divider(color = CyberBorder.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- MODULE 4: FINANCIALS & BILLING CONTROLS ---
@Composable
fun AdminFinancialsModule(viewModel: TradeViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val logs by viewModel.allActivityLogs.collectAsStateWithLifecycle()

    val premiumUsersCount = users.count { it.subscriptionType == "PREMIUM" }
    val freeUsersCount = users.count { it.subscriptionType == "FREE" }
    val totalEstimatedRevenue = premiumUsersCount * 29.99

    val paymentPurchases = logs.filter { it.action == "PREMIUM_PURCHASE" }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Earnings Stats Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.5.dp, TradingWin.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "TOTAL SIMULATED MRR (RECURRING REVENUE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradingWin,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.US, "$%,.2f", totalEstimatedRevenue),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TradingTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Based on $premiumUsersCount active premium accounts @ $29.99/mo",
                    fontSize = 11.sp,
                    color = TradingTextSecondary
                )
            }
        }

        // Subscriptions details breakdown grid counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(95.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("ACTIVE PREMIUMS", fontSize = 11.sp, color = TradingTextSecondary, fontWeight = FontWeight.SemiBold)
                    Text("$premiumUsersCount accounts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(95.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("FREE USERS CAP", fontSize = 11.sp, color = TradingTextSecondary, fontWeight = FontWeight.SemiBold)
                    Text("$freeUsersCount accounts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TradingTextMuted)
                }
            }
        }

        // Payments invoice logs register
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "PREMIUM TRANSACTION INVOICES LEDGER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ElectricPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (paymentPurchases.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payment transactions recorded yet.", color = TradingTextMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.height(250.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(paymentPurchases) { transaction ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CyberSurfaceLighter),
                                border = BorderStroke(1.dp, CyberBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(transaction.email, fontWeight = FontWeight.Bold, color = TradingTextPrimary, fontSize = 13.sp)
                                        Text(
                                            transaction.details.substringAfter("Mock receipt: "), 
                                            fontSize = 11.sp, 
                                            color = TradingTextSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("$29.99", fontWeight = FontWeight.Bold, color = TradingWin, fontSize = 14.sp)
                                        Text(
                                            SimpleDateFormat("dd MMM, HH:mm", Locale.US).format(Date(transaction.timestamp)), 
                                            fontSize = 10.sp, 
                                            color = TradingTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- CUSTOM MODIFIER FOR FILTER TRAY HORIZONTAL SCROLL COMPATIBILITIES ---
@Composable
fun Modifier.fillModifierHorizontalScroll(): Modifier {
    return this.horizontalScroll(rememberScrollState())
}
