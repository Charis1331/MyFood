package com.example.foodvenueapp.ui

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.foodvenueapp.R
import com.example.foodvenueapp.domain.model.UNKNOWN_ERROR_CODE
import com.example.foodvenueapp.ui.home.MainActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Test
    fun onBottomPanelDraw_firstItemIsShown_secondItemHasOnlyItsNameShown() {
        testApp().apply {
            returnError = false
            returnNetworkError = false
        }

        ActivityScenario.launch(MainActivity::class.java)

        onView(isRoot()).perform(waitId(R.id.listItemRoot))

        onView(withText("name0")).check(matches(isDisplayed()))
        onView(withText("address0")).check(matches(isDisplayed()))
        onView(withText("category0")).check(matches(isDisplayed()))

        onView(withText("name1")).check(matches(isDisplayed()))
        onView(withText("address1")).check(doesNotExist())
        onView(withText("category1")).check(doesNotExist())
    }

    @Test
    fun onGenericError_toastHasProperMessage_and_noElementsAreShown() {
        testApp().run {
            returnError = true
            returnNetworkError = false
        }

        lateinit var decorView: View
        ActivityScenario
            .launch(MainActivity::class.java)
            .onActivity { activity: MainActivity ->
                decorView = activity.window.decorView
            }

        sleep(500)

        val expectedToastText =
            getApplicationContext<Context>().getString(R.string.generic_error, UNKNOWN_ERROR_CODE)
        onView(withText(expectedToastText))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        onView(withText("name0")).check(doesNotExist())
        onView(withText("address0")).check(doesNotExist())
        onView(withText("category0")).check(doesNotExist())

        onView(withText("name1")).check(doesNotExist())
        onView(withText("address1")).check(doesNotExist())
        onView(withText("category1")).check(doesNotExist())
    }

    @Test
    fun onNetworkError_toastHasProperMessage_and_noElementsAreShown() {
        testApp().run {
            returnError = false
            returnNetworkError = true
        }

        lateinit var decorView: View
        ActivityScenario
            .launch(MainActivity::class.java)
            .onActivity { activity: MainActivity ->
                decorView = activity.window.decorView
            }

        val expectedToastText =
            getApplicationContext<Context>().getString(R.string.no_network_connection)
        onView(withText(expectedToastText))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()))

        onView(withText("name0")).check(doesNotExist())
        onView(withText("address0")).check(doesNotExist())
        onView(withText("category0")).check(doesNotExist())

        onView(withText("name1")).check(doesNotExist())
        onView(withText("address1")).check(doesNotExist())
        onView(withText("category1")).check(doesNotExist())
    }

    @Test
    fun onItemClick_overlayIsDisplayed() {
        testApp().run {
            returnError = false
            returnNetworkError = false
        }
        ActivityScenario.launch(MainActivity::class.java)
        onView(isRoot()).perform(waitId(R.id.listItemRoot))

        onView(withText("name0")).perform(click())

        onView(withId(R.id.venueDetailsLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.venueDistance)).check(matches(isDisplayed()))
        onView(withId(R.id.distanceUnit)).check(matches(isDisplayed()))
    }

    @Test
    fun onBackPress_overlayIsDisplayed() {
        testApp().run {
            returnError = false
            returnNetworkError = false
        }
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(isRoot()).perform(waitId(R.id.listItemRoot))

        onView(withText("name0")).perform(click())

        onView(allOf(withText("name0"), isDescendantOfA(withId(R.id.recycler_view))))
            .check(matches(not(isDisplayed())))

        scenario.onActivity {
            it.onBackPressed()
        }

        sleep(100)

        onView(withId(R.id.venueDetailsLayout)).check(matches(not(isDisplayed())))
        onView(allOf(withText("name0"), isDescendantOfA(withId(R.id.recycler_view))))
            .check(matches(isDisplayed()))
    }

    private fun testApp(): TestApp = getApplicationContext<Context>() as TestApp

}