package com.worldexplorerblog.stravaintervall.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingExecutionDetailsFragment
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import com.worldexplorerblog.stravaintervall.service.TrainingRecordingService
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.onUiThread

class TrainingExecutionActivity : AppCompatActivity() {

    private var recordingService: TrainingRecordingService? = null

    private val recordingServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            with(service as TrainingRecordingService.TrainingRecordingBinder) {
                recordingService = service.getService() as TrainingRecordingService
                recordingService?.onTimerTick = { onTimerTick() }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Do Nothing
        }
    }

    private val trainingPlan by lazy {
        Gson().fromJson(intent.getStringExtra("training-interval"), TrainingPlanModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TrainingRecordingService::class.java)
        bindService(intent, recordingServiceConnection, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_execution_activity)

        val trainingExecutionDetailsFragment = TrainingExecutionDetailsFragment()
        trainingExecutionDetailsFragment.trainingPlan = trainingPlan
        trainingExecutionDetailsFragment.onStartRecordingClick = { onStartRecordingClick() }
        trainingExecutionDetailsFragment.onStopRecordingClick = { onStopRecordingClick() }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingExecutionDetailsFragment)
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(recordingServiceConnection)
    }

    override fun onBackPressed() {
        alert(message = "Do you want to discard the training?") {
            positiveButton("Discard") {
                recordingService?.stopRecording()
                super.onBackPressed()
            }
            negativeButton("Cencel") { }
        }.show()
    }

    private fun onStartRecordingClick() {
        recordingService?.trainingIntervals = trainingPlan.intervals
        recordingService?.startRecording()
    }

    private fun onStopRecordingClick() {
        // TODO: Show stop warning
        recordingService?.stopRecording()
    }

    private fun onTimerTick() {
        onUiThread {
            // Highlight the current interval
            val index = recordingService?.currentIntervalIndex as Int
            val limit = trainingPlan?.intervals?.count() as Int
            if (index < limit) {
                with(findViewById(R.id.highlight_current_interval) as ImageView) {
                    backgroundColor = when (trainingPlan
                            .intervals[recordingService?.currentIntervalIndex as Int]
                            .intensity) {
                        TrainingIntensity.WarmUp -> resources.getColor(R.color.training_interval_warm_up)
                        TrainingIntensity.Low -> resources.getColor(R.color.training_interval_low)
                        TrainingIntensity.Medium -> resources.getColor(R.color.training_interval_medium)
                        TrainingIntensity.High -> resources.getColor(R.color.training_interval_high)
                        TrainingIntensity.CoolDown -> resources.getColor(R.color.training_interval_cool_down)
                    }
                }
            }

            with(findViewById(R.id.interval_remaining_time_label) as TextView) {
                text = secondsToMMSS(recordingService?.intervalRemainingSeconds ?: 0)
            }

            with(findViewById(R.id.elapsed_time_label) as TextView) {
                text = secondsToHMMSS(recordingService?.trainingElapsedSeconds ?: 0)
            }
        }
    }

    fun secondsToMMSS(seconds: Int): String {
        val m = (seconds / 60).toInt()
        val min = if (m <= 9) {
            "0$m"
        } else {
            "$m"
        }

        val s = seconds % 60
        val sec = if (s <= 9) {
            "0$s"
        } else {
            "$s"
        }

        return "$min:$sec"
    }

    fun secondsToHMMSS(seconds: Int): String {
        val h = (seconds / 3600).toInt()
        val mmss = secondsToMMSS(seconds % 3600)
        return "$h:$mmss"
    }
}

