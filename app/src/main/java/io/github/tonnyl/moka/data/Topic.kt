package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Topic(
        val id: String,
        val name: String
) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryQuery.Topic) = Topic(
                data.id(),
                data.name()
        )

    }

}