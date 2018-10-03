package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepositoryPermissions(
        val admin: Boolean,
        val push: Boolean,
        val pull: Boolean
) : Parcelable