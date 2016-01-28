package com.worldexplorerblog.stravaintervall.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import org.jetbrains.anko.backgroundColor
import java.util.*

class TrainingIntervalAdapter : ArrayAdapter<TrainingIntervalModel> {
    constructor(context: Context?,
                resource: Int)
    : super(context, resource)

    constructor(context: Context?,
                resource: Int,
                items: ArrayList<TrainingIntervalModel>)
    : super(context, resource, items)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var adapterView: View?
        val trainingInterval = getItem(position)

        if (convertView == null) {
            adapterView = LayoutInflater.from(context).inflate(R.layout.training_interval_adapter, parent, false)
        } else {
            adapterView = convertView
        }

        with(adapterView?.findViewById(R.id.intensity_label) as TextView) {
            text = when (trainingInterval.intensity) {
                TrainingIntensity.WarmUp -> resources.getString(R.string.warm_up_interval)
                TrainingIntensity.Low -> resources.getString(R.string.low_intensity_interval)
                TrainingIntensity.Medium -> resources.getString(R.string.medium_intensity_interval)
                TrainingIntensity.High -> resources.getString(R.string.high_intensity_interval)
                TrainingIntensity.CoolDown -> resources.getString(R.string.cool_down_interval)
            }
        }

        with(adapterView?.findViewById(R.id.time_label) as TextView) {
            text = secondsToTime(trainingInterval.durationInSeconds)
        }

        with(adapterView?.findViewById(R.id.intensity_color) as ImageView) {
            backgroundColor = when (trainingInterval.intensity) {
                TrainingIntensity.WarmUp -> resources.getColor(R.color.training_interval_warm_up)
                TrainingIntensity.Low -> resources.getColor(R.color.training_interval_low)
                TrainingIntensity.Medium -> resources.getColor(R.color.training_interval_medium)
                TrainingIntensity.High -> resources.getColor(R.color.training_interval_high)
                TrainingIntensity.CoolDown -> resources.getColor(R.color.training_interval_cool_down)
            }
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