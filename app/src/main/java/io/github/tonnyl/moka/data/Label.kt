package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Label(

    /**
     * Identifies the label color.
     */
    val color: String,

    /**
     * Identifies the date and time when the label was created.
     */
    val createdAt: Date?,

    /**
     * A brief description of this label.
     */
    val description: String?,

    val id: String,

    /**
     * Indicates whether or not this is a default label.
     */
    val isDefault: Boolean,

    /**
     * Identifies the label name.
     */
    val name: String,

    /**
     * The HTTP path for this label.
     */
    val resourcePath: Uri,

    /**
     * Identifies the date and time when the label was last updated.
     */
    val updatedAt: Date?,

    /**
     * The HTTP URL for this label.
     */
    val url: Uri

) : Parcelable