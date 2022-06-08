package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var geofencingClient: GeofencingClient

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )

        geofencingClient = LocationServices.getGeofencingClient(this)

        val data = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem?
        binding.reminderDataItem = data

        removeThisGeofence(data!!.id)

        binding.okayBtn.setOnClickListener {
           val intent = Intent(this,RemindersActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
            startActivity(intent)
        }
    }

    private fun removeThisGeofence(id:String){
        val list = mutableListOf(id)
        geofencingClient?.removeGeofences(list)?.run {
            addOnSuccessListener {
                // Geofences removed
                Log.e(this.javaClass.canonicalName,"Geofence with $id has been removed")
            }
            addOnFailureListener {
                // Failed to remove geofences
                Log.e(this.javaClass.canonicalName,"Geofence with $id has not been removed")
            }
        }
    }

    override fun onBackPressed() {
       finish()
    }
}
