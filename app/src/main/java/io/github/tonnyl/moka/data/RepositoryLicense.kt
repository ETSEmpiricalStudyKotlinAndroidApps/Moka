package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepositoryLicense(
        val key: String,
        val name: String,
        val spdxId: String,
        val url: String,
        val nodeId: String
) : Parcelable