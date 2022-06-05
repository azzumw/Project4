package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@SmallTest
class RemindersListViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    //For livedata
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(appContext, fakeDataSource)

    }

    @Test
    fun loadReminders_resultError() = runBlockingTest {
        //GIVEN - no data exist
        //make the repository return error
        fakeDataSource.setShouldReturnError(true)

        //WHEN - data is loaded
        remindersListViewModel.loadReminders()

        //observe Livedata
        remindersListViewModel.showSnackBar.getOrAwaitValue()
        remindersListViewModel.showLoading.getOrAwaitValue()

        //THEN - liveData values are as expected and appropriate error message is returned
        assertThat(remindersListViewModel.showSnackBar.value, `is`("Reminders not found!"))
        assertThat(remindersListViewModel.showLoading.value, `is`(false))
    }

    @Test
    fun loadReminders_resultSuccess() {

        //GIVEN - a list of reminders exist in the repository
        val reminder1 = ReminderDTO("Reminder 1", "desc", "London", 34.0, 34.0, "ID_1")
        val reminder2 = ReminderDTO("Reminder 2", "desc", "Darlington", 65.0, 14.0, "ID_2")
        val reminder3 = ReminderDTO("Reminder 3", "desc", "Telford", 54.0, 74.0, "ID_3")

        //Make repository return list
        fakeDataSource.setShouldReturnError(false)

        fakeDataSource.addReminders(reminder1, reminder2, reminder3)

        //WHEN - reminders are loaded
        remindersListViewModel.loadReminders()

        //observe this viewModel livedata
        remindersListViewModel.remindersList.getOrAwaitValue()
        remindersListViewModel.showLoading.getOrAwaitValue()

        //THEN - a list of data is returned with the correct size
        val valueList = remindersListViewModel.remindersList.value
        assertThat(valueList, hasSize(3))

        //showloading is false
        assertThat(remindersListViewModel.showLoading.value, `is`(false))
    }

    @Test
    fun check_loading() {
        //GIVEN - erm... lol
        mainCoroutineRule.pauseDispatcher()

        //WHEN - reminders are loaded
        remindersListViewModel.loadReminders()

        //THEN - assert that the loading indicator is shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        //THEN - assert that the loading indicator is hidden
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }

    @After
    fun tearDown() {
        stopKoin()
    }
}