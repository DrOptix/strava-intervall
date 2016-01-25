package com.worldexplorerblog.stravaintervall.models

enum class TrainingIntensity {
    WarmUp,
    Low,
    Medium,
    High,
    CoolDown
}

data class TrainingIntervalModel(var intensity: TrainingIntensity,
                                 var durationInSeconds: Long)