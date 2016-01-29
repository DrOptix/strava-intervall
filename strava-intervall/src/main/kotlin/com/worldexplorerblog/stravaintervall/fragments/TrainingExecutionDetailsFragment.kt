package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.onClick
import java.util.*

class TrainingExecutionDetailsFragment : Fragment() {
    public var trainingPlan: TrainingPlanModel? = null

    public var onStartRecordingClick: () -> Unit = { /* Do Nothing */ }
    public var onStopRecordingClick: () -> Unit = { /* Do Nothing */ }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.training_execution_details_fragment, container, false)

        with(layout?.findViewById(R.id.training_program_adapter) as RelativeLayout) {
            with(findViewById(R.id.training_program_name_label) as TextView) {
                text = trainingPlan?.name
            }

            with(findViewById(R.id.training_program_time_label) as TextView) {
                var totalSeconds = 0
                trainingPlan?.intervals?.forEach {
                    totalSeconds += it.durationInSeconds
                }
                text = secondsToHMMSS(totalSeconds)
            }

            with(findViewById(R.id.training_program_color_map) as LinearLayout) {
                removeAllViews()
                for (item in trainingPlan?.intervals as ArrayList<TrainingIntervalModel>) {
                    val intensityColorView = View(context)
                    intensityColorView.layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f * item.durationInSeconds)

                    intensityColorView.backgroundColor = when (item.intensity) {
                        TrainingIntensity.WarmUp -> resources.getColor(R.color.training_interval_warm_up)
                        TrainingIntensity.Low -> resources.getColor(R.color.training_interval_low)
                        TrainingIntensity.Medium -> resources.getColor(R.color.training_interval_medium)
                        TrainingIntensity.High -> resources.getColor(R.color.training_interval_high)
                        TrainingIntensity.CoolDown -> resources.getColor(R.color.training_interval_cool_down)
                    }
                    addView(intensityColorView)
                }
            }
        }

        with(layout?.findViewById(R.id.start_training_button) as Button) {
            onClick {
                with(layout?.findViewById(R.id.stop_training_button) as Button) {
                    visibility = View.VISIBLE
                }

                visibility = View.GONE
                onStartRecordingClick()
            }
        }

        with(layout?.findViewById(R.id.stop_training_button) as Button) {
            onClick {
                with(layout?.findViewById(R.id.start_training_button) as Button) {
                    visibility = View.VISIBLE
                }

                visibility = View.GONE
                onStopRecordingClick()
            }
        }

        return layout
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