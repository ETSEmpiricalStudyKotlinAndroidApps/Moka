package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrendingRepository(
        val author: String,
        val name: String,
        val url: String,
        val description: String?,
        val language: String?,
        val languageColor: String?,
        val stars: Int,
        val forks: Int,
        val currentPeriodStars: Int,
        val builtBy: List<TrendingRepositoryBuiltBy>
) : Parcelable

@Parcelize
data class TrendingRepositoryBuiltBy(
        val href: String,
        val avatar: String,
        val username: String
) : Parcelable