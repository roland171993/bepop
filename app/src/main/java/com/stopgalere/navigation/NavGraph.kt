package com.stopgalere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.stopgalere.presentation.ui.about.AboutScreen
import com.stopgalere.presentation.ui.coverletter.CoverLetterDetailScreen
import com.stopgalere.presentation.ui.coverletter.CoverLetterScreen
import com.stopgalere.presentation.ui.intro.WizardPagerScreen
import com.stopgalere.presentation.ui.job.JobDetailScreen
import com.stopgalere.presentation.ui.main.MainScreen
import com.stopgalere.presentation.ui.splash.SplashScreen


sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Intro : Route("intro")
    data object Main : Route("main")

    data object About : Route("about")

    data object CoverLetter : Route("coverLetter")
    data object JobDetail : Route("jobDetail/{jobId}") {
        fun of(jobId: String) = "jobDetail/$jobId"
        const val ARG_ID = "jobId"
    }

    data object CoverLetterDetail : Route("coverLetterDetail/{coverLetterId}") {
        fun of(id: String) = "coverLetterDetail/$id"
        const val ARG_ID = "coverLetterId"
    }
}

// SingleTop + Save/Restore state navigation for all routes
fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        // Keep only one instance and restore state if we had visited before
        launchSingleTop = true
        restoreState = true
        // Do not grow the back stack when switching top-level items; keep state
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

/** Helpers for cleaner call-sites */
fun NavHostController.navigateToJobDetail(jobId: String) =
    navigateSingleTopTo(Route.JobDetail.of(jobId))

fun NavHostController.navigateToCoverLetterDetail(id: String) =
    navigateSingleTopTo(Route.CoverLetterDetail.of(id))


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Splash.path
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
        composable(Route.About.path) {
            AboutScreen(navController = navController)
        }
        composable(Route.Main.path) {
            MainScreen(
                navController = navController,
                onOpenJobDetail = {jobId ->
                    navController.navigateToJobDetail(jobId)
                })
        }

        composable(
            route = Route.JobDetail.path,
            arguments = listOf(navArgument(Route.JobDetail.ARG_ID) { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "stopgalere://job/{${Route.JobDetail.ARG_ID}}" }
            )
        ) {
            JobDetailScreen(navController = navController)
        }

        composable(Route.CoverLetter.path){
            CoverLetterScreen(
                navController = navController,
                onOpenDetail = { id ->
                    navController.navigateToCoverLetterDetail(id)
                })
        }

        composable(
            route = Route.CoverLetterDetail.path,
            arguments = listOf(navArgument(Route.CoverLetterDetail.ARG_ID) { type = NavType.StringType })
        ) {
            CoverLetterDetailScreen(
                navController = navController
            )
        }
    }
}

