package com.worldexplorerblog.stravaintervall.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingDetailsFragment
import com.worldexplorerblog.stravaintervall.fragments.TrainingsListFragment
import com.worldexplorerblog.stravaintervall.models.TrainingModel
import org.jetbrains.anko.longToast

class TrainingProgramsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_programs_activity)

        val trainingsListFragment = TrainingsListFragment()
        trainingsListFragment.onTrainingSelected = { trainingModel -> onTrainingSelected(trainingModel) }
        trainingsListFragment.onCreateNewTrainingAction = { onCreateNewTrainingAction() }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingsListFragment)
                .commit()
    }

    private fun onTrainingSelected(trainingModel: TrainingModel) {
        val trainingDetailsFragment = TrainingDetailsFragment(trainingModel)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingDetailsFragment)
                .addToBackStack("Training")
                .commit()
    }

    private fun onCreateNewTrainingAction() {
        longToast("Not implemented yet")
    }
}