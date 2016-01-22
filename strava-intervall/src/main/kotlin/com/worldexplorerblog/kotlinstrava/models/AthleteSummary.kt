package com.worldexplorerblog.kotlinstrava.models

data class AthleteSummary(val id: Int,
                          val firstName: String,
                          val lastName: String,
                          val profileMedium: String,
                          val profile: String,
                          val city: String,
                          val state: String,
                          val country: String,
                          val sex: String,
                          val friend: String,
                          val follower: String,
                          val premium: Boolean,
                          val createdAt: String,
                          val updatedAt: String)