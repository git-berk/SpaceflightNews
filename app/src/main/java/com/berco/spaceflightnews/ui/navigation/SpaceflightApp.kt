package com.berco.spaceflightnews.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun SpaceflightApp() {
    SpaceflightNavHost(
        navController = rememberNavController(),
        modifier = Modifier.fillMaxSize(),
    )
}
