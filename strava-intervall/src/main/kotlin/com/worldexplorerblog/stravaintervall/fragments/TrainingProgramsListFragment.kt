package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ListView
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.adapters.TrainingProgramAdapter
import com.worldexplorerblog.stravaintervall.models.TrainingIntensity
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import com.worldexplorerblog.stravaintervall.models.TrainingProgramModel

class TrainingProgramsListFragment : Fragment() {
    public var onTrainingProgramSelected: (TrainingProgramModel) -> Unit = { trainingProgram -> /* Do Nothing */ }
    public var onCreateNewTrainingProgramAction: () -> Boolean = { false /* Do Nothing */ }

    var trainingPrograms = emptyList<TrainingProgramModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        trainingPrograms = listOf(
                TrainingProgramModel(context.getString(R.string.standard_training_program_name),
                                     arrayOf(TrainingIntervalModel(TrainingIntensity.WarmUp, 5 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.WarmUp, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.WarmUp, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.WarmUp, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.WarmUp, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.WarmUp, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.Medium, 1 * 60),
                                             TrainingIntervalModel(TrainingIntensity.CoolDown, 5 * 60)
                                     )
                ),
                TrainingProgramModel(context.getString(R.string.pyramid_training_program_name),
                                     arrayOf(TrainingIntervalModel(TrainingIntensity.WarmUp, 5 * 60),
                                             TrainingIntervalModel(TrainingIntensity.High, 15),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 25),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 35),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 45),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 55),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 45),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 35),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 25),
                                             TrainingIntervalModel(TrainingIntensity.Low, 40),
                                             TrainingIntervalModel(TrainingIntensity.High, 15),
                                             TrainingIntervalModel(TrainingIntensity.CoolDown, 5 * 60)
                                     )
                )
        )
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.training_programs_list_fragment, container, false)

        with(layout?.findViewById(R.id.training_programs_list) as ListView) {
            adapter = TrainingProgramAdapter(context,
                                             R.layout.training_program_adapter,
                                             trainingPrograms.toTypedArray())

            with(adapter as TrainingProgramAdapter) {
                setNotifyOnChange(true)
            }
        }

        return layout
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.training_programs_list_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.create_new_training_program_action -> onCreateNewTrainingProgramAction()
            else -> super.onOptionsItemSelected(item)
        }
    }
}