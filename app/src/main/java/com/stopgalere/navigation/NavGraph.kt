package com.stopgalere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stopgalere.presentation.ui.intro.WizardPagerScreen
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
            SplashScreen(navController = navController)
        }
        composable("intro"){
            WizardPagerScreen(navController = navController)
        }
        composable("main"){
            MainScreen(navController)
        }
    }
}
