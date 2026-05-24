package com.stopgalere

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner that replaces the production [MyApplication] with
 * [HiltTestApplication] for Hilt-instrumented tests.
 *
 * Registered in app/build.gradle.kts:
 *   testInstrumentationRunner = "com.stopgalere.HiltTestRunner"
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl:        ClassLoader,
        className: String,
        context:   Context
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
