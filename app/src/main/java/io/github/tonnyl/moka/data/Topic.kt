package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.Topic as RawTopic

@Parcelize
data class Topic(

    val id: String,

    /**
     * The topic's name.
     */
    val name: String,

    /**
     * Returns a boolean indicating whether the viewing user has starred this starrable.
     */
    val viewerHasStarred: Boolean

) : Parcelable

fun RawTopic.toNonNullTopic(): Topic {
    return Topic(id, name, viewerHasStarred)
}