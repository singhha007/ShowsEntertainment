package com.servicetitan.android.platform.android.showentertainment.api.model

import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<T>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)