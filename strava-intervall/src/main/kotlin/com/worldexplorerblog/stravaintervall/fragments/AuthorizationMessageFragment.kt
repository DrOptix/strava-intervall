package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.worldexplorerblog.stravaintervall.R

class AuthorizationMessageFragment : Fragment() {
    private val upper = this

    public var onConnectWithStravaClick = { }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.authorization_message_fragment, container, false)
        with(layout?.findViewById(R.id.button_connect_with_strava) as ImageButton) {
            this.setOnClickListener {
                upper.onConnectWithStravaClick()
            }
        }

        return layout
    }
}