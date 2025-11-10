package com.yomi.next2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.yomi.next2go.ui.screens.RaceScreen
import com.yomi.next2go.viewmodel.RaceViewModel
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Next2GoTheme {
                val viewModel: RaceViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RaceScreen(
                        uiState = uiState,
                        onIntent = viewModel::handleIntent,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
