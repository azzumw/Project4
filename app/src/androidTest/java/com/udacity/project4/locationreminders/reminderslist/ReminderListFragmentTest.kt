package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import android.os.SystemClock
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.getOrAwaitValue
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@MediumTest
@RunWith(AndroidJUnit4::class)
class ReminderListFragmentTest  : KoinTest {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    //    TODO: test the displayed data on the UI.

    //    TODO: add testing for the error messages.

    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    fun init() {
        //stop the original app koin
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }

    }


    @Before
    fun setUp(){
        fakeDataSource = FakeDataSource()


    }
    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun clickAddReminderFab_navigateToSaveReminder() {

        //GIVEN - ReminderListFragment is launched with empty list
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - Add Reminder Fab is clicked
        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed())).perform(click())

        //THEN - verify we navigate to SaveReminderFragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun checkReminderIsDisplayed() = runBlockingTest{
        val context = ApplicationProvider.getApplicationContext<Application>()

        val reminderListViewModel = RemindersListViewModel(context,fakeDataSource)

        val reminder =
            ReminderDTO("Tesco", "", "East Road", 51.0, 51.0, "id1")

        fakeDataSource.saveReminder(reminder)

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)

        dataBindingIdlingResource.monitorFragment(scenario)

        reminderListViewModel.loadReminders()

        reminderListViewModel.remindersList.getOrAwaitValue()

        assertThat(reminderListViewModel.remindersList.value?.get(0)?.location,`is`("East Road"))

        SystemClock.sleep(2000)

    }
}