package com.stopgalere.navigation.destinations

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object JobList : Screen("jobList")
    object ResumeList : Screen("resumeList")
    object CoverList : Screen("coverList")
    object About : Screen("about")
}