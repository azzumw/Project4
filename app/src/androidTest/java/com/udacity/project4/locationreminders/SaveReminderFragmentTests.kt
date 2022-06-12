package com.udacity.project4.locationreminders

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.*
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class SaveReminderFragmentTests {


    @Test
    fun saveReminder_missingTitle_missingLocation_shows_snackbar_correctTitleErrorMessage() {
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        viewWithId(R.id.saveReminder).click()

        viewWithId(com.google.android.material.R.id.snackbar_text)
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.err_enter_title)))
    }

    @Test
    fun saveReminder_missingLocation_shows_snackbar_with_correctErrorMessage(){
        //GIVEN: save reminder fragment is launched
        launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)

        //title field is filled
        viewWithId(R.id.reminderTitle).type(remindertitle)

        //WHEN: save reminder button is clicked
        viewWithId(R.id.saveReminder).click()

        //THEN - snackbar with correct error message is shown
        viewWithId(com.google.android.material.R.id.snackbar_text)
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.err_select_location)))

    }
}