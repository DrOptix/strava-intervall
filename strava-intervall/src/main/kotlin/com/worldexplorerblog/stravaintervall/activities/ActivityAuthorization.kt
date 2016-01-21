package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.AuthorizationOAuthFragment
import com.worldexplorerblog.stravaintervall.fragments.AuthorizationMessageFragment

class ActivityAuthorization : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.authorization_activity)

        when (this.intent.getStringExtra("show-authorization-welcome")) {
            null, "yes".toUpperCase() -> {
                val appName = this.getString(R.string.app_name)
                val storedToken = this.getSharedPreferences(appName, Context.MODE_PRIVATE)
                        .getString("token", "")

                when (isValidToken(storedToken)) {
                    true -> {
                        // TODO: load interval selection activity
                    }

                    false -> {
                        initializeWelcomeFragment()
                    }
                }
            }

            "no".toUpperCase() -> {
                initializeOAuthFragment()
            }
        }
    }

    private fun onConnectWithStravaClick() {
        initializeOAuthFragment()
    }

    private fun onOAuthWorkflowFinished(token: String) {
        this.getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE)
                .edit()
                .putString("token", token)
                .commit()
    }

    private fun initializeWelcomeFragment() {
        val fragmentWelcome = AuthorizationMessageFragment()
        this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentWelcome)
                .commit()

        fragmentWelcome.onConnectWithStravaClick = {
            run { this.onConnectWithStravaClick() }
        }
    }

    private fun initializeOAuthFragment() {
        val fragmentOAuth = AuthorizationOAuthFragment()
        this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentOAuth)
                .commit()

        fragmentOAuth.onOAuthWorkflowFinished = {
            token ->
            run { this.onOAuthWorkflowFinished(token) }
        }
    }

    private fun isValidToken(token: String): Boolean {
        return true
    }
}