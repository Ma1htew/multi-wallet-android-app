package com.example.wallettestversion.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRates(
    val success: Boolean,
    val source: String,
    val quotes: Map<String, Double>
)