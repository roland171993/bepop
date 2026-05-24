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
import com.stopgalere.presentation.ui.ai.AiCoverLetterScreen
import com.stopgalere.presentation.ui.ai.AiProPhotoScreen
import com.stopgalere.presentation.ui.auth.LoginScreen
import com.stopgalere.presentation.ui.auth.ProfileScreen
import com.stopgalere.presentation.ui.auth.RegisterScreen
import com.stopgalere.presentation.ui.auth.UpdateProfileScreen
import com.stopgalere.presentation.ui.call.VideoCallScreen
import com.stopgalere.presentation.ui.chat.ChatScreen
import com.stopgalere.presentation.ui.coverletter.CoverLetterDetailScreen
import com.stopgalere.presentation.ui.coverletter.CoverLetterScreen
import com.stopgalere.presentation.ui.intro.WizardPagerScreen
import com.stopgalere.presentation.ui.job.JobDetailScreen
import com.stopgalere.presentation.ui.main.MainScreen
import com.stopgalere.presentation.ui.splash.SplashScreen

// ─── Route definitions ────────────────────────────────────────────────────────

sealed class Route(val path: String) {
    // App flow
    data object Splash  : Route("splash")
    data object Intro   : Route("intro")
    data object Main    : Route("main")
    data object About   : Route("about")

    // Auth flow
    data object Login         : Route("login")
    data object Register      : Route("register")
    data object Profile       : Route("profile")
    data object UpdateProfile : Route("updateProfile")

    // Feature routes
    data object Chat : Route("chat/{userId}") {
        fun of(userId: String) = "chat/$userId"
        const val ARG_USER_ID = "userId"
    }
    data object VideoCall : Route("videoCall/{roomId}") {
        fun of(roomId: String) = "videoCall/$roomId"
        const val ARG_ROOM_ID = "roomId"
    }
    data object CoverLetter : Route("coverLetter")
    data object JobDetail   : Route("jobDetail/{jobId}") {
        fun of(jobId: String) = "jobDetail/$jobId"
        const val ARG_ID = "jobId"
    }
    data object CoverLetterDetail : Route("coverLetterDetail/{coverLetterId}") {
        fun of(id: String) = "coverLetterDetail/$id"
        const val ARG_ID = "coverLetterId"
    }
    data object AiCoverLetter : Route("aiCoverLetter/{jobId}/{jobTitle}/{jobDescription}/{jobCompany}") {
        fun of(jobId: String, jobTitle: String, jobDescription: String, jobCompany: String) =
            "aiCoverLetter/$jobId/${android.net.Uri.encode(jobTitle)}/${android.net.Uri.encode(jobDescription)}/${android.net.Uri.encode(jobCompany)}"
        const val ARG_JOB_ID = "jobId"
        const val ARG_JOB_TITLE = "jobTitle"
        const val ARG_JOB_DESCRIPTION = "jobDescription"
        const val ARG_JOB_COMPANY = "jobCompany"
    }
    data object AiProPhoto : Route("aiProPhoto/{jobId}/{jobTitle}") {
        fun of(jobId: String, jobTitle: String) = "aiProPhoto/$jobId/${android.net.Uri.encode(jobTitle)}"
        const val ARG_JOB_ID = "jobId"
        const val ARG_JOB_TITLE = "jobTitle"
    }
}

// ─── Navigation helpers ───────────────────────────────────────────────────────

fun NavHostController.navigateSingleTopTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState    = true
        popUpTo(graph.findStartDestination().id) { saveState = true }
    }
}

fun NavHostController.navigateToChat(userId: String) =
    navigateSingleTopTo(Route.Chat.of(userId))

fun NavHostController.navigateToVideoCall(roomId: String) =
    navigate(Route.VideoCall.of(roomId))

fun NavHostController.navigateToJobDetail(jobId: String) =
    navigateSingleTopTo(Route.JobDetail.of(jobId))

fun NavHostController.navigateToCoverLetterDetail(id: String) =
    navigateSingleTopTo(Route.CoverLetterDetail.of(id))

