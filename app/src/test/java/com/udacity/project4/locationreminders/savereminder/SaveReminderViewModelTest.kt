package com.udacity.project4.locationreminders.savereminder


import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var appContext: Application

    //subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    //For livedata
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {

        appContext = getApplicationContext()
        fakeDataSource = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(appContext, fakeDataSource)

    }

    @Test
    fun saveReminder() = runBlockingTest {

        //Given - a reminder is created
        val reminder1 = ReminderDTO("reminder1", "", "Home", 51.0, -51.0, "home")

        //When - saveReminder is called
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                reminder1.title,
                reminder1.description,
                reminder1.location,
                reminder1.latitude,
                reminder1.longitude,
                reminder1.id
            )
        )

        val returnedReminder = saveReminderViewModel.dataSource.getReminder("home")

        //THEN - the same Reminder instance is returned
        returnedReminder as Result.Success
        assertThat(returnedReminder.data.id, `is`("home"))

        //observe the livedata values
        saveReminderViewModel.showToast.getOrAwaitValue()
        saveReminderViewModel.showLoading.getOrAwaitValue()
        saveReminderViewModel.navigationCommand.getOrAwaitValue()

        //toast values match
        val expectedToastValue = appContext.getString(R.string.reminder_saved)
        val actualToastValue = saveReminderViewModel.showToast.value
        assertThat(actualToastValue, `is`(expectedToastValue))

        //showLoading value matches
        assertThat(saveReminderViewModel.showLoading.value, `is`(false))

        //navigation Command matches
        assertThat(
            saveReminderViewModel.navigationCommand.value as NavigationCommand.Back,
            `is`(NavigationCommand.Back)
        )
