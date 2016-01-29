package com.worldexplorerblog.stravaintervall.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingDetailsFragment
import com.worldexplorerblog.stravaintervall.fragments.TrainingsListFragment
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
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

    private fun onTrainingSelected(trainingPlan: TrainingPlanModel) {
        val trainingDetailsFragment = TrainingDetailsFragment(trainingPlan)
        trainingDetailsFragment.onUseTrainingPlanClick = { trainingPlan -> onUseTrainingPlanClick(trainingPlan) }
        trainingDetailsFragment.onEditTrainingPlanClick = { trainingPlan -> onEditTrainingPlanClick(trainingPlan) }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingDetailsFragment)
                .addToBackStack(null)
                .commit()
    }

    private fun onUseTrainingPlanClick(trainingModel: TrainingPlanModel) {
        val intent = Intent(applicationContext, TrainingExecutionActivity::class.java);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("training-interval", Gson().toJson(trainingModel));
        startActivity(intent)
    }

    private fun onEditTrainingPlanClick(trainingModel: TrainingPlanModel) {
        longToast("Not implemented yet")
    }

    private fun onCreateNewTrainingAction() {
        longToast("Not implemented yet")
    }
}