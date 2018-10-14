package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepositoryAbstractOwner(
        val avatarUrl: Uri,
        val id: String,
        val login: String,
        val resourcePath: Uri,
        val url: Uri
) : Parcelable