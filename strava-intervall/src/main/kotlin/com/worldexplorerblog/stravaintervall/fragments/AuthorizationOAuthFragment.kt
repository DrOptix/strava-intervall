package com.worldexplorerblog.stravaintervall.fragments

import android.graphics.Bitmap
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
    private val upper = this

    public var onAuthorizationSuccess: (String) -> Unit = { token: String -> /* Do Nothing */ }
    public var onAuthorizationFail: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val layout = inflater?.inflate(R.layout.authorization_oauth_fragment, container, false)

        val webView = layout?.findViewById(R.id.webview) as WebView

        val redirectUrl = upper.getString(R.string.callback_domain) + upper.getString(R.string.callback_path)
        var authorizeUrl = "https://www.strava.com/oauth/authorize"
        authorizeUrl += "?client_id=${upper.getString(R.string.client_id)}"
        authorizeUrl += "&response_type=code"
        authorizeUrl += "&redirect_uri=$redirectUrl"
        authorizeUrl += "&scope=write"
        authorizeUrl += "&approval_prompt=force"

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                val isCallbackDomain = url?.contains("http://strava.com", true) as Boolean
                                       && url?.contains(upper.getString(R.string.callback_domain), true) as Boolean
                if (isCallbackDomain) {
                    view?.visibility = View.INVISIBLE
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                val isCallbackDomain = url?.contains(upper.getString(R.string.callback_domain), true)

                if (isCallbackDomain as Boolean) {
                    if (url?.contains("code=") as Boolean) {
                        async() {
                            var postData = "client_id=${upper.getString(R.string.client_id)}"
                            postData += "&client_secret=${upper.getString(R.string.client_secret)}"
                            postData += "&code=${Uri.parse(url).getQueryParameter("code")}"

                            val request = HttpRequest.post("https://www.strava.com/oauth/token")
                                    .ignoreCloseExceptions(true)
                                    .send(postData)
                            val result = request?.body()

                            onUiThread {
                                if (!(result?.contains("Authorization Error", true) as Boolean)) {
                                    val gson = Gson()
                                    val authDetails = gson.fromJson(result, AuthorizationDetailed::class.java)
                                    upper.onAuthorizationSuccess(authDetails.access_token)
                                } else {
                                    upper.onAuthorizationFail()
                                }
                            }
                        }
                    }
                }
            }
        });
        webView.loadUrl(authorizeUrl)

        return layout
    }
}