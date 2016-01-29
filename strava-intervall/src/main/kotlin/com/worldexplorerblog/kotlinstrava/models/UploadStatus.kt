package com.worldexplorerblog.kotlinstrava.models

data class UploadStatus(val  id: Int,
                        val external_id: String,
                        val error: String?,
                        val status: String,
                        val activity_id: Int?)