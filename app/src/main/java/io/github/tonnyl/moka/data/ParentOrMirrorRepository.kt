package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.ParentOfForkedRepository as RawParentOfForkedRepository

@Parcelize
data class ParentOrMirrorRepository(

    /**
     * The HTTP URL for this repository
     */
    val url: Uri,

    /**
     * The name of the repository.
     */
    val name: String,

    /**
     * The repository's name with owner.
     */
    val nameWithOwner: String

) : Parcelable

fun RawParentOfForkedRepository.toNonNullParentOfForkedRepository(): ParentOrMirrorRepository {
    return ParentOrMirrorRepository(url(), name(), nameWithOwner())
}