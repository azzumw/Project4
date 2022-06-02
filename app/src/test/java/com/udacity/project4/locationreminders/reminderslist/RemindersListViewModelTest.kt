package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel : RemindersListViewModel
    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setup(){
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(appContext,fakeDataSource)

    }
    fun loadReminders_resultError(){
        //make the repository return error
        fakeDataSource.setShouldReturnError(true)

        remindersListViewModel.loadReminders()
    }
}