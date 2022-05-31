package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class ReminderListFragmentTest {

    //    TODO: test the navigation of the fragments.
    //    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.


    @Test
    fun clickAddReminderFab_navigateToSaverReminder() {

        //GIVEN - ReminderListFragment is launched with empty list
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

//        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

//        scenario.onFragment {
//            Navigation.setViewNavController(it.view!!,navController)
//        }

        //WHEN - Add Reminder Fab is clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - verify we navigate to SaveReminderFragment
//        verify(navController).navigate(
//            ReminderListFragmentDirections.toSaveReminder()
//        )

    }
}