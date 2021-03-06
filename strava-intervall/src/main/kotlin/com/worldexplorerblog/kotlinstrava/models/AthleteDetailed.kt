package com.worldexplorerblog.kotlinstrava.models

data class AthleteDetailed(val id: Int,
                           val firstname: String,
                           val lastname: String,
                           val profile_medium: String,
                           val profile: String,
                           val city: String,
                           val state: String,
                           val country: String,
                           val sex: String,
                           val friend: String?,
                           val follower: String?,
                           val premium: Boolean,
                           val created_at: String,
                           val updated_at: String,
                           val follower_count: Int,
                           val friend_count: Int,
                           val mutual_friend_count: Int,
                           val athlete_type: Int,
                           val date_preference: String,
                           val measurement_preference: String,
                           val email: String,
                           val ftp: Int?,
                           val weight: Float?,
                           val clubs: List<ClubSummary>,
                           val bikes: List<GearSummary>,
                           val shoes: List<GearSummary>)