//        assertEquals(saveReminderViewModel.navigationCommand.value,NavigationCommand.Back)
    }

    @Test
    fun validateAndSaveReminder_validData_returnTrue() {
        //GIVEN - a reminder is created
        val reminder1 = ReminderDataItem("reminder1", "", "Home", 51.0, -51.0, "home")

        //WHEN - validateAndSaveReminder is called
        val result = saveReminderViewModel.validateAndSaveReminder(reminder1)

        //THEN - it returns true
        assertThat(result, `is`(true))
    }

    @Test
    fun validateAndSaveReminder_titleNull_returnFalse() {
        //GIVEN - a reminder is created with title null
        val reminder1 = ReminderDataItem(null, "", "Home", 51.0, -51.0, "home")

        //WHEN - validateAndSaveReminder is called
        val result = saveReminderViewModel.validateAndSaveReminder(reminder1)

        //THEN - it returns false
        assertThat(result, `is`(false))
    }

    @Test
    fun validateAndSaveReminder_titleEmpty_returnFalse() {
        //GIVEN - a reminder is created with title empty
        val reminder1 = ReminderDataItem("", "", "Home", 51.0, -51.0, "home")

        //WHEN - validateAndSaveReminder is called
        val result = saveReminderViewModel.validateAndSaveReminder(reminder1)

        //THEN - it returns false
        assertThat(result, `is`(false))
    }

    @Test
    fun validateAndSaveReminder_locationNull_returnFalse() {
        //GIVEN - a reminder is created with location null
        val reminder1 = ReminderDataItem("Title", "", null, 51.0, -51.0, "home")

        //WHEN - validateAndSaveReminder is called
        val result = saveReminderViewModel.validateAndSaveReminder(reminder1)

        //THEN - it returns false
        assertThat(result, `is`(false))
    }

    @Test
    fun validateAndSaveReminder_locationEmpty_returnFalse() {
        //GIVEN - a reminder is created with location empty
        val reminder1 = ReminderDataItem(null, "", "", 51.0, -51.0, "home")

        //WHEN - validateAndSaveReminder is called
        val result = saveReminderViewModel.validateAndSaveReminder(reminder1)

        //THEN - it returns false
        assertThat(result, `is`(false))
    }

    @Test
    fun validateEnteredData_validData_returnTrue() {

        //GIVEN - a valid data
        val reminderDataItem = ReminderDataItem("reminder1", "", "Home", 51.0, -51.0, "home")

        //When entering a valid new data
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //Then the data is validated and returns true
        assertThat(result, `is`(true))

        //snackbar is not shown, value is null
        val snackBarValue = saveReminderViewModel.showSnackBarInt.value
        assertThat(snackBarValue, nullValue())
    }

    @Test
    fun validateEnteredData_titleNull_returnFalse() {

        //GIVEN - an invalid data
        val reminderDataItem = ReminderDataItem(null, "", "Home", 51.0, -51.0, "home")

        //WHEN -  validating invalid  data
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //THEN - the data is invalidated and returns false
        assertThat(result, `is`(false))

        //snackbar is shown
        // value is updated with appropriate error message (err_enter_title)
        saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))

        //Snackbar value is not location error
        assertThat(saveReminderViewModel.showSnackBarInt.value, not(R.string.err_select_location))
    }

    @Test
    fun validateEnteredData_titleEmpty_returnFalse() {

        //GIVEN - an invalid data
        val reminderDataItem = ReminderDataItem("", "", "Home", 51.0, -51.0, "home")

        //WHEN - entering invalid new data
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //THEN - the data is invalidated and returns false
        assertThat(result, `is`(false))

        //snackbar is shown
        // value is updated with appropriate error message (err_enter_title)
        saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))

        //Snackbar value is not location error
        assertThat(saveReminderViewModel.showSnackBarInt.value, not(R.string.err_select_location))
    }

    @Test
    fun validateEnteredData_locationNull_returnFalse() {

        //GIVEN - an invalid data
        val reminderDataItem = ReminderDataItem("Title 1", "", null, 51.0, -51.0, "home")

        //WHEN - entering invalid new data
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //THEN - the data is invalidated and returns false
        assertThat(result, `is`(false))

        //snackbar is shown
        // value is updated with appropriate error message (err_select_location)
        saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))

        //Snackbar value is not title error
        assertThat(saveReminderViewModel.showSnackBarInt.value, not(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_locationEmpty_returnFalse() {

        //GIVEN - an invalid data
        val reminderDataItem = ReminderDataItem("Title 1", "", "", 51.0, -51.0, "home")

        //WHEN - entering invalid new data
        val result = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //THEN - the data is invalidated and returns false
        assertThat(result, `is`(false))

        //snackbar is shown
        // value is updated with appropriate error message (err_select_location)
        saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))

        //Snackbar value is not title error
        assertThat(saveReminderViewModel.showSnackBarInt.value, not(R.string.err_enter_title))
    }

    @Test
    fun onClear_liveDataValues_setToDefault() {

        //GIVEN - a reminder/POI is created
        val reminder1 = ReminderDTO("reminder1", "description", "London", 51.0, -51.0, "home")
        val poi = PointOfInterest(LatLng(51.0, 51.0), reminder1.id, reminder1.location)

        //Live data values are set
        saveReminderViewModel.isPoiSelected.value = true
        saveReminderViewModel.selectedPOI.value = poi
        saveReminderViewModel.reminderSelectedLocationStr.value = poi.name
        saveReminderViewModel.reminderTitle.value = reminder1.title
        saveReminderViewModel.reminderDescription.value = reminder1.description
        saveReminderViewModel.longitude.value = poi.latLng.longitude
        saveReminderViewModel.latitude.value = poi.latLng.latitude

        //observe live data
        saveReminderViewModel.reminderTitle.getOrAwaitValue()
        saveReminderViewModel.reminderDescription.getOrAwaitValue()
        saveReminderViewModel.isPoiSelected.getOrAwaitValue()
        saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue()
        saveReminderViewModel.selectedPOI.getOrAwaitValue()
        saveReminderViewModel.latitude.getOrAwaitValue()
        saveReminderViewModel.longitude.getOrAwaitValue()


        //WHEN - viewModel is cleared
        saveReminderViewModel.onClear()

        //THEN - Live Data values are reset to defaults as per onClear()
        assertThat(saveReminderViewModel.isPoiSelected.value, `is`(false))
        assertThat(saveReminderViewModel.selectedPOI.value, nullValue())
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, nullValue())
        assertThat(saveReminderViewModel.reminderTitle.value, nullValue())
        assertThat(saveReminderViewModel.reminderDescription.value, nullValue())
        assertThat(saveReminderViewModel.latitude.value, nullValue())
        assertThat(saveReminderViewModel.longitude.value, nullValue())
    }

    @Test
    fun check_loading() {
        //Given - a reminder is created
        val reminder1 = ReminderDTO("reminder1", "", "Home", 51.0, -51.0, "home")

        mainCoroutineRule.pauseDispatcher()
        //When - saveReminder is called
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                reminder1.title,
                reminder1.description,
                reminder1.location,
                reminder1.latitude,
                reminder1.longitude,
                reminder1.id
            )
        )

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}