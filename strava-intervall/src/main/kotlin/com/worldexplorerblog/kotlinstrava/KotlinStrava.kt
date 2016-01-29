package com.worldexplorerblog.kotlinstrava

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.worldexplorerblog.kotlinstrava.exceptions.OAuthNotAuthenticatedException
import com.worldexplorerblog.kotlinstrava.models.AthleteDetailed
import com.worldexplorerblog.kotlinstrava.models.UploadStatus
import org.jetbrains.anko.asyncResult
import java.io.File
import java.util.concurrent.Future

class KotlinStrava(token: String) {
    private val token = token
    private val gson = Gson()

    public val athlete: Future<AthleteDetailed?>
        get() = asyncResult { getAthlete() }

    private fun getAthlete(): AthleteDetailed? {
        try {
            val request = HttpRequest("https://www.strava.com/api/v3/athlete", "GET")
                    .header("Authorization", "Bearer $token")
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

    public fun uploadActivity(activity_type: String?,
                              name: String?,
                              description: String?,
                              private: String?,
                              trainer: String?,
                              data_type: String,
                              external_id: String?,
                              file: String): Future<UploadStatus> {
        return asyncResult {
            try {
                val dataMap = mapOf(
                        Pair("activity_type", activity_type),
                        Pair("name", name),
                        Pair("description", description),
                        Pair("private", private),
                        Pair("trainer", trainer),
                        Pair("data_type", data_type),
                        Pair("external_id", external_id))

                var request = HttpRequest("https://www.strava.com/api/v3/uploads", "POST")
                        .header("Authorization", "Bearer $token")
                for ((key, value) in dataMap) {
                    if (value != null) {
                        request.part(key, value)
                    }
                }
                request.part("file", File(file))

                val result = request.body()

                if (!result.toUpperCase().contains("Authorization Error".toUpperCase())) {
                    gson.fromJson(result, UploadStatus::class.java)
                } else {
                    throw OAuthNotAuthenticatedException()
                }
            } catch (ex: HttpRequest.HttpRequestException) {
                throw ex
            }
        }
    }
}