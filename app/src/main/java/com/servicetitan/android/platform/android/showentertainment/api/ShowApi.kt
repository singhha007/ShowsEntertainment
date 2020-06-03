package com.servicetitan.android.platform.android.showentertainment.api

import com.servicetitan.android.platform.android.showentertainment.api.model.GenreResponse
import com.servicetitan.android.platform.android.showentertainment.api.model.Response
import com.servicetitan.android.platform.android.showentertainment.api.model.Show
import com.servicetitan.android.platform.android.showentertainment.api.model.ShowDetail
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ShowApi {

    @GET("tv/popular")
    fun popularShow(): Observable<Response<Show>>

    @GET("tv/{id}")
    fun showDetails(@Path("id") showId: Int): Observable<ShowDetail>

    @GET("genre/movie/list")
    fun genre(): Observable<GenreResponse>
}