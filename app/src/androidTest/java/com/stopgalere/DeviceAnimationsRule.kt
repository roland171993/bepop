package com.stopgalere

import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Disables system animations during tests to avoid "main thread is busy/locked".
 * Re-enables them when tests finish.
 *
 * Based on Espresso setup guidance to turn off system animations.
 */
class DeviceAnimationsRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val automation = InstrumentationRegistry.getInstrumentation().uiAutomation

                fun runShell(cmd: String) {
                    val pfd: ParcelFileDescriptor =
                        automation.executeShellCommand(cmd)
                    ParcelFileDescriptor.AutoCloseInputStream(pfd).use { `is` ->
                        BufferedReader(InputStreamReader(`is`)).readText() // drain
                    }
                }

                try {
                    // Disable animations
                    runShell("settings put global window_animation_scale 0")
                    runShell("settings put global transition_animation_scale 0")
                    runShell("settings put global animator_duration_scale 0")
                    base.evaluate()
                } finally {
                    // Restore defaults
                    runShell("settings put global window_animation_scale 1")
                    runShell("settings put global transition_animation_scale 1")
                    runShell("settings put global animator_duration_scale 1")
                }
            }
        }
    }
}
