package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
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
    fun clickAddReminderFab_navigateToSaveReminder() {

        //GIVEN - ReminderListFragment is launched with empty list
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!,navController)
        }

        //WHEN - Add Reminder Fab is clicked
        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed())).perform(click())

        //THEN - verify we navigate to SaveReminderFragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun checkReminderIsDisplayed() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val dataSource = FakeDataSource()
        val saveReminderViewModel = SaveReminderViewModel(context,dataSource)

        val reminder =ReminderDataItem("Tesco","","East Road",51.0,51.0,"id1")
        saveReminderViewModel.saveReminder(reminder)

//        val scenario = launchFragmentInContainer<ReminderListFragment>()

    }
}