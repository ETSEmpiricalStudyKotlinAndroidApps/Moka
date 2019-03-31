package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

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
) : Parcelable {

    companion object {

        fun createFromRaw(language: RepositoryQuery.PrimaryLanguage?): Language? = if (language == null) null else Language(
                language.color(),
                language.id(),
                language.name()
        )

        fun createFromRaw(data: RepositoryFragment.PrimaryLanguage?): Language? = if (data == null) null else Language(
                data.color(),
                data.id(),
                data.name()
        )

    }

}