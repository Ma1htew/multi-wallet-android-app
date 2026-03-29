package com.example.wallettestversion.di

import android.content.Context
import androidx.room.Room
import com.example.wallettestversion.data.local.AppDatabase
import com.example.wallettestversion.data.local.WalletDao
import com.example.wallettestversion.data.remote.CurrencyApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAccessKey(): String = "2beea4564ccdfc6291503cfb4b5bce06"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "wallet_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDao(db: AppDatabase): WalletDao = db.dao()

    @Provides
    @Singleton
    fun provideCurrencyApi(): CurrencyApi {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://api.currencylayer.com/")
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(CurrencyApi::class.java)
    }
}