package com.servicetitan.android.platform.android.showentertainment.api.model

import com.google.gson.annotations.SerializedName

data class Show(
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int> = emptyList(),
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("origin_country") val originCountry: List<String> = emptyList(),
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("popularity") val popularity: Double?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int,
    var genreList: List<Genre> = emptyList()
) {
    fun updateGenre(genres: List<Genre>) {
        genreList = genres.filter { genreIds.contains(it.id) }
    }
}