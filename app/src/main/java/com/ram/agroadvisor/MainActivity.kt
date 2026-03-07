package com.ram.agroadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ram.agroadvisor.ui.navigation.NavGraph
import com.ram.agroadvisor.ui.theme.AgroAdvisorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgroAdvisorTheme {
                NavGraph()
            }
        }
    }
}