package com.servicetitan.android.platform.android.showentertainment.api.model

import com.servicetitan.android.platform.android.showentertainment.api.ShowApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ShowApiRepository(private val showApi: ShowApi) {

    fun popularShow(): Flow<List<Show>> =
        flow { emit(showApi.popularShow().results) }

    fun showDetails(id: Int): Flow<ShowDetail> =
        flow { emit(showApi.showDetails(id)) }

    fun genre(): Flow<List<Genre>> =
        flow { emit(showApi.genre().genres) }
}