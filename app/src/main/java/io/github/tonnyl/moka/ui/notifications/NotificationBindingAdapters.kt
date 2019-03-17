package io.github.tonnyl.moka.ui.notifications

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.NotificationReasons

object NotificationBindingAdapters {

    @JvmStatic
    @BindingAdapter(
            value = ["notificationReasonSpan", "notificationReason", "notificationTitle"],
            requireAll = true
    )
    fun notificationContent(
            textView: AppCompatTextView,
            notificationReasonSpan: ForegroundColorSpan,
            notificationReason: String,
            notificationTitle: String
    ) {
        val notificationTypeResId: Int = when (notificationReason) {
            NotificationReasons.ASSIGN.value -> R.string.notification_reason_assign
            NotificationReasons.AUTHOR.value -> R.string.notification_reason_author
            NotificationReasons.COMMENT.value -> R.string.notification_reason_comment
            NotificationReasons.INVITATION.value -> R.string.notification_reason_invitation
            NotificationReasons.MANUAL.value -> R.string.notification_reason_manual
            NotificationReasons.MENTION.value -> R.string.notification_reason_mention
            NotificationReasons.STATE_CHANGE.value -> R.string.notification_reason_state_change
            NotificationReasons.SUBSCRIBED.value -> R.string.notification_reason_subscribed
            // including NotificationReasons.TEAM_MENTION.value
            else -> R.string.notification_reason_team_mention
        }
        val notificationReasonContent = textView.context.getString(notificationTypeResId)
        val notificationReasonPlusHyphen = textView.context.getString(R.string.notification_caption_notification_type, notificationReasonContent)
        val spannable = SpannableString(notificationReasonPlusHyphen + notificationTitle)
        spannable.setSpan(notificationReasonSpan, 0, notificationReasonPlusHyphen.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                spannable,
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

}