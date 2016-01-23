package com.worldexplorerblog.stravaintervall.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.worldexplorerblog.kotlinstrava.models.AuthorizationDetailed
import com.worldexplorerblog.stravaintervall.R
import org.jetbrains.anko.async
import org.jetbrains.anko.support.v4.onUiThread

class AuthorizationOAuthFragment : Fragment() {
    public var onAuthorizationSuccess: (String) -> Unit = { token: String -> /* Do Nothing */ }
    public var onAuthorizationFail: () -> Unit = { /* Do Nothing */ }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.authorization_oauth_fragment, container, false)
        val webView = layout?.findViewById(R.id.webview) as WebView

        var authorizeUrl = "https://www.strava.com/oauth/authorize"
        authorizeUrl += "?client_id=${getString(R.string.client_id)}}"
        authorizeUrl += "&response_type=code"
        authorizeUrl += "&redirect_uri=${getString(R.string.callback_domain) + getString(R.string.callback_path)}"
        authorizeUrl += "&scope=write"
        authorizeUrl += "&approval_prompt=force"

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val isCallbackDomain = !(url?.contains("://strava.com", true) as Boolean)
                                       && url?.contains(getString(R.string.callback_domain), true) as Boolean

                if (isCallbackDomain) {
                    if (url?.contains("code=", true) as Boolean) {
                        view?.visibility = View.INVISIBLE
                        async() {
                            var postData = "client_id=${getString(R.string.client_id)}"
                            postData += "&client_secret=${getString(R.string.client_secret)}"
                            postData += "&code=${Uri.parse(url).getQueryParameter("code")}"

                            val request = HttpRequest.post("https://www.strava.com/oauth/token")
                                    .ignoreCloseExceptions(true)
                                    .send(postData)
                            val result = request?.body()

                            onUiThread {
                                if (!(result?.contains("Authorization Error", true) as Boolean)) {
                                    val authDetails = Gson().fromJson(result, AuthorizationDetailed::class.java)

                                    onAuthorizationSuccess(authDetails.access_token)
                                } else {
                                    onAuthorizationFail()
                                }
                            }
                        }
                    } else if (url?.contains("error=access_denied", true) as Boolean) {
                        onAuthorizationFail()
                    }
                }
            }
        });
        webView.loadUrl(authorizeUrl)

        return layout
    }
}