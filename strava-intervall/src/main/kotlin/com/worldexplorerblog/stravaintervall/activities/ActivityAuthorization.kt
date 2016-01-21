package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Window
import com.worldexplorerblog.stravaintervall.R
import javastrava.api.v3.auth.model.Token
import javastrava.api.v3.service.Strava

class ActivityAuthorization : FragmentActivity() {
    var storedToken: String
        get() {
            return getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE)
                    .getString(this.getString(R.string.token_name), "token")
        }
        set(value) {
            getSharedPreferences(this.getString(R.string.app_name), Context.MODE_PRIVATE)
                    .edit()
                    .putString(this.getString(R.string.token_name), value)
                    .commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.authorization_activity)

        val stravaApi = this.createStravaApi()

        try {
            stravaApi.authenticatedAthlete
        } catch(ex: RuntimeException) {
            val message = ex.message
        }
    }

    private fun createStravaApi(): Strava {
        val token = Token()
        token.token = this.storedToken
        return Strava(token)
    }
}