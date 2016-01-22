package com.worldexplorerblog.kotlinstrava

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.worldexplorerblog.kotlinstrava.exceptions.OAuthNotAuthenticatedException
import com.worldexplorerblog.kotlinstrava.models.AthleteDetailed
import org.jetbrains.anko.asyncResult
import java.util.concurrent.Future

class KotlinStrava(token: String) {
    private val upper = this

    private val token = token
    private val gson = Gson()

    val athlete: Future<AthleteDetailed?>
        get() = asyncResult { upper.getAthlete() }

    private fun getAthlete(): AthleteDetailed? {
        try {
            val request = HttpRequest("https://www.strava.com/api/v3/athlete", "GET")
                    .header("Authorization", "Bearer ${upper.token}")
            val result = request.body()

            if (!result.toUpperCase().contains("Authorization Error".toUpperCase())) {
                return gson.fromJson(result, AthleteDetailed::class.java)
            } else {
                throw OAuthNotAuthenticatedException()
            }

        } catch(ex: HttpRequest.HttpRequestException) {
            throw ex
        }
    }
}