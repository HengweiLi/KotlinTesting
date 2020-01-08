package com.example.kotlintesting

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(
        MainActivity::class.java
    )
    private var mIdlingResource: IdlingResource? = null
    @Before
    fun registerIdlingResource() {
        mIdlingResource = activityTestRule.activity.getIdlingResource()
        Espresso.registerIdlingResources(mIdlingResource)
    }

    @Test
    fun testRecycleView() {
        Espresso.onView(ViewMatchers.withId(R.id.forecast_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.forecast_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(4, ViewActions.click()))
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.forecast_list))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(20))
        Espresso.onView(ViewMatchers.withId(R.id.forecast_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(21, ViewActions.click())
        )
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click())
    }

    @After
    fun unregisterIdlingResource() {
        if (mIdlingResource != null) Espresso.unregisterIdlingResources(mIdlingResource)
    }
}