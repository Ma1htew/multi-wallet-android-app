package com.example.wallettestversion.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("live")
    suspend fun getLive(
        @Query("access_key") accessKey: String,
        @Query("source") source: String = "USD"
    ): ExchangeRates
}