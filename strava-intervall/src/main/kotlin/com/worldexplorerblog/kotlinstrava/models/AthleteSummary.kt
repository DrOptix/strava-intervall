package com.worldexplorerblog.kotlinstrava.models

data class AthleteSummary(val id: Int,
                          val firstname: String,
                          val lastname: String,
                          val profile_medium: String,
                          val profile: String,
                          val city: String,
                          val state: String,
                          val country: String,
                          val sex: String,
                          val friend: String,
                          val follower: String,
                          val premium: Boolean,
                          val created_at: String,
                          val updated_at: String)