package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.adapters.TrainingIntervalAdapter
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onItemSelectedListener
import java.util.*

class TrainingExecutionDetailsFragment : Fragment() {
    public var trainingPlan: TrainingPlanModel? = null
        get
        set

    public var onIntervalSelected: (TrainingIntervalModel) -> Unit = { /* Do Nothing */ }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.training_execution_details_fragment,
                                       container,
                                       false)

        val intervalList = layout?.findViewById(R.id.training_program_intervals_list) as ListView
        if (trainingPlan?.intervals != null) {
            with(intervalList) {
                choiceMode = ListView.CHOICE_MODE_SINGLE;
                adapter = TrainingIntervalAdapter(context,
                                                  R.layout.training_interval_adapter,
                                                  trainingPlan?.intervals as ArrayList<TrainingIntervalModel>)

                onItemSelectedListener {
                    onItemSelected { parent, view, position, id ->
                        onIntervalSelected(parent?.getItemAtPosition(position) as TrainingIntervalModel)
                    }
                }
            }
        }

        with(layout?.findViewById(R.id.start_training_button) as Button) {
            onClick {
                intervalList.setItemChecked(0, true)

                // TODO: strart recording from gps and start intervl playlist
            }
        }

        return layout
    }
}