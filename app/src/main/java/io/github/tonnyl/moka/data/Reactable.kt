package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.Reactable as RawReactable

@Parcelize
data class Reactable(

    val id: String,

    /**
     * Can user react to this subject
     */
    val viewerCanReact: Boolean

) : Parcelable

fun RawReactable.toNonNullReactable(): Reactable {
    return Reactable(id, viewerCanReact)
}