package com.worldexplorerblog.stravaintervall.activities

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.FragmentOAuthAuthorization
import com.worldexplorerblog.stravaintervall.fragments.FragmentWelcomeAuthorization

class ActivityAuthorization : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_authorization)

        when (this.intent.getStringExtra("show-authorization-welcome")) {
            null, "yes".toUpperCase() -> {
                val fragmentWelcomeAuthorization = FragmentWelcomeAuthorization()
                fragmentWelcomeAuthorization.connectWithStravaCallback = {
                    // TODO: replace the welcome authorization fragment with the oauth one
                    Toast.makeText(this, "Muhaha", Toast.LENGTH_SHORT).show()
                }

                this.supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragmentWelcomeAuthorization)
                        .commit()
            }

            "no".toUpperCase() -> {
                val fragmentOAuthAuthorization = FragmentOAuthAuthorization()
                fragmentOAuthAuthorization.oauthWorkflowCallback = {
                    // TODO: write the damn callback
                }

                this.supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, FragmentOAuthAuthorization())
                        .commit()
            }
        }
    }
}