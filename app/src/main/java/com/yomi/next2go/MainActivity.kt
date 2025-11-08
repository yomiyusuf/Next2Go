package com.yomi.next2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yomi.next2go.ui.theme.Next2GoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Next2GoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NextToGoApp(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun NextToGoApp(modifier: Modifier = Modifier) {
    Text(
        text = "Next2Go Racing",
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun NextToGoAppPreview() {
    Next2GoTheme {
        NextToGoApp()
    }
}
