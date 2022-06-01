package com.udacity.project4


import android.app.Application
import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.not
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test


    @get:Rule
    val rule = ActivityScenarioRule<RemindersActivity>(RemindersActivity::class.java)

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private var decorView: View? = null

    companion object{
        private lateinit var uiDevice: UiDevice

        @BeforeClass
        @JvmStatic
        fun setup(){
            uiDevice = UiDevice.getInstance(getInstrumentation())
        }
    }

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
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


//    TODO: add End to End testing to the app


    @Test
    fun e2e_saveAReminder() {

        val activity   = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.selectLocation)).perform(click())

        SystemClock.sleep(5000)

        onView(withId(R.id.map)).perform(longClick())
        SystemClock.sleep(2000)
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.map)).perform(click())

        onView(withId(R.id.saveBtn)).click()

        onView(withId(R.id.selectedLocation)).check(matches(withText(containsString("Dropped Pin"))))

        viewWithId(R.id.reminderTitle).type(remindertitle)
        viewWithId(R.id.reminderDescription).type(reminderDesc)

        viewWithId(R.id.saveReminder).click()

//        onView(withText("Reminder Saved!")).inRoot(withDecorView(not(decor))).check(matches(isDisplayed()))

        viewWithId(R.id.reminderssRecyclerView).check(matches(hasDescendant(withText(remindertitle))))

        SystemClock.sleep(2000)

        uiDevice.openNotification()

        uiDevice.wait(Until.hasObject(By.text(remindertitle)), 2000)
        uiDevice.findObject(UiSelector().textContains(remindertitle)).clickAndWaitForNewWindow()

        onView(withText(remindertitle)).check(matches(isDisplayed()))

        activity.close()
    }

    /**
     * @Test: title error is displayed with correct error text
     * */

    @Test
    fun e2e_correctTitleErrorDisplayed() {

        val activityScenarioRule = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.selectLocation)).perform(click())

        SystemClock.sleep(5000)

        onView(withId(R.id.map)).perform(longClick())
        SystemClock.sleep(2000)
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.map)).perform(click())

        onView(withId(R.id.saveBtn)).click()

        viewWithId(R.id.reminderDescription).type(reminderDesc)

        viewWithId(R.id.saveReminder).click()

        //check error snackbar with text R.string.err_enter_title
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_enter_title)))

        activityScenarioRule.close()
    }

    /**
     * @Test: location error is displayed with correct error text
     * */

    @Test
    fun e2e_correctLocationErrorDisplayed() {

        //GIVEN: correct a title exists
        val activityScenarioRule = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB)).perform(click())

        viewWithId(R.id.reminderTitle).type(remindertitle)

        //WHEN: save reminder button is clicked
        viewWithId(R.id.saveReminder).click()

        //THEN: snackbar appears with the appropriate error text
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_select_location)))

        activityScenarioRule.close()
    }

    /**
     * @Test: snackbar appears when SaveLocationFragment launches with correct text
     * */

    @Test
    fun e2e_snackBarWithCorrectTextAppears_launch_SaveLocationFragment() {

        val activityScenarioRule = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB)).perform(click())

        onView(withId(R.id.selectLocation)).perform(click())

        uiDevice.wait(Until.hasObject(By.clazz(Snackbar::class.java)),2000L)

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.selection_location_message)))

        activityScenarioRule.close()
    }
}
