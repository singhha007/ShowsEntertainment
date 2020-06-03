package com.servicetitan.android.platform.android.showentertainment.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val BASE_URL = "https://api.themoviedb.org/3/"
private val API_KEY_TAG = "api_key"
private val API_KEY = "5812e4b63553d1273a420416fddeed72"

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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun provideShowApi() = provideRetrofit().create(ShowApi::class.java)
}