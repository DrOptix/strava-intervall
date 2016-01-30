package com.worldexplorerblog.kotlinstrava

import com.github.kevinsawicki.http.HttpRequest
import com.google.gson.Gson
import com.worldexplorerblog.kotlinstrava.exceptions.OAuthNotAuthenticatedException
import com.worldexplorerblog.kotlinstrava.models.AthleteDetailed
import com.worldexplorerblog.kotlinstrava.models.UploadStatus
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.entity.mime.content.FileBody
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.util.EntityUtils
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

    public fun uploadActivity(activity_type: String? = null,
                              name: String? = null,
                              description: String? = null,
                              private: String? = null,
                              trainer: String? = null,
                              data_type: String,
                              external_id: String? = null,
                              file: String): Future<UploadStatus> {
        return asyncResult {
            try {

                //                var request = HttpRequest("https://www.strava.com/api/v3/uploads", "POST")
                //                        .header("Authorization", "Bearer $token")
                //                        .header("Content-type", "multipart/form-data")

                val dataMap = mapOf(
                        Pair("activity_type", activity_type),
                        Pair("name", name),
                        Pair("description", description),
                        Pair("private", private),
                        Pair("trainer", trainer),
                        Pair("data_type", data_type),
                        Pair("external_id", external_id))

                val entity = MultipartEntityBuilder.create()
                        .addTextBody("data_type", data_type)
                        .addPart("file", FileBody(File(file)))
                        .build()

                val request = HttpPost("https://www.strava.com/api/v3/uploads")
                request.entity = entity
                request.addHeader("Authorization", "Bearer $token")

                val client = DefaultHttpClient()
                val response = EntityUtils.toString(client.execute(request).entity, "UTF-8")

                Gson().fromJson(response, UploadStatus::class.java)
            } catch (ex: Exception) {
                throw ex
            }
        }
    }
}