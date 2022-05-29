package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    //subject under test
    private lateinit var saveReminderViewModel : SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        fakeDataSource = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(getApplicationContext(),fakeDataSource)

    }
    //TODO: provide testing to the SaveReminderView and its live data objects

    @Test
    fun saveReminder(){

        //Given
        val reminder1 = ReminderDTO("reminder1","","Home",51.0,-51.0,"home")
        val reminder2 = ReminderDTO("reminder2","","Home",51.0,-51.0,"kafe")
        val reminder3 = ReminderDTO("reminder3","","Home",51.0,-51.0,"nando")


        //When


        runBlocking {
            saveReminderViewModel.saveReminder(ReminderDataItem(reminder1.title,
            reminder1.description,
            reminder1.location,
            reminder1.latitude,
            reminder1.longitude,
            reminder1.id
        )) }



        val returnedRemindertitle = runBlocking { saveReminderViewModel.dataSource.getReminder("home") }

        Log.e("SaveReminderviewModelTest",returnedRemindertitle.toString())
    }

    @Test
    fun validateEnteredData_validData_returnTrue(){



        //Given a fresh ViewModel
//        val viewModel = SaveReminderViewModel(appContext,)
        //When entering a valid new data

        //Then the data is validated and returns true

    }

    @Test
    fun validateEnteredData_invalidData_returnFalse(){

    }


}