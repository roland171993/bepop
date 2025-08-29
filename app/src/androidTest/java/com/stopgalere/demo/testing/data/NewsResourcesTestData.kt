
@file:Suppress("ktlint:standard:max-line-length")

package com.stopgalere.demo.testing.data

import com.stopgalere.demo.model.data.NewsResource
import kotlinx.datetime.Instant

val newsResourcesTestData: List<NewsResource> = listOf(
    NewsResource(
        id = "1",
        title = "Android Basics with Compose",
        content = "We released the first two units of Android Basics with Compose, our first free course that teaches Android Development with Jetpack Compose to anyone; you do not need any prior programming experience other than basic computer literacy to get started. You’ll learn the fundamentals of programming in Kotlin while building Android apps using Jetpack Compose, Android’s modern toolkit that simplifies and accelerates native UI development. These two units are just the beginning; more will be coming soon. Check out Android Basics with Compose to get started on your Android development journey",
        url = "https://android-developers.googleblog.com/2022/05/new-android-basics-with-compose-course.html",
        headerImageUrl = "https://developer.android.com/images/hero-assets/android-basics-compose.svg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Codelab",
        topics = listOf(topicsTestData[1]),
    ),
    NewsResource(
        id = "2",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(topicsTestData[0], topicsTestData[1]),
    ),
    NewsResource(
        id = "3",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed " +
            "with Paging. Transformations like inserting separators, when to " +
            "create a new pager, and customisation options for consuming " +
            "PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(topicsTestData[2]),
    ),
    NewsResource(
        id = "4",
        title = "New Jetpack Release",
        content = "New Jetpack release includes updates to libraries such as CameraX, Benchmark, and" +
            "more!",
        url = "https://developer.android.com/jetpack/androidx/versions/all-channel",
        headerImageUrl = "",
        publishDate = Instant.parse("2022-10-01T00:00:00.000Z"),
        type = "",
        topics = listOf(topicsTestData[2]),
    ),
)
