package com.worldexplorerblog.stravaintervall.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.TrainingExecutionDetailsFragment

class TrainingExecutionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.training_execution_activity)

        val trainingExecutionDetailsFragment = TrainingExecutionDetailsFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, trainingExecutionDetailsFragment)
                .commit()
    }
}

