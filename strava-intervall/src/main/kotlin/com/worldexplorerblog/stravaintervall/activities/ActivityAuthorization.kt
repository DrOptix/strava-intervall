package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Window
import com.worldexplorerblog.kotlinstrava.KotlinStrava
import com.worldexplorerblog.kotlinstrava.exceptions.OAuthNotAuthenticatedException
import com.worldexplorerblog.stravaintervall.R
import com.worldexplorerblog.stravaintervall.fragments.AuthorizationMessageFragment
import com.worldexplorerblog.stravaintervall.fragments.AuthorizationOAuthFragment
import org.jetbrains.anko.toast
import java.util.concurrent.ExecutionException

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

        // Check to see if the stored token is valid
        try {
            val athlete = KotlinStrava(this.storedToken).athlete.get()
            this.toast(this.getString(R.string.authorization_success))

            // TODO: Load IntervalManagementActivity
        } catch(ex: ExecutionException) {
            val cause: OAuthNotAuthenticatedException? = ex.cause as OAuthNotAuthenticatedException
            if (cause?.javaClass?.isInstance(OAuthNotAuthenticatedException()) as Boolean) {
                val messageFragment = AuthorizationMessageFragment()
                messageFragment.message = this.getString(R.string.authorization_default_message)
                messageFragment.onConnectWithStravaClick = { this.onConnectWithStravaClick() }

                this.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, messageFragment)
                        .commit()
            }
        }
    }

    private fun onConnectWithStravaClick() {
        val oauthFragment = AuthorizationOAuthFragment()
        oauthFragment.onAuthorizationSuccess = { token -> this.onAuthorizationSuccess(token) }
        oauthFragment.onAuthorizationFail = { this.onAuthorizationFail() }

        this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, oauthFragment)
                .commit()
    }

    private fun onAuthorizationSuccess(token: String) {
        this.storedToken = token
        this.toast(this.getString(R.string.authorization_success))

        // TODO: load IntervalManagementActivity
    }

    private fun onAuthorizationFail() {
        val messageFragment = AuthorizationMessageFragment()
        messageFragment.message = this.getString(R.string.authorization_default_message)
        messageFragment.onConnectWithStravaClick = { this.onConnectWithStravaClick() }

        this.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, messageFragment)
                .commit()
    }

}