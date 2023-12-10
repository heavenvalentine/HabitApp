package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        if (shouldNotify) {
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            createNotificationChannel(notificationManager)

            val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val hbuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setContentIntent(getPendingIntent(habitId))
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(habitTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(applicationContext.getString(R.string.notify_content))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(
                    ContextCompat.getColor(
                        applicationContext,
                        android.R.color.holo_red_dark
                    )
                )
                .setSound(ringtone)

            notificationManager.notify(1, hbuilder.build())
        }
        return Result.success()
    }

    private fun getPendingIntent(habit: Int): PendingIntent? {
        val intent = Intent(applicationContext, DetailHabitActivity::class.java)

        intent.putExtra(HABIT_ID, habit)
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = applicationContext.getString(R.string.notify_channel_name)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH,
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }
}
