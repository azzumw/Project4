package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
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
    fun saveReminder_retrievesReminder() = runBlocking{
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
    fun deleteReminder_getById_returnErrorMessageOnNull()= runBlocking{
        //Error message to be expected
        val error = "Reminder not found!"
        //GIVEN: a reminder is inserted
        val reminder = ReminderDTO("Reminder1","","Hackney",51.0,54.0,"idh1")
        localRepository.saveReminder(reminder)

        //WHEN - the reminder is deleted, and retrieved
        localRepository.deleteReminder(reminder.id)
        val retrievedReminder = localRepository.getReminder(reminder.id)

        //THEN - reminder is not found and appropriate error is shown
        retrievedReminder as Result.Error
        assertEquals(retrievedReminder.message,error)
    }
}