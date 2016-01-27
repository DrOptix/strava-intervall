package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.models.TrainingModel

class TrainingDetailsFragment(trainingModel: TrainingModel) : Fragment() {
    public var onUseTrainingPlan: (TrainingModel) -> Unit = { trainingProgram -> /* Do Nothing */ }
    public var onEditTrainingProgramAction: (TrainingModel) -> Unit = { trainingProgram -> /* Do Nothing */ }

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
                onEditTrainingProgramAction(trainingProgram)
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }
}