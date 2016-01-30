package com.worldexplorerblog.stravaintervall.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import java.text.SimpleDateFormat
import java.util.*

data class RecordedIntervalModel(var timestamp: String,
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

    public var currentIntervalIndex = -1
        get
        private set

    public var onTimerTick: () -> Unit = { /* Do Nothing */ }

    private val binder = TrainingRecordingBinder()
    private var timer = Timer()
    private var textToSpeech: TextToSpeech? = null

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (isRecording && location != null) {
                if (currentIntervalIndex != -1
                    && currentIntervalIndex < trainingIntervals.count()) {
                    recordedIntervals[currentIntervalIndex].locations.add(location)

                } else if (currentIntervalIndex < trainingIntervals.count()) {
                    recordedIntervals[currentIntervalIndex + 1].locations.add(location)
                }
            }
            previousBestLocation = location
        }

        override fun onProviderDisabled(provider: String?) {
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }
    }

    var locationManager: LocationManager? = null
    public var previousBestLocation: Location? = null
        get
        private set

    private var isRecording: Boolean = false

    public fun startRecording() {
        isRecording = true

        recordedIntervals.clear()
        trainingIntervals.forEach {
            recordedIntervals.add(RecordedIntervalModel(timeStamp(), ArrayList<Location>()))
        }

        currentIntervalIndex = -1
        trainingElapsedSeconds = 0
        intervalRemainingSeconds = 0
        timer.schedule(object : TimerTask() {
            override fun run() {
                processTimerTick()
                onTimerTick()
            }
        }, 0, 1000)
    }

    public fun stopRecording() {
        isRecording = false
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
        locationManager?.removeUpdates(listener);
    }

    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            textToSpeech?.setLanguage(Locale.UK)
        })
        textToSpeech?.speak(" ", TextToSpeech.QUEUE_FLUSH, null)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5.0f, listener)
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5.0f, listener)
    }

    private fun processTimerTick() {
        if (intervalRemainingSeconds == 0) {
            currentIntervalIndex += 1

            if (currentIntervalIndex < trainingIntervals.count()) {
                speakInterval()
                intervalRemainingSeconds = trainingIntervals[currentIntervalIndex].durationInSeconds

                recordedIntervals[currentIntervalIndex].timestamp = timeStamp()
            } else if (currentIntervalIndex == trainingIntervals.count()) {
                speakGoalAchieved()
                // Add the continuation interval
                recordedIntervals.add(RecordedIntervalModel(timeStamp(), ArrayList<Location>()))
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

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        formatter.calendar = calendar
        formatter.timeZone = timezone

        val timestamp = formatter.format(calendar.time)
        return timestamp
    }

    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val oneSecond = 1000

        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > oneSecond
        val isSignificantlyOlder = timeDelta < -oneSecond
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(location.provider,
                                                currentBestLocation.provider)

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    inner class TrainingRecordingBinder : Binder() {
        fun getService(): TrainingRecordingService {
            return this@TrainingRecordingService
        }
    }
}
