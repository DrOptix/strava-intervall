package com.worldexplorerblog.stravaintervall.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.worldexplorerblog.kotlinstrava.KotlinStrava
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingExecutionDetailsFragment
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import com.worldexplorerblog.stravaintervall.service.RecordedIntervalModel
import com.worldexplorerblog.stravaintervall.service.TrainingRecordingService
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.onUiThread
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

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

    var storedToken: String
        get() {
            return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    .getString(getString(R.string.token_name), "token")
        }
        set(value) {
            getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.token_name), value)
                    .commit()
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
        alert(message = "Do you want to finish and upload to Strava?") {
            positiveButton("Upload") {
                // TODO ask to discard if no location data in present

                val outputFile = File.createTempFile("upload", "tcx", baseContext.cacheDir)
                exportTcx(outputFile, recordingService?.recordedIntervals as ArrayList<RecordedIntervalModel>)

                val uploadStatus = KotlinStrava(storedToken).uploadActivity(
                        data_type = "tcx",
                        file = outputFile.absolutePath)

                recordingService?.stopRecording()
                // TODO check the upload status and show a report
            }
            negativeButton("Cencel") { }
        }.show()
    }

    private fun computeNiceZoom(location: Location): Double {
        // Location accuracy diameter (in meters)
        val accuracy = location.accuracy * 2

        // Screen measurements
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        // Use min(width, height) (to properly fit the screen
        val screenSize = Math.min(metrics.widthPixels, metrics.heightPixels)

        // Equators length
        val equator = 40075004

        // The meters per pixel required to show the whole area the user might be located in
        val requiredMpp = accuracy / screenSize

        // Calculate the zoom level
        val zoomLevel = Math.log(equator / (256.0 * requiredMpp)) / Math.log(2.0) + 1
        return zoomLevel
    }

    private fun onTimerTick() {
        onUiThread {
            // Highlight the current interval
            if (recordingService?.previousBestLocation != null) {
                with(findViewById(R.id.map_view) as MapView) {
                    controller.setCenter(GeoPoint(recordingService?.previousBestLocation))
                    controller.setZoom(computeNiceZoom(recordingService?.previousBestLocation as Location).toInt())
                }
            }

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

    private fun exportTcx(file: File, recordedIntervals: ArrayList<RecordedIntervalModel>) {
        val writer = PrintWriter(file)
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")

        writer.println("<TrainingCenterDatabase xsi:schemaLocation=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd\" xmlns:ns5=\"http://www.garmin.com/xmlschemas/ActivityGoals/v1\" xmlns:ns3=\"http://www.garmin.com/xmlschemas/ActivityExtension/v2\" xmlns:ns2=\"http://www.garmin.com/xmlschemas/UserProfile/v2\" xmlns=\"http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
        writer.println("<Activities>")
        writer.println("<Activity Sport=\"Running\">")

        writer.println("<Id>${recordedIntervals[0].timestamp}</Id>")

        for (interval in recordedIntervals) {
            writer.println("<Lap StartTime=\"${recordedIntervals[0].timestamp}\">")
            writer.println("<Track>")

            val dateFormater = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            for (location in interval.locations) {
                writer.println("<Trackpoint>")
                writer.println("<Time>${dateFormater.format(Date(location.time))}</Time>")

                writer.println("<Position>")

                writer.println("<LatitudeDegrees>${location.latitude}</LatitudeDegrees>")
                writer.println("<LongitudeDegrees>${location.longitude}</LongitudeDegrees>")

                writer.println("</Position>")
                writer.println("</Trackpoint>")
            }

            writer.println("</Track>")
            writer.println("</Lap>")
        }

        writer.println("</Activity>")
        writer.println("</Activities>")
        writer.println("</TrainingCenterDatabase>")
        writer.flush()
        writer.close()
    }
}

