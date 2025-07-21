package com.stopgalere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stopgalere.presentation.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            // 🌟 Pass the controller here!
            SplashScreen(navController = navController)
        }
//        composable("intro") {
//            IntroWizard(navController = navController)
//        }
//        composable("main") {
//            MainScreen(navController = navController)
//        }
        // …other routes
    }
}
