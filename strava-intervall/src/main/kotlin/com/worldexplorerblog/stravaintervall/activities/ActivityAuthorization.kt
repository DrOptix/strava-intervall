package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Window
import com.worldexplorerblog.stravaintervall.R

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
    }
}