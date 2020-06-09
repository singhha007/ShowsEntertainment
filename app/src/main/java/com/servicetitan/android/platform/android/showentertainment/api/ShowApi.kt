package com.servicetitan.android.platform.android.showentertainment.api

import com.servicetitan.android.platform.android.showentertainment.api.model.GenreResponse
import com.servicetitan.android.platform.android.showentertainment.api.model.Response
import com.servicetitan.android.platform.android.showentertainment.api.model.Show
import com.servicetitan.android.platform.android.showentertainment.api.model.ShowDetail
import retrofit2.http.GET
import retrofit2.http.Path

interface ShowApi {

    @GET("tv/popular")
    suspend fun popularShow(): Response<Show>

    @GET("tv/{id}")
    suspend fun showDetails(@Path("id") showId: Int): ShowDetail

    @GET("genre/movie/list")
    suspend fun genre(): GenreResponse
}