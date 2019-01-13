package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrendingLanguages(
        val popular: List<TrendingLanguage>,
        val all: List<TrendingLanguage>
) : Parcelable

@Parcelize
data class TrendingLanguage(
        val urlParam: String,
        val name: String
) : Parcelable