package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource() : ReminderDataSource {

    var remindersServiceData : LinkedHashMap<String,ReminderDTO> = LinkedHashMap()


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return Result.Success(remindersServiceData.getValue(id))
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }

    override suspend fun deleteReminder(id: String) {
        remindersServiceData.remove(id)
    }

    fun addReminders(vararg reminders:ReminderDTO){
        for (reminder in reminders){
            runBlocking { saveReminder(reminder) }
        }
    }
}