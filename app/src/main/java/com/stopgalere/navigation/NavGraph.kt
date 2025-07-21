package com.stopgalere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stopgalere.R
import com.stopgalere.navigation.destinations.Screen
import com.stopgalere.presentation.ui.about.AboutScreen
import com.stopgalere.presentation.ui.coverletter.CoverList
import com.stopgalere.presentation.ui.job.JobList
import com.stopgalere.presentation.ui.resume.ResumeList
import com.stopgalere.presentation.ui.main.MainScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                navController = navController,
                title = stringResource(R.string.screen_main_title)
            )
        }
        composable(Screen.JobList.route) {
            JobList(
                navController = navController,
                title = stringResource(R.string.screen_jobs_title)
            )
        }
        composable(Screen.ResumeList.route) {
            ResumeList(
                navController = navController,
                title = stringResource(R.string.screen_resumes_title)
            )
        }
        composable(Screen.CoverList.route) {
            CoverList(
                navController = navController,
                title = stringResource(R.string.screen_coverletters_title)
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                title = stringResource(R.string.screen_about_title)
            )
        }
    }
}
