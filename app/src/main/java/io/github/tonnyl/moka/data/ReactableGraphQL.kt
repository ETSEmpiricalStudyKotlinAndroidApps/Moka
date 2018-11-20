package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.IssueQuery
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReactableGraphQL(

        /**
         * Identifies the primary key from the database.
         */
        val databaseId: Int?,

        val id: String,

        /**
         * Can user react to this subject
         */
        val viewerCanReact: Boolean

) : Parcelable {

    companion object {

        fun createFromIssueSubject(data: IssueQuery.Subject): ReactableGraphQL = ReactableGraphQL(
                data.databaseId(),
                data.id(),
                data.viewerCanReact()
        )

    }

}