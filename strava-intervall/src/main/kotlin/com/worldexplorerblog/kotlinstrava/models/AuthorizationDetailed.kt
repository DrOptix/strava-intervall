package com.worldexplorerblog.kotlinstrava.models

data class AuthorizationDetailed(val access_token: String,
                                 val athlete: AthleteDetailed)