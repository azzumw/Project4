package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database : RemindersDatabase
    private lateinit var localRepository: RemindersLocalRepository

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        localRepository = RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)

    }

    @After
    fun closeDb () = database.close()

    @Test
    fun saveAndGetReminder_Success() = runBlockingTest{
        //GIVEN a new reminder saved in the database
        val reminder = ReminderDTO("Reminder1","","Hackney",51.0,54.0,"idh1")
        localRepository.saveReminder(reminder)

        //WHEN - a reminder is retreived by ID
        val result = localRepository.getReminder(reminder.id)

        // THEN - Same reminder is returned.
        assertThat(result.succeeded,`is`(true))
        result as Result.Success
        assertThat(result.data.title,`is`("Reminder1"))
    }

    @Test
    fun getReminderById_Error()= runBlockingTest{
        //Error message to be expected
        val error = "Reminder not found!"

        //GIVEN: a reminder does not exist

        //WHEN - the reminder is retrieved
        val retrievedReminder = localRepository.getReminder("id1")

        //THEN - reminder is not found and appropriate error is shown
        retrievedReminder as Result.Error
        assertEquals(retrievedReminder.message,error)
    }

    @Test
    fun deleteAllReminders() = runBlockingTest{
        //GIVEN - a list of reminders is saved in the database
        val reminder1 = ReminderDTO("Reminder1", "Desc", "Hackney", 51.0, 54.0, "idh1")
        val reminder2 = ReminderDTO("Reminder2", "Desc 2", "Wood Green", 56.0, 84.0, "idh2")

        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)

        val savedReminders = localRepository.getReminders() as Result.Success

        //assert that the reminders were successfully saved
        assertThat(savedReminders.data , hasItems(reminder1,reminder2))

        //WHEN - all reminders are deleted
        localRepository.deleteAllReminders()

        //THEN - no reminders exist in the database.
        val result = localRepository.getReminders() as Result.Success
        assertThat(result.data , `is`(emptyList()))

    }

    @Test
    fun deleteAReminderById() = runBlockingTest{
        //GIVEN - a reminder exists in the database
        val reminder = ReminderDTO("Reminder1","","Hackney",51.0,54.0,"idh1")
        localRepository.saveReminder(reminder)

        //assert reminder was saved
        val savedReminder = localRepository.getReminder(reminder.id) as Result.Success
        assertThat(savedReminder.data, `is`(reminder))

        //WHEN - the reminder is deleted
        localRepository.deleteReminder(reminder.id)

        //THEN - assert that the same reminder does not exist in the database
        val error = "Reminder not found!"
        val result = localRepository.getReminder(reminder.id) as Result.Error
        assertThat(result.message, `is`(error))

    }
}