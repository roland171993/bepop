package com.stopgalere.presentation.ui.intro

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import org.hamcrest.Matchers.`is`
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WizardPagerEspressoTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
        // If your splash screen auto-navigates to "intro" after some delay,
        // consider IdlingResource or a short Thread.sleep() for testing only:
        Thread.sleep(1500) // replace with proper idling if possible
    }

    @Test
    fun wizard_navigates_to_main_on_finish() {
        // Page 0 -> Page 1
        onView(withText(R.string.screen_wizard_next))
            .check(matches(isDisplayed()))
            .perform(click())

        // Page 1 -> Page 2
        onView(withText(R.string.screen_wizard_next))
            .check(matches(isDisplayed()))
            .perform(click())

        // Page 2 (last) -> Main screen
        onView(withText(R.string.screen_wizard_finish))
            .check(matches(isDisplayed()))
            .perform(click())

        // Verify we reached main — adjust to an element unique to MainScreen
        onView(withTagValue(`is`("MainScreen")))
            .check(matches(isDisplayed()))
    }
}
