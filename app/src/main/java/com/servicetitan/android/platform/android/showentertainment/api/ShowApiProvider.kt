package com.servicetitan.android.platform.android.showentertainment.api

import com.servicetitan.android.platform.android.showentertainment.api.model.ShowApiRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY_TAG = "api_key"
private const val API_KEY = "5812e4b63553d1273a420416fddeed72"

object ShowApiProvider {

    private fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val url = it.request().url().newBuilder().addQueryParameter(
                API_KEY_TAG,
                API_KEY
            ).build()
            it.proceed(it.request().newBuilder().url(url).build())
        }
        .build()

    private fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttp())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun provideShowApi(): ShowApi = provideRetrofit().create(ShowApi::class.java)

    fun showApiRepository() = ShowApiRepository(provideShowApi())
}