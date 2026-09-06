package com.berco.spaceflightnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.berco.spaceflightnews.core.ui.theme.SpaceflightTheme
import com.berco.spaceflightnews.ui.feed.FeedScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpaceflightTheme {
                FeedScreen(
                    onArticleClick = {},
                    modifier = Modifier,
                )
            }
        }
    }
}
