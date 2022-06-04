package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import java.lang.Error

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_and_getReminderById() = runBlockingTest {

        //Given - a reminder is inserted
        val reminder = ReminderDTO("Reminder1", "", "Hackney", 51.0, 54.0, "idh1")
        database.reminderDao().saveReminder(reminder)

        //WHEN  - load the reminder from the database using its id
        val loadedReminder = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(loadedReminder as ReminderDTO, notNullValue())
        assertThat(loadedReminder.id, `is`(reminder.id))
        assertThat(loadedReminder.location, `is`(reminder.location))
        assertThat(loadedReminder.title, `is`(reminder.title))

    }


    @Test
    fun deleteReminder() = runBlockingTest {
        //Given - a reminder is inserted
        val reminder = ReminderDTO("Reminder1", "", "Hackney", 51.0, 54.0, "idh1")
        database.reminderDao().saveReminder(reminder)

        //WHEN  - the reminder with the ID is deleted from database
        database.reminderDao().deleteReminder(reminder.id)

        //THEN - no reminder exists in the Database with that ID
        val retrievedReminder = database.reminderDao().getReminderById(reminderId = reminder.id)
        assertThat(retrievedReminder, nullValue())

    }

    @Test
    fun deleteAllReminders() = runBlockingTest{
        //GIVEN - a list of reminders is saved in the database
        val reminder1 = ReminderDTO("Reminder1", "Desc", "Hackney", 51.0, 54.0, "idh1")
        val reminder2 = ReminderDTO("Reminder2", "Desc 2", "Wood Green", 56.0, 84.0, "idh2")

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        //WHEN - all reminders in the database are deleted
        database.reminderDao().deleteAllReminders()

        //THEN - no reminders exist in the database
        val retrievedReminders = database.reminderDao().getReminders()
        assertThat(retrievedReminders.size, `is`(0) )
        assertThat(retrievedReminders, `is`(emptyList()) )
    }

    @Test
    fun getReminders() = runBlockingTest{
        //GIVEN - a list of reminders is saved in the database
        val reminder1 = ReminderDTO("Reminder1", "Desc", "Hackney", 51.0, 54.0, "idh1")
        val reminder2 = ReminderDTO("Reminder2", "Desc 2", "Wood Green", 56.0, 84.0, "idh2")

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        //WHEN - the list of reminders are fetched
        val listOfReminders = database.reminderDao().getReminders()

        //THEN - The list of reminders...
        assertThat(listOfReminders.size, `is`(2))
        assertThat(listOfReminders[0].id, `is`("idh1"))
        assertThat(listOfReminders[1].id, `is`("idh2"))
        assertThat(listOfReminders,hasItems(reminder1,reminder2))
        //How do I assert this is a list of ReminderDTO?
    }
}