package io.github.tonnyl.moka.ui.issue

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.CommentAuthorAssociation

object IssueTimelineBindingAdapters {

    @JvmStatic
    @BindingAdapter("authorAssociation")
    fun authorAssociation(
            textView: AppCompatTextView,
            association: CommentAuthorAssociation?
    ) {
        val stringResId = when (association) {
            CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
            CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
            CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
            CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
            CommentAuthorAssociation.MEMBER -> R.string.author_association_member
            CommentAuthorAssociation.OWNER -> R.string.author_association_owner
            // including CommentAuthorAssociation.NONE and null
            else -> -1
        }

        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                if (stringResId != -1) textView.resources.getString(stringResId) else "",
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

}