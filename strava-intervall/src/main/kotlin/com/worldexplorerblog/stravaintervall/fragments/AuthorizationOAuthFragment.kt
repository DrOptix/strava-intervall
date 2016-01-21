package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.worldexplorerblog.stravaintervall.R

class AuthorizationOAuthFragment : Fragment() {
    private val upper = this

    public var onOAuthWorkflowFinished: (String) -> Unit = { token: String -> /* Do Nothing */ }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.authorization_oauth_fragment, container, false)
        // TODO: setup the web view

        this.onOAuthWorkflowFinished("caca")

        return layout
    }
}