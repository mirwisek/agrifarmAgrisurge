package com.fyp.agrifarm.app.crops

import com.google.gson.annotations.SerializedName


data class ModelResult (
        @SerializedName("class")
        val label: String,
        @SerializedName("prob")
        val probability: String
)

data class ModelResponse (
        val output: List<ModelResult>
)
