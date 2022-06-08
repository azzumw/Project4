package com.udacity.project4

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.ReminderDescriptionActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.os.SystemClock
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.rules.ActivityScenarioRule


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@LargeTest
class AppNavigation {

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your idling resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun remindersList_clickOnFab_opensSaveReminderScreen() {
        //GIVEN - Reminder activity is launched
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //When FAB is clicked
         viewWithId(R.id.addReminderFAB).click()

        //THEN - SaveReminder Screen is shown
        viewWithId(R.id.reminderTitle).check(matches(isDisplayed()))
    }

    @Test
    fun remindersList_clickOnFab_opensSaveReminderScreen_opensSelectLocation(){
        //GIVEN - Reminder activity is launched
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //When FAB is clicked
        viewWithId(R.id.addReminderFAB).click()

        //THEN - SaveReminder Screen is shown
        viewWithId(R.id.reminderTitle).check(matches(isDisplayed()))

        //WHEN - Select Location is clicked
        viewWithId(R.id.selectLocation).click()

        //THEN - SelectLocation Screen is shown
        viewWithId(R.id.map).check(matches(isDisplayed()))
    }

    @Test
    fun remindersList_to_SaveReminders_pressBack_to_remindersList(){
        //GIVEN - Reminder activity is launched
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //When FAB is clicked
        viewWithId(R.id.addReminderFAB).click()

        //THEN - SaveReminder Screen is shown
        viewWithId(R.id.reminderTitle).check(matches(isDisplayed()))

        //WHEN - back button is pressed
        pressBack()

        //THEN - reminders List screen is shown
        viewWithId(R.id.noDataTextView).check(matches(isDisplayed()))
    }

    @Test
    fun remindersList_to_saveReminder_to_selectLocation_double_backpress_remindersList() {
        //GIVEN - Reminder activity is launched
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //When FAB is clicked
        viewWithId(R.id.addReminderFAB).click()

        //THEN - SaveReminder Screen is shown
        viewWithId(R.id.reminderTitle).check(matches(isDisplayed()))

        //WHEN - Select Location is clicked
        viewWithId(R.id.selectLocation).click()

        //use uiautomator watcher

        //THEN - SelectLocation Screen is shown
        viewWithId(R.id.map).check(matches(isDisplayed()))

        //WHEN - back is pressed
        Espresso.pressBack()

        //THEN - Save reminder screen is shown
        viewWithId(R.id.reminderTitle).check(matches(isDisplayed()))

        //WHEN - back is pressed
        pressBack()

        //THEN - reminders List screen is shown
        viewWithId(R.id.noDataTextView).check(matches(isDisplayed()))
    }


    @Test
    fun reminderDescription_clickingFab_returnsToRemindersList() {
        val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"
        val dataItem =  ReminderDataItem("Title 1","Desc","London",51.0,51.0,"id1")

        val intent = Intent().apply {
            this.putExtra(EXTRA_ReminderDataItem,dataItem)
        }

        val activityScenario = ActivityScenario.launch(ReminderDescriptionActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        viewWithId(R.id.enteredLocation_tv).check(matches(isDisplayed()))

        viewWithId(R.id.okay_btn).click()

        viewWithId(R.id.noDataTextView).check(matches(isDisplayed()))
    }
}