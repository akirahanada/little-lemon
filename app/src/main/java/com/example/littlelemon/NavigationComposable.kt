package com.example.littlelemon

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("LittleLemon", Context.MODE_PRIVATE)
    
    // Determine start destination based on whether user data exists
    val startDestination = if (sharedPreferences.contains("firstName")) {
        Home.route
    } else {
        Onboarding.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Onboarding.route) {
            Onboarding(navController = navController)
        }
        
        composable(Home.route) {
            Home(navController = navController)
        }
        
        composable(Profile.route) {
            Profile(navController = navController)
        }
    }
}
