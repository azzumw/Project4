package com.udacity.project4

import com.udacity.project4.locationreminders.data.local.RemindersDaoTest
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepositoryTest
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    RemindersActivityTest::class,
    AppNavigation::class,
    RemindersDaoTest::class,
    RemindersLocalRepositoryTest::class,
    ReminderListFragmentTest::class
    )
class AndroidTestsSuite {
}