package com.worldexplorerblog.stravaintervall.models

enum class TrainingIntensity {
    WarmUpOrCoolDown,
    Low,
    Medium,
    High
}

data class TrainingIntervalModel(var intensity: TrainingIntensity,
                                 var durationInSeconds: Long)