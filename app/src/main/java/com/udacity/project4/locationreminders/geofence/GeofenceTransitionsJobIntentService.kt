package com.udacity.project4.locationreminders.geofence

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.*
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    val _viewModel: SaveReminderViewModel by inject()

    private lateinit var geofencingClient: GeofencingClient

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {

        private const val JOB_ID = 573
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java,
                JOB_ID,
                intent
            )
        }

        internal object GeofencingConstants {
            const val GEOFENCE_RADIUS_IN_METERS = 100f
            const val ACTION_GEOFENCE_EVENT =
                "ACTION_GEOFENCE_EVENT"
        }
    }


    override fun onHandleWork(intent: Intent) {
        val tag = "JobOnHandle"
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            Log.e("$tag: trig geos", geofencingEvent.triggeringGeofences.size.toString())

            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e("JobIntentKt", errorMessage)
                return
            }

            // Get the transition type.
            val geofenceTransition = geofencingEvent.geofenceTransition


            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.e("JobIntentKt", "Geofence entered")
                for (i in geofencingEvent.triggeringGeofences) {
                    Log.e("JobIntentKt", i.toString())
                }

                sendNotification(geofencingEvent.triggeringGeofences)

            }
        } else {
            Log.e("JObIntentKt", "Error matching intent")
        }
    }

    private fun sendNotification(triggeringGeofences: List<Geofence>) {

        val remindersLocalRepository: ReminderDataSource by inject()

        CoroutineScope(coroutineContext).launch(SupervisorJob()) {

            var i = 0
            while (i < triggeringGeofences.size) {
                val result = remindersLocalRepository.getReminder(triggeringGeofences[i].requestId)

                if (result is Result.Success<ReminderDTO>) {
                    val reminderDTO = result.data
                    //send a notification to the user with the reminder details
                    sendNotification(
                        this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                            reminderDTO.title,
                            reminderDTO.description,
                            reminderDTO.location,
                            reminderDTO.latitude,
                            reminderDTO.longitude,
                            reminderDTO.id
                        )
                    )


                    remindersLocalRepository.deleteReminder(reminderDTO.id)
                    geofencingClient =
                        LocationServices.getGeofencingClient(this@GeofenceTransitionsJobIntentService)

                    removeGeofences(mutableListOf(triggeringGeofences[i].requestId))

                }
            }
        }
    }

    private fun removeGeofences(listOfGeofenceIds: MutableList<String>) {
        geofencingClient?.removeGeofences(listOfGeofenceIds)?.run {
            addOnSuccessListener {
                // Geofences removed
                Log.e(
                    this.javaClass.canonicalName,
                    "Geofence with $listOfGeofenceIds has been removed"
                )
            }
            addOnFailureListener {
                // Failed to remove geofences
                Log.e(
                    this.javaClass.canonicalName,
                    "Geofence with $listOfGeofenceIds has not been removed"
                )
            }
        }
    }
}