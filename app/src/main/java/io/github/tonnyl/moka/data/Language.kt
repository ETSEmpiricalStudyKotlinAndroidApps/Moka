package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.Language as RawLanguage

/**
 * Represents a given language found in repositories.
 */
@Parcelize
data class Language(

    /**
     * The color defined for the current language.
     */
    val color: String?,

    val id: String,

    /**
     * The name of the current language.
     */
    val name: String

) : Parcelable

fun RawLanguage.toNonNullLanguage(): Language {
    return Language(color(), id(), name())
}