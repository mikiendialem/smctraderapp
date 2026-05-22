package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.SmartMoneyHub
import com.example.ui.TradeViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Secure stateful ViewModel with Custom Database Injection Factory
        val viewModel = ViewModelProvider(
            this, 
            TradeViewModel.provideFactory(this.application)
        )[TradeViewModel::class.java]

        setContent {
            MyApplicationTheme {
                SmartMoneyHub(viewModel = viewModel)
            }
        }
    }
}
