package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.adapters.TrainingIntervalAdapter
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.onClick

class TrainingDetailsFragment(trainingModel: TrainingPlanModel) : Fragment() {
    public var onUseTrainingPlanClick: (TrainingPlanModel) -> Unit = { trainingProgram -> /* Do Nothing */ }
    public var onEditTrainingPlanClick: (TrainingPlanModel) -> Unit = { trainingProgram -> /* Do Nothing */ }

    private val trainingProgram = trainingModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()

        with(activity as AppCompatActivity) {
            supportActionBar.title = getString(R.string.training_details_fragment_title)
            supportActionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.training_program_details_fragment, container, false)

        with(layout?.findViewById(R.id.training_program_name_label) as TextView) {
            text = trainingProgram.name
        }

        with(layout?.findViewById(R.id.training_program_time_label) as TextView) {
            var totalSeconds = 0
            trainingProgram.intervals.forEach {
                totalSeconds += it.durationInSeconds
            }
            text = secondsToTime(totalSeconds)
        }

        with(layout?.findViewById(R.id.training_program_color_map) as LinearLayout) {
            removeAllViews()
            for (item in trainingProgram.intervals) {
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
            forceLayout()
        }

        with(layout?.findViewById(R.id.training_program_intervals_list) as ListView) {
            adapter = TrainingIntervalAdapter(context,
                                              R.layout.training_interval_adapter,
                                              trainingProgram.intervals)
        }

        with(layout?.findViewById(R.id.use_program_button) as Button) {
            onClick { onUseTrainingPlanClick(trainingProgram) }
        }

        return layout
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.training_program_details_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                activity.onBackPressed()
                return true
            }

            R.id.edit_training_program_action -> {
                onEditTrainingPlanClick(trainingProgram)
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun secondsToTime(seconds: Int): String {
        val m = (seconds / 60).toInt()
        val min = if (m < 9) {
            "0$m"
        } else {
            "$m"
        }

        val s = seconds % 60
        val sec = if (s < 9) {
            "0$s"
        } else {
            "$s"
        }

        return "$min:$sec"
    }
}