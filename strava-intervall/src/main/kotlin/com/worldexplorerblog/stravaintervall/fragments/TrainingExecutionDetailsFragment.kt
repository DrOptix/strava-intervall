package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import org.jetbrains.anko.onClick

class TrainingExecutionDetailsFragment : Fragment() {
    public var trainingPlan: TrainingPlanModel? = null

    public var onStartRecordingClick: () -> Unit = { /* Do Nothing */ }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.training_execution_details_fragment, container, false)

        with(layout?.findViewById(R.id.start_training_button) as Button) {
            onClick { onStartRecordingClick() }
        }

        return layout
    }
}