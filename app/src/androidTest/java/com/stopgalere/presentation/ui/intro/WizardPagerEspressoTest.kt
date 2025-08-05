package com.stopgalere.presentation.ui.intro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import com.stopgalere.MainActivity
import com.RolandAssoh.stopgalere.ci.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WizardPagerEspressoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
        // TODO: Navigate past splash to intro
    }

    @Test
    fun nextButton_isDisplayed_andClickable() {
        // Wait until the “Suivant” button is on screen…
        onView(withText(R.string.screen_wizard_next))
            .check(matches(isDisplayed()))
            .perform(click())

        // Optionally verify the text flips to “Finish” on last page:
        onView(withText(R.string.screen_wizard_finish))
            .check(matches(isDisplayed()))
    }
}