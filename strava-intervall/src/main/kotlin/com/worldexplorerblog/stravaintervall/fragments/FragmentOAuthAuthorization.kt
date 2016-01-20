package com.worldexplorerblog.stravaintervall.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.worldexplorerblog.stravaintervall.R

class FragmentOAuthAuthorization : Fragment() {
    private val upper = this

    public var oauthWorkflowCallback: () -> Unit = {}
        get
        set

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.fragment_welcome_authorization, container, false)
        // TODO: setup the web view

        return layout
    }
}