package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestsQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class PullRequestItem(
        val avatarUrl: Uri?,
        val login: String?,
        val id: String,
        val number: Int,
        val createdAt: Date,
        val title: String,
        val closed: Boolean,
        val merged: Boolean
) : Parcelable {

    companion object {

        fun createFromRaw(data: PullRequestsQuery.Node): PullRequestItem = PullRequestItem(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                data.id(),
                data.number(),
                data.createdAt(),
                data.title(),
                data.closed(),
                data.merged()
        )

    }

}