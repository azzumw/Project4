package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModelTest
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    RemindersListViewModelTest::class,
    SaveReminderViewModelTest::class
)
class LocalSuite {
}