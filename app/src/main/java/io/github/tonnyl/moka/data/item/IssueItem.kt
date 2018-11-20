package io.github.tonnyl.moka.data.item

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.IssuesQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class IssueItem(
        val avatarUrl: Uri?,
        val login: String?,
        val id: String,
        val number: Int,
        val createdAt: Date,
        val title: String,
        val closed: Boolean
) : Parcelable {

    companion object {

        fun createFromRaw(data: IssuesQuery.Node): IssueItem = IssueItem(
                data.author()?.avatarUrl(),
                data.author()?.login(),
                data.id(),
                data.number(),
                data.createdAt(),
                data.title(),
                data.closed()
        )

    }

}