package com.worldexplorerblog.stravaintervall.activities

import android.content.Context
import android.content.Intent
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

class AuthorizationActivity : FragmentActivity() {
    var storedToken: String
        get() {
            return getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    .getString(getString(R.string.token_name), "token")
        }
        set(value) {
            getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.token_name), value)
                    .commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.authorization_activity)

        try {
            KotlinStrava(storedToken).athlete.get()
            toast(getString(R.string.authorization_success))

            val intent = Intent(this, TrainingProgramsActivity::class.java)
            startActivity(intent)
        } catch(ex: ExecutionException) {
            val cause: OAuthNotAuthenticatedException? = ex.cause as OAuthNotAuthenticatedException
            if (cause?.javaClass?.isInstance(OAuthNotAuthenticatedException()) as Boolean) {
                val messageFragment = AuthorizationMessageFragment()
                messageFragment.message = getString(R.string.authorization_default_message)
                messageFragment.onConnectWithStravaClick = { onConnectWithStravaClick() }

                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, messageFragment)
                        .commit()
            }
        }
    }

    private fun onConnectWithStravaClick() {
        val oauthFragment = AuthorizationOAuthFragment()
        oauthFragment.onAuthorizationSuccess = { token -> onAuthorizationSuccess(token) }
        oauthFragment.onAuthorizationFail = { onAuthorizationFail() }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, oauthFragment)
                .commit()
    }

    private fun onAuthorizationSuccess(token: String) {
        storedToken = token
        toast(getString(R.string.authorization_success))
    }

    private fun onAuthorizationFail() {
        val messageFragment = AuthorizationMessageFragment()
        messageFragment.message = getString(R.string.authorization_default_message)
        messageFragment.onConnectWithStravaClick = { onConnectWithStravaClick() }

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, messageFragment)
                .commit()
    }

}