fun NavHostController.navigateToAiCoverLetter(jobId: String, jobTitle: String, jobDescription: String, jobCompany: String) =
    navigate(Route.AiCoverLetter.of(jobId, jobTitle, jobDescription, jobCompany))

fun NavHostController.navigateToAiProPhoto(jobId: String, jobTitle: String) =
    navigate(Route.AiProPhoto.of(jobId, jobTitle))

// ─── NavGraph ─────────────────────────────────────────────────────────────────

@Composable
fun NavGraph(
    navController:    NavHostController = rememberNavController(),
    startDestination: String            = Route.Splash.path
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // ── App flow ──────────────────────────────────────────────────
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
                navController    = navController,
                onOpenJobDetail  = { jobId -> navController.navigateToJobDetail(jobId) }
            )
        }

        // ── Auth flow ─────────────────────────────────────────────────
        composable(Route.Login.path) {
            LoginScreen(navController = navController)
        }
        composable(Route.Register.path) {
            RegisterScreen(navController = navController)
        }
        composable(Route.Profile.path) {
            ProfileScreen(navController = navController)
        }
        composable(Route.UpdateProfile.path) {
            UpdateProfileScreen(navController = navController)
        }

        // ── Feature routes ────────────────────────────────────────────

        // Customer-support chat
        composable(
            route     = Route.Chat.path,
            arguments = listOf(navArgument(Route.Chat.ARG_USER_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(Route.Chat.ARG_USER_ID) ?: ""
            ChatScreen(navController = navController, userId = userId)
        }

        // Video call
        composable(
            route     = Route.VideoCall.path,
            arguments = listOf(navArgument(Route.VideoCall.ARG_ROOM_ID) { type = NavType.StringType; nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString(Route.VideoCall.ARG_ROOM_ID)
            VideoCallScreen(navController = navController, roomId = roomId)
        }

        composable(
            route     = Route.JobDetail.path,
            arguments = listOf(navArgument(Route.JobDetail.ARG_ID) { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "stopgalere://job/{${Route.JobDetail.ARG_ID}}" }
            )
        ) {
            JobDetailScreen(navController = navController)
        }

        composable(Route.CoverLetter.path) {
            CoverLetterScreen(
                navController = navController,
                onOpenDetail  = { id -> navController.navigateToCoverLetterDetail(id) }
            )
        }

        composable(
            route     = Route.CoverLetterDetail.path,
            arguments = listOf(navArgument(Route.CoverLetterDetail.ARG_ID) { type = NavType.StringType })
        ) {
            CoverLetterDetailScreen(navController = navController)
        }

        composable(
            route = Route.AiCoverLetter.path,
            arguments = listOf(
                navArgument(Route.AiCoverLetter.ARG_JOB_ID) { type = NavType.StringType },
                navArgument(Route.AiCoverLetter.ARG_JOB_TITLE) { type = NavType.StringType },
                navArgument(Route.AiCoverLetter.ARG_JOB_DESCRIPTION) { type = NavType.StringType },
                navArgument(Route.AiCoverLetter.ARG_JOB_COMPANY) { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            AiCoverLetterScreen(
                navController = navController,
                jobId = backStackEntry.arguments?.getString(Route.AiCoverLetter.ARG_JOB_ID) ?: "",
                jobTitle = backStackEntry.arguments?.getString(Route.AiCoverLetter.ARG_JOB_TITLE) ?: "",
                jobDescription = backStackEntry.arguments?.getString(Route.AiCoverLetter.ARG_JOB_DESCRIPTION) ?: "",
                jobCompany = backStackEntry.arguments?.getString(Route.AiCoverLetter.ARG_JOB_COMPANY) ?: ""
            )
        }

        composable(
            route = Route.AiProPhoto.path,
            arguments = listOf(
                navArgument(Route.AiProPhoto.ARG_JOB_ID) { type = NavType.StringType },
                navArgument(Route.AiProPhoto.ARG_JOB_TITLE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AiProPhotoScreen(
                navController = navController,
                jobId = backStackEntry.arguments?.getString(Route.AiProPhoto.ARG_JOB_ID) ?: "",
                jobTitle = backStackEntry.arguments?.getString(Route.AiProPhoto.ARG_JOB_TITLE) ?: ""
            )
        }
    }
}
