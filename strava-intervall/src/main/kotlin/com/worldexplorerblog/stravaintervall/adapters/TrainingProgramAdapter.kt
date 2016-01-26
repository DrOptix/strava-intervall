package com.worldexplorerblog.stravaintervall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingProgramModel
import org.jetbrains.anko.backgroundColor
import java.util.*

class TrainingProgramAdapter : ArrayAdapter<TrainingProgramModel> {
    constructor(context: Context?,
                resource: Int)
    : super(context, resource)

    constructor(context: Context?,
                resource: Int,
                items: ArrayList<TrainingProgramModel>)
    : super(context, resource, items)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var adapterView: View?
        val trainingProgramModel = getItem(position)

        if (convertView == null) {
            adapterView = LayoutInflater.from(context).inflate(R.layout.training_program_adapter, parent, false)
        } else {
            adapterView = convertView
        }

        with(adapterView?.findViewById(R.id.training_program_name_label) as TextView) {
            text = trainingProgramModel.name
        }

        with(adapterView?.findViewById(R.id.training_program_time_label) as TextView) {
            var totalSeconds = 0
            trainingProgramModel.intervals.forEach {
                totalSeconds += it.durationInSeconds
            }
            text = secondsToTime(totalSeconds)
        }

        with(adapterView?.findViewById(R.id.training_program_color_map) as LinearLayout) {
            removeAllViews()

            for (item in trainingProgramModel.intervals) {
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

        return adapterView
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