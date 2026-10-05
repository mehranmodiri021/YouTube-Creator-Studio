package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.MainApp
import com.example.ui.theme.YouTubeCreatorStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.core.database.DatabaseModule.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            YouTubeCreatorStudioTheme(darkTheme = true) {
                MainApp()
            }
        }
    }
}
