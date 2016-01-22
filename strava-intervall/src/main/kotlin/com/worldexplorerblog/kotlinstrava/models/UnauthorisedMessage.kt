package com.worldexplorerblog.kotlinstrava.models

data class UnauthorisedMessage(val message: String,
                               val errors: Array<Pair<String, String>>)