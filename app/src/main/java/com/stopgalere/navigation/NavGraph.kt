package com.stopgalere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stopgalere.presentation.ui.intro.WizardPagerScreen
import com.stopgalere.presentation.ui.job.JobDetailScreen
import com.stopgalere.presentation.ui.main.MainScreen
import com.stopgalere.presentation.ui.splash.SplashScreen


sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Intro : Route("intro")
    data object Main : Route("main")
    data object JobDetail : Route("jobDetail/{jobId}") {
        fun of(jobId: String) = "jobDetail/$jobId"
        const val ARG_ID = "jobId"
    }
}
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Main.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Splash.path) {
            SplashScreen(navController = navController)
        }
        composable(Route.Intro.path) {
            WizardPagerScreen(navController = navController)
        }
        composable(Route.Main.path) {
            MainScreen(navController = navController)
        }

        composable(
            route = Route.JobDetail.path,
            arguments = listOf(navArgument(Route.JobDetail.ARG_ID) { type = NavType.StringType })
        ) {
            // Hilt will inject JobDetailViewModel and read the "jobId" from SavedStateHandle
            JobDetailScreen(navController = navController)
        }
    }
}

/** Helper for cleaner call-sites */
fun NavHostController.navigateToJobDetail(jobId: String) {
    navigate(Route.JobDetail.of(jobId))
}
