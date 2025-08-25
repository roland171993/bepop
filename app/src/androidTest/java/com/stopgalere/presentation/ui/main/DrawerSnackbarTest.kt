package com.stopgalere.presentation.ui.main

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.stopgalere.presentation.ui.main.components.DrawerContent
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test

class DrawerSnackbarTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun clickingCv_showsSnackbar() {
        val expected = "Bientôt disponible"

        rule.setContent {
            val host = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            Box(Modifier.fillMaxSize()) {
                DrawerContent(
                    width = androidx.compose.ui.unit.Dp(150f),
                    onItemSelected = { route ->
                        if (route == "cv") {
                            // same behavior as MainScreen
                            // we don't navigate; just show the snackbar
                            // use launch effect via coroutine in tests:
                            // compose coroutine context is fine here
                            // SnackbarHostState.showSnackbar is suspend
                            scope.launch {
                                host.showSnackbar(
                                    message = expected,
                                    actionLabel = null,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                )

                // host for assertions
                SnackbarHost(
                    hostState = host,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Click the drawer item with tag "DrawerItem_cv" (provided by your Drawer)
        rule.onNodeWithTag("DrawerItem_cv").performClick()

        // Assert snackbar text becomes visible
        rule.onNodeWithText(expected).assertExists()
    }
}
