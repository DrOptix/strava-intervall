package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.ImageView
import android.widget.TextView
import com.worldexplorerblog.kotlinstrava.KotlinStrava
import com.worldexplorerblog.stravaintervall.R
import org.jetbrains.anko.async
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.onUiThread
import java.net.URL

class DashboardActivity : FragmentActivity() {
    var storedToken: String
        get() {
            val pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
            return pref.getString(getString(R.string.token_name), "token")
        }
        set(value) {
            getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.token_name), value)
                    .commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)


        val stravaAthlete = KotlinStrava(storedToken)
                .athlete
                .get()

        with(findViewById(R.id.athlete_welcome_label) as TextView) {
            text = stravaAthlete?.firstname + getString(R.string.athlete_welcome)
        }

        with(findViewById(R.id.athlete_picture) as ImageView) {
            async() {
                val athletePictureBitmap = BitmapFactory.decodeStream(
                        URL(stravaAthlete?.profile)
                                .openConnection()
                                .inputStream)

                onUiThread {
                    imageBitmap = athletePictureBitmap
                }
            }
        }
    }
}