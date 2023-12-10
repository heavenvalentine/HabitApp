package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class CountDownActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null) {
            val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]
            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished
            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.
            val tvCountDownTitle = findViewById<TextView>(R.id.tv_count_down_title)
            val tvCountDown = findViewById<TextView>(R.id.tv_count_down)
            val btnStart = findViewById<Button>(R.id.btn_start)
            val btnStop = findViewById<Button>(R.id.btn_stop)

            tvCountDownTitle.text = habit.title
            viewModel.setInitialTime(habit.minutesFocus)

            viewModel.currentTimeString.observe(this) { currentTime ->
                tvCountDown.text = currentTime
            }

            val mWorkManager = WorkManager.getInstance(this)
            val data = Data.Builder()
                .putInt(HABIT_ID, habit.id)
                .putString(NOTIFICATION_CHANNEL_ID, getString(R.string.notify_channel_name))
                .putString(HABIT_TITLE, habit.title)
                .build()

            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                .setInputData(data)
                .addTag(NOTIF_UNIQUE_WORK)
                .build()

            viewModel.eventCountDownFinish.observe(this) { event ->
                if (event) {
                    mWorkManager.enqueueUniqueWork(
                        NOTIF_UNIQUE_WORK, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest
                    )
                    updateButtonState(false)
                } else {
                    mWorkManager.cancelUniqueWork(NOTIF_UNIQUE_WORK)
                }
            }

            btnStart.setOnClickListener {
                viewModel.startTimer()
                updateButtonState(true)
            }

            btnStop.setOnClickListener {
                viewModel.resetTimer()
                updateButtonState(false)
            }
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}