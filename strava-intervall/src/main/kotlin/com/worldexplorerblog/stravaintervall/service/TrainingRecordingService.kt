package com.worldexplorerblog.stravaintervall.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import java.text.SimpleDateFormat
import java.util.*

data class RecordedIntervalModel(val timestamp: String,
                                 val locations: ArrayList<Location>)

class TrainingRecordingService : Service() {
    public var trainingIntervals = emptyList<TrainingIntervalModel>().toArrayList()

    public var recordedIntervals = emptyList<RecordedIntervalModel>().toArrayList()
        get
        private set

    public var intervalRemainingSeconds = 0
        get
        private set

    public var trainingElapsedSeconds = 0
        get
        private set

    public var currentIntervalIndex = 0
        get
        private set

    public var onTimerTick: () -> Unit = { /* Do Nothing */ }

    private val binder = TrainingRecordingBinder()
    private var timer = Timer()
    private var textToSpeech: TextToSpeech? = null

    public fun startRecording() {
        trainingElapsedSeconds = 0
        intervalRemainingSeconds = 0
        currentIntervalIndex = -1
        timer.schedule(object : TimerTask() {
            override fun run() {
                processTimerTick()
                onTimerTick()
            }
        }, 0, 1000)
    }

    public fun stopRecording() {
        timer.cancel()
        timer.purge()
        timer = Timer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.shutdown()
    }

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            textToSpeech?.setLanguage(Locale.UK)
        })
        textToSpeech?.speak(" ", TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun processTimerTick() {
        if (intervalRemainingSeconds == 0) {
            currentIntervalIndex += 1

            if (currentIntervalIndex < trainingIntervals.count()) {
                speakInterval()
                intervalRemainingSeconds = trainingIntervals[currentIntervalIndex].durationInSeconds

                recordedIntervals.add(
                        RecordedIntervalModel(timeStamp(), ArrayList<Location>()))
            } else if (currentIntervalIndex == trainingIntervals.count()) {
                speakGoalAchieved()
            }
        }

        trainingElapsedSeconds += 1

        if (currentIntervalIndex < trainingIntervals.count()) {
            intervalRemainingSeconds -= 1
        }
    }

    private fun speakGoalAchieved() {
        textToSpeech?.speak("You achieved your goal. You can continue training or conclude your training session.",
                            TextToSpeech.QUEUE_FLUSH,
                            null)
    }

    private fun speakInterval() {
        val currentInterval = trainingIntervals[currentIntervalIndex]

        val minutes = currentInterval.durationInSeconds / 60
        val seconds = currentInterval.durationInSeconds % 60

        var durationString = if (minutes > 1 && seconds > 0) {
            "$minutes minutes $seconds seconds"
        } else if (minutes > 1 && seconds == 0) {
            "$minutes minutes"
        } else if (minutes == 1 && seconds > 0) {
            "1 minute $seconds seconds"
        } else if (minutes == 1 && seconds == 0) {
            "1 minute"
        } else {
            "$seconds seconds"
        }

        textToSpeech?.speak("${currentInterval.intensity.toString()} intensity, $durationString",
                            TextToSpeech.QUEUE_FLUSH,
                            null)
    }

    private fun timeStamp(): String {
        val timezone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timezone)

        val formatter = SimpleDateFormat("YYYY-MM-DDTHH:mm:ss z")
        formatter.calendar = calendar
        formatter.timeZone = timezone

        val timestamp = formatter.format(calendar.time)
        return timestamp
    }

    inner class TrainingRecordingBinder : Binder() {
        fun getService(): TrainingRecordingService {
            return this@TrainingRecordingService
        }
    }
}
