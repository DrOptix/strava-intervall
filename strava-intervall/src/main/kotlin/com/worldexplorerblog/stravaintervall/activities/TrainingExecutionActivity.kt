package com.worldexplorerblog.stravaintervall.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingExecutionDetailsFragment
import com.worldexplorerblog.stravaintervall.models.TrainingIntervalModel
import com.worldexplorerblog.stravaintervall.models.TrainingPlanModel
import org.jetbrains.anko.toast

class TrainingExecutionActivity : AppCompatActivity() {
    private val trainingPlan by lazy {
        Gson().fromJson(intent.getStringExtra("training-interval"), TrainingPlanModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_execution_activity)

        val trainingExecutionDetailsFragment = TrainingExecutionDetailsFragment()
        trainingExecutionDetailsFragment.trainingPlan = trainingPlan
        trainingExecutionDetailsFragment.onIntervalSelected = { trainingInterval -> onIntervalSelected(trainingInterval) }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingExecutionDetailsFragment)
                .commit()
    }

    private fun onIntervalSelected(trainingInterval: TrainingIntervalModel) {
        toast(trainingInterval.intensity.toString())
    }
}